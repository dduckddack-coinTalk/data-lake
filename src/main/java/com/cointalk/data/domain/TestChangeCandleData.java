package com.cointalk.data.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TestChangeCandleData {

    private List<Object> t;

    private List<Object> o;

    private List<Object> h;

    private List<Object> c;

    private List<Object> l;

    private List<Object> v;
}
