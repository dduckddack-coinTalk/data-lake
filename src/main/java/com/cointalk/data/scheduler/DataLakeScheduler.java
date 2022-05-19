package com.cointalk.data.scheduler;

import com.cointalk.data.service.DataLakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLakeScheduler {

    private final DataLakeService dataLakeService;

    // 빗썸 1분봉 데이터 저장 30초 마다 실행
    @Scheduled(cron ="*/30 * * * * *")
    public void dataCrawling1m() {
        dataLakeService.dataCrawling("1m");
    }

    // 빗썸 10분봉 데이터 저장 5분 마다 실행
    @Scheduled(cron ="0 */5 * * * *")
    public void dataCrawling10m() {
        dataLakeService.dataCrawling("10m");
    }

    // 빗썸 30분봉 데이터 저장 15분 마다 실행
    @Scheduled(cron ="0 */15 * * * *")
    public void dataCrawling30m() {
        dataLakeService.dataCrawling("30m");
    }

    // 빗썸 60분봉 데이터 저장 30분 마다 실행
    @Scheduled(cron ="0 */30 * * * *")
    public void dataCrawling60m() {
        dataLakeService.dataCrawling("1h");
    }

}
