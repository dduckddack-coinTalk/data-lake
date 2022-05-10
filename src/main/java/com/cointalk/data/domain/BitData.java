package com.cointalk.data.domain;

import lombok.*;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BitData {

    private String status;

    private List<List<Object>> data;

}
