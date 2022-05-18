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

	}

