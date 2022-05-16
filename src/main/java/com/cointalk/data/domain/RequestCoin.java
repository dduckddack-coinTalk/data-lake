package com.cointalk.data.domain;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestCoin {

    private String coin;

    private String time;

    private String timeType;
}
