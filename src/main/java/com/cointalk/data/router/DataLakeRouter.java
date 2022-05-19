package com.cointalk.data.router;

import com.cointalk.data.handler.DataLakeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration(proxyBeanMethods = false)
public class DataLakeRouter {

    @Bean
    public RouterFunction<ServerResponse> route(DataLakeHandler cointalkDataHandler) {
        return RouterFunctions
                .route(POST("/dataLake/getCandleData"), cointalkDataHandler::getChartData) // 사용자가 요청한 코인의 특정시간 데이터 조회
                ;
    }

}
