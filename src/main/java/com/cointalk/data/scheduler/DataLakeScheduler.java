package com.cointalk.data.scheduler;

import com.cointalk.data.service.DataLakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLakeScheduler {

    private final DataLakeService dataLakeService;

    // 빗썸 1분봉 데이터 저장
    @Scheduled(fixedDelay = 30000, initialDelay = 1000)
    public void dataCrawling() {
        dataLakeService.dataCrawling();
    }

}
