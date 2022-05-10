package com.cointalk.data.service;

import com.cointalk.data.domain.BitData;
import com.cointalk.data.domain.BtcData;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DataLakeServiceImpl implements DataLakeService {

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate;

    @Override
    public void dataCrawling() {
        WebClient client = WebClient.create("https://api.bithumb.com/public/candlestick");

        Mono<BitData> btc1 = client.get()
                .uri("/BTC_KRW/1m")
                .retrieve()
                .bodyToMono(BitData.class)
                .doOnNext(data-> test2(data.getData(), "BTC"))
                .publishOn(Schedulers.parallel())
                .log()
                ;

        Mono<BitData> eth1 = client.get()
                .uri("/ETH_KRW/1m")
                .retrieve()
                .bodyToMono(BitData.class)
                .doOnNext(data-> test2(data.getData(), "ETH"))
                .publishOn(Schedulers.parallel())
                .log()
                ;

        Mono<BitData> xrp = client.get()
                .uri("/XRP_KRW/1m")
                .retrieve()
                .bodyToMono(BitData.class)
                .doOnNext(data-> test2(data.getData(), "XRP"))
                .publishOn(Schedulers.parallel())
                .log()
                ;

        Flux.merge(btc1, eth1, xrp)
                .publishOn(Schedulers.parallel())
                .blockLast();
    }

    @Override
    public Mono<BtcData> selectOneData(String measurement) {
        return null;
    }

    private void test2(List<List<Object>> param, String coinName) {
        Collections.reverse(param);
        for (int i = 0; i < 10; i++) { // N개씩 만 처리
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
