package com.study.signuplogin.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // Object 를 응답할 때 Null 인 필드가 있다면 JSON 으로 파싱할 때 넣지 않는다.
public class CommonResponse {
    private String msg;
    private Integer statusCode;

    public CommonResponse(String msg, int statusCode) {
        this.msg = msg;
        this.statusCode = statusCode;
    }
}