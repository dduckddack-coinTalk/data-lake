package com.cointalk.data.handler;

import com.cointalk.data.domain.ChangeCandleData;
import com.cointalk.data.domain.RequestCoinData;
import com.cointalk.data.service.DataLakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CointalkDataHandler {

    private final DataLakeService dataLakeService;

    // 사용자가 요청한 코인의 특정시간 데이터 조회
    public Mono<ServerResponse> getChartData(ServerRequest request) {
        Mono<ChangeCandleData> changeCandleDataMono = request.bodyToMono(RequestCoinData.class)
                .flatMap(dataLakeService::getCandleData)
                .log()
                ;

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(changeCandleDataMono, ChangeCandleData.class)
                .onErrorResume(error -> ServerResponse.badRequest().build())
                ;
    }


}
