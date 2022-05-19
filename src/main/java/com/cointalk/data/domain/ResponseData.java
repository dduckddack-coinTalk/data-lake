package com.cointalk.data.domain;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {

    private String status;
    private Object message;

    public ResponseData error(String message){
        return new ResponseData("error", message);
    }

}
