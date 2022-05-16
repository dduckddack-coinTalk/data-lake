package com.cointalk.data;

import com.cointalk.data.domain.*;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
class CointalkDataApplicationTests {

	@Autowired
	private InfluxDBTemplate<Point> influxDBTemplate;

	private ChangeCandleData result = new ChangeCandleData();

	@Test
	void contextLoads2() {

		WebClient client = WebClient.create("https://api.bithumb.com/public/candlestick");

		Mono<BithumbCoinData> btc1 = client.get()
				.uri("/BTC_KRW/1m")
				.retrieve()
				.bodyToMono(BithumbCoinData.class)
				.doOnNext(data-> test2(data.getData(), "BTC"))
				.publishOn(Schedulers.parallel())
				.log()
				;

		Mono<BithumbCoinData> eth1 = client.get()
				.uri("/ETH_KRW/1m")
				.retrieve()
				.bodyToMono(BithumbCoinData.class)
				.doOnNext(data-> test2(data.getData(), "ETH"))
				.publishOn(Schedulers.parallel())
				.log()
				;

		Mono<BithumbCoinData> xrp = client.get()
				.uri("/XRP_KRW/1m")
				.retrieve()
				.bodyToMono(BithumbCoinData.class)
				.doOnNext(data-> test2(data.getData(), "XRP"))
				.publishOn(Schedulers.parallel())
				.log()
				;

		Flux.merge(btc1, eth1, xrp)
				.publishOn(Schedulers.parallel())
				.blockLast();
	}

	private void test2(List<List<Object>> param, String coinName){
		Collections.reverse(param); // 받아온 캔들차트 데이터의 뒷 부분 (최신 시간) 부터 처리
		for(int i = 0; i< 10; i++){ // N개씩 만 처리
			Point point = Point.measurement("cointalk")
					.time((Number) param.get(i).get(0), TimeUnit.MILLISECONDS)
					.tag("id", coinName)
					.addField("open", (String) param.get(i).get(1))
					.addField("close",  (String) param.get(i).get(2))
					.addField("high",  (String) param.get(i).get(3))
					.addField("low",  (String) param.get(i).get(4))
					.addField("volume",  (String) param.get(i).get(5))
					.build();
			influxDBTemplate.write(point);
		}

	}
	// 저장된 데이터 select
	@Test
	void select_data() {

		String q = "SELECT * FROM cointalk where id='XRP' and (time>1652080680000000000 and time<1652080860000000000) order by desc limit 10";

		Long t = 1652080680000000000L;

		Query query = BoundParameterQuery.QueryBuilder.newQuery(q)
				.forDatabase("coin")
				.create();

		QueryResult queryResult = influxDBTemplate.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<CandleData> testMeasurementList = resultMapper.toPOJO(queryResult, CandleData.class);

		for (CandleData tm : testMeasurementList) {
			System.out.println(tm.toString());
		}

	}

	// 저장된 데이터 가공해서 뿌려줌
	@Test
	void changeDBData() {
		String coin = "XRP";
		String time = "1652374380000000000";

		String q = "SELECT * FROM cointalk where id="+"'"+coin+"'"+" and time >"+time+" -3m";

		Long t = 1652080680000000000L;

		Query query = BoundParameterQuery.QueryBuilder.newQuery(q)
				.forDatabase("coin")
				.create();

		QueryResult queryResult = influxDBTemplate.query(query);

		InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
		List<CandleData> testMeasurementList = resultMapper.toPOJO(queryResult, CandleData.class);

		Mono<ChangeCandleData> tt2 = Mono.just(resultMapper.toPOJO(queryResult, CandleData.class))
				.map(this::change_test)
				.log()
				;

		tt2.subscribe();

	}

	private ChangeCandleData change_test(List<CandleData> oneMinuteCandleData){
		ChangeCandleData result = new ChangeCandleData();
		List<Object> t = new ArrayList<>();
		List<Object> o = new ArrayList<>();
		List<Object> h = new ArrayList<>();
		List<Object> c = new ArrayList<>();
		List<Object> l = new ArrayList<>();
		List<Object> v = new ArrayList<>();

		for(CandleData tm : oneMinuteCandleData){
			t.add(tm.getTime().toEpochMilli());
			o.add(tm.getOpen());
			h.add(tm.getHigh());
			c.add(tm.getClose());
			l.add(tm.getLow());
			v.add(tm.getVolume());
		}
		ChangeCandleInnerData b = new ChangeCandleInnerData(t,o,h,c,l,v);

		result.setStatus("0000");
		result.setData(b);

		return result;
	}

	private ChangeCandleData change_test2(CandleData oneMinuteCandleData){

		List<Object> t = new ArrayList<>();
		List<Object> o = new ArrayList<>();
		List<Object> h = new ArrayList<>();
		List<Object> c = new ArrayList<>();
		List<Object> l = new ArrayList<>();
		List<Object> v = new ArrayList<>();

		ChangeCandleInnerData b = new ChangeCandleInnerData(t,o,h,c,l,v);

		result.setStatus("0000");
		result.setData(b);

		return result;
	}

}
