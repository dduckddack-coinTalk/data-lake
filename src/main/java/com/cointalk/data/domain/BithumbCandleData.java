package com.cointalk.data.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BithumbCandleData {

    private String status;

    private List<List<Object>> data;

}
