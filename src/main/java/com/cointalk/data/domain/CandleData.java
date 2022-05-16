package com.cointalk.data.domain;

import lombok.Getter;
import lombok.Setter;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Getter
@Setter
@Measurement(name = "cointalk")
public class CandleData {

    @Column(name = "time")
    private Instant time;

    @Column(name = "id")
    private String id;

    @Column(name = "close")
    private String close;

    @Column(name = "high")
    private String high;

    @Column(name = "low")
    private String low;

    @Column(name = "open")
    private String open;

    @Column(name = "volume")
    private String volume;

    @Override
    public String toString() {
        return "OneMinuteCandleData{" +
                "time=" + time +
                ", id='" + id + '\'' +
                ", close='" + close + '\'' +
                ", high='" + high + '\'' +
                ", low='" + low + '\'' +
                ", open='" + open + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}
