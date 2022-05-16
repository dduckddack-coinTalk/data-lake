package com.cointalk.data.service;

import com.cointalk.data.domain.*;
import lombok.RequiredArgsConstructor;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DataLakeServiceImpl implements DataLakeService {

    private final InfluxDBTemplate<Point> influxDBTemplate;

    @Override
    public void dataCrawling() {
        WebClient client = WebClient.create("https://api.bithumb.com/public/candlestick");

        Mono<BithumbCoinData> btc1 = client.get()
                .uri("/BTC_KRW/1m")
                .retrieve()
                .bodyToMono(BithumbCoinData.class)
                .doOnNext(data-> insertCandleData(data.getData(), "BTC"))
                .publishOn(Schedulers.parallel())
                ;

        Mono<BithumbCoinData> eth1 = client.get()
                .uri("/ETH_KRW/1m")
                .retrieve()
                .bodyToMono(BithumbCoinData.class)
                .doOnNext(data-> insertCandleData(data.getData(), "ETH"))
                .publishOn(Schedulers.parallel())
                ;

        Mono<BithumbCoinData> xrp = client.get()
                .uri("/XRP_KRW/1m")
                .retrieve()
                .bodyToMono(BithumbCoinData.class)
                .doOnNext(data-> insertCandleData(data.getData(), "XRP"))
                .publishOn(Schedulers.parallel())
                ;

        Flux.merge(btc1, eth1, xrp)
                .publishOn(Schedulers.parallel())
                .blockLast();
    }

    @Override
    public Mono<ChangeCandleData> getCandleData(RequestCoin requestCoin) {
        String coin = requestCoin.getCoin();
        String time = requestCoin.getTime();
        String addTime = "000000"; // influxdb는 Microsecond 단위까지 요구하기 때문에 자리수 맞춰줌
        // String timeType = requestCoin.getTimeType(); 1d, 5m 등 time type 로직 차후 구현

        String q = "SELECT * FROM cointalk where id="+"'"+coin+"'"+" and time >"+time+addTime+" -1500m";

        Query query = BoundParameterQuery.QueryBuilder.newQuery(q)
                .forDatabase("coin")
                .create();

        QueryResult queryResult = influxDBTemplate.query(query);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();

        return Mono.just(resultMapper.toPOJO(queryResult, CandleData.class))
                .map(this::changeCandleData);
    }

    private ChangeCandleData changeCandleData(List<CandleData> oneMinuteCandleData){
        ChangeCandleData result = new ChangeCandleData();
        List<Object> t = new ArrayList<>();
        List<Object> o = new ArrayList<>();
        List<Object> h = new ArrayList<>();
        List<Object> c = new ArrayList<>();
        List<Object> l = new ArrayList<>();
        List<Object> v = new ArrayList<>();

        for(CandleData candleData : oneMinuteCandleData){
            t.add(candleData.getTime().toEpochMilli()); // influxDB에서 받아온 시간에서 프론트에서 사용가능한 timestamp 형식으로 변환
            o.add(candleData.getOpen());
            h.add(candleData.getHigh());
            c.add(candleData.getClose());
            l.add(candleData.getLow());
            v.add(candleData.getVolume());
        }
        ChangeCandleInnerData changeCandleInnerData = new ChangeCandleInnerData(t,o,h,c,l,v);

        result.setStatus("0000");
        result.setData(changeCandleInnerData);

        return result;
    }

    private void insertCandleData(List<List<Object>> param, String coinName) {
        Collections.reverse(param);
        for (int i = 0; i < 50; i++) { // N개씩 만 처리
            Point point = Point.measurement("cointalk")
                    .time((Number) param.get(i).get(0), TimeUnit.MILLISECONDS)
                    .tag("id", coinName)
                    .addField("open", (String) param.get(i).get(1))
                    .addField("close", (String) param.get(i).get(2))
                    .addField("high", (String) param.get(i).get(3))
                    .addField("low", (String) param.get(i).get(4))
                    .addField("volume", (String) param.get(i).get(5))
                    .build();
            influxDBTemplate.write(point);
        }
    }
}
