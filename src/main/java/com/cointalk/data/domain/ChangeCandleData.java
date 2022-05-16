package com.cointalk.data.domain;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCandleData {

    private String status;
    private ChangeCandleInnerData data;

}
