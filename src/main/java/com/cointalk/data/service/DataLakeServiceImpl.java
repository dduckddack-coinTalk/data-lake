package com.cointalk.data.service;

import com.cointalk.data.domain.*;
import com.cointalk.data.kafka.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDBException;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataLakeServiceImpl implements DataLakeService {

    private final InfluxDBTemplate<Point> influxDBTemplate;
    private final Producer producer;

    @Override
    public void dataCrawling(String chartIntervals) {

        // 버퍼초과 DataBufferLimitException 문제로 데이터 버퍼링 제한 10M 까지 늘림.
        WebClient webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize(2 * 1024 * 1024))
                .baseUrl("https://api.bithumb.com/public/candlestick")
                .build();

//        WebClient client = WebClient.create("https://api.bithumb.com/public/candlestick");

        log.info("data Crawling.. Chart Intervals : "+chartIntervals);

        String [] coinList = new String[]{"ETH","XRP","BTC"}; // aws 프리티어 용량문제로 3개 코인만 저장
        Flux<BithumbCandleData> bithumbCoinDataFlux = Flux.range(0, coinList.length)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(t -> webClient.get()
                        .uri("/"+coinList[t] + "_KRW/" + chartIntervals)
                        .retrieve()
                        .bodyToMono(BithumbCandleData.class)
                        .doOnNext(data-> insertCandleData(data.getData(), coinList[t], chartIntervals))
                )
                ;
        bithumbCoinDataFlux.subscribe();

    }

    @Override
    public Mono<ResponseData> getCandleData(GetCandleData requestCoinData) {
        String coin = requestCoinData.getCoin();
        String time = requestCoinData.getTime();
        String addTime = "000000"; // influxDB가 Microsecond 단위까지 계산하기 때문에 클라이언트의 요청과 자리수 맞춰줌
        String chartIntervals = StringUtils.defaultIfEmpty(requestCoinData.getChartIntervals(), "1m");
        if(chartIntervals != null && !chartIntervals.trim().isEmpty()){ // 차트 시간 간격값 default 1m
            chartIntervals = "1m";
        }
        String timeCount = String.valueOf(Integer.parseInt(String.valueOf(chartIntervals.charAt(0))) * 1500); // 조회해야할 분봉 갯수를 카운트. 분단위로 계산되므로 클라이언트 요청 시간단위 x1500
        if(chartIntervals.equals("1h")){
            timeCount = String.valueOf(60 * 1500);
        }
        log.info(MessageFormat.format("getCandleData | coin : {0}  time : {1}  ChartIntervals : {2}", coin, time, chartIntervals));

        String q = "SELECT * FROM cointalk " +
                "WHERE coin='"+coin+"' " +
                "AND chartIntervals='"+chartIntervals+"' " +
                "AND time >"+time+addTime+" -"+timeCount+"m";

        Query query = BoundParameterQuery.QueryBuilder.newQuery(q)
                .forDatabase("coin")
                .create();

        QueryResult queryResult = influxDBTemplate.query(query);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

        return Mono.just(resultMapper.toPOJO(queryResult, CandleData.class))
                .map(this::changeCandleData)
                .onErrorReturn(new ResponseData().error("캔들 데이터 조회 에러"))
                .log()
                ;

    }

    // influxDB에 저장된 데이터를 클라이언트가 사용할 수 있게 빗썸 캔들차트 조회 API 양식에 맞춰 재구성.
    private ResponseData changeCandleData(List<CandleData> candleDataList){
        ResponseData result = new ResponseData();
        try{
            List<Object> t = new ArrayList<>();
            List<Object> o = new ArrayList<>();
            List<Object> h = new ArrayList<>();
            List<Object> c = new ArrayList<>();
            List<Object> l = new ArrayList<>();
            List<Object> v = new ArrayList<>();

            for(CandleData candleData : candleDataList){
                t.add(candleData.getTime().toEpochMilli()); // influxDB에서 받아온 형식을 timestamp 로 변환
                o.add(candleData.getOpen());
                h.add(candleData.getHigh());
                c.add(candleData.getClose());
                l.add(candleData.getLow());
                v.add(candleData.getVolume());
            }
            ChangeCandleInnerData changeCandleInnerData = new ChangeCandleInnerData(t,o,h,c,l,v);

            result.setStatus("0000");
            result.setMessage(changeCandleInnerData);
        } catch (InfluxDBException e){
            producer.sendMessageToSlack("");
            result.error("InfluxDB 에러");
            e.printStackTrace();
        } catch (Exception e){
            result.error("기타 에러");
            e.printStackTrace();
        }

        return result;
    }

    private void insertCandleData(List<List<Object>> param, String coinName, String chartIntervals){
        log.info("insertCandleData : "+ coinName);

        try{

            Collections.reverse(param);

            // N개씩 만 저장
            Flux.range(0, 500)
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap(i-> {
                        Point point = Point.measurement("cointalk")
                                .time((Number) param.get(i).get(0), TimeUnit.MICROSECONDS)
                                .tag("coin", coinName)
                                .tag("chartIntervals", chartIntervals)
                                .addField("open", (String) param.get(i).get(1))
                                .addField("close", (String) param.get(i).get(2))
                                .addField("high", (String) param.get(i).get(3))
                                .addField("low", (String) param.get(i).get(4))
                                .addField("volume", (String) param.get(i).get(5))
                                .build();
                        influxDBTemplate.write(point);
                        return Flux.empty();
                    })
                    .subscribe()
                    ;

//            }
        }catch (InfluxDBException e){
            e.printStackTrace();
        }

    }
}
