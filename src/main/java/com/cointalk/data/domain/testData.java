package com.cointalk.data.domain;

import lombok.*;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class testData {

    private TimeUnit timestamp;

    private String open;

    private String close;

    private String high;

    private String low;

    private String volume;
}
