package com.cointalk.data.service;

import com.cointalk.data.domain.CandleData;
import com.cointalk.data.domain.ChangeCandleData;
import com.cointalk.data.domain.RequestCoin;
import reactor.core.publisher.Mono;

public interface DataLakeService {

    // 빗썸 1분봉 데이터 저장 (스케쥴러)
    void dataCrawling();

    // 특정 코인의 특정시간 데이터 조회
    Mono<ChangeCandleData> getCandleData(RequestCoin requestCoin);
}
