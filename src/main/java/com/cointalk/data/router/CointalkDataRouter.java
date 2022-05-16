package com.cointalk.data.router;

import com.cointalk.data.handler.CointalkDataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration(proxyBeanMethods = false)
public class CointalkDataRouter {

    @Bean
    public RouterFunction<ServerResponse> route(CointalkDataHandler cointalkDataHandler) {
        return RouterFunctions
                .route(POST("/dataLake/getCandleData"), cointalkDataHandler::getOneMinuteData) // 코인데이터 받아오기
                ;
    }

}
