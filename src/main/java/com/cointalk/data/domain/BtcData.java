package com.cointalk.data.domain;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.time.Instant;

@Measurement(name = "upbit_KRW_BTC_1d")
public class BtcData {

    @Column(name = "time")
    private Instant time;

    @Column(name = "close")
    private int close;

    @Column(name = "high")
    private int high;

    @Column(name = "low")
    private int low;

    @Column(name = "open")
    private int open;

    @Column(name = "value")
    private int value;

    @Column(name = "volume")
    private int volume;

    @Override
    public String toString() {
        return "BtcData{" +
                "time=" + time +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", value=" + value +
                ", volume=" + volume +
                '}';
    }
}
