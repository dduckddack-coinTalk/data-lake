package com.cointalk.data.service;

import com.cointalk.data.domain.ChangeCandleData;
import com.cointalk.data.domain.RequestCoinData;
import reactor.core.publisher.Mono;

public interface DataLakeService {

    // 빗썸 분봉 데이터 저장 (스케쥴러)
    void dataCrawling(String chartIntervals);

    // 사용자가 요청한 코인의 특정시간 데이터 조회
    Mono<ChangeCandleData> getCandleData(RequestCoinData requestCoinData);
}
