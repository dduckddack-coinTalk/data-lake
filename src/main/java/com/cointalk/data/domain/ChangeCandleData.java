package com.cointalk.data.domain;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCandleData {

    private String status;
    private TestChangeCandleData data;

}
