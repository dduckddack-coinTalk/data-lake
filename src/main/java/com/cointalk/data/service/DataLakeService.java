package com.cointalk.data.service;

import com.cointalk.data.domain.BtcData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

public interface DataLakeService {

    // 빗썸 1분봉 데이터 저장 (스케쥴러)
    void dataCrawling();

    // 데이터 한개 가져오기
    Mono<BtcData> selectOneData(String measurement);

}
