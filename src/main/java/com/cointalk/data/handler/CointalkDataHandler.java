package com.cointalk.data.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CointalkDataHandler {

//    private final CointalkDataService cointalkDataService;
//
//    // 강의개설
//    public Mono<ServerResponse> createLecture(ServerRequest request) {
//        Mono<BtcData> lectureMono = request.bodyToMono(BtcData.class)
//                .flatMap(cointalkDataService::selectOneData)
//                .log()
//                ;
//
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(lectureMono, BtcData.class)
//                .onErrorResume(error -> ServerResponse.badRequest().build())
//                ;
//    }


}
