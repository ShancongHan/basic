package com.example.basic.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DongChengGnResponse {

    @JsonProperty(value = "Data")
    private String data;//业务数据

    @JsonProperty(value = "Code")
    private Integer code;//结果码：1为请求成功，0为请求失败

    @JsonProperty(value = "Msg")
    private String msg;//信息

    @JsonProperty(value = "TotalCount")
    private Integer totalCount;//分页需要，总数据量

    @JsonProperty(value = "PageSize")
    private Integer pageSize;//页大小

    @JsonProperty(value = "PageIndex")
    private Integer pageIndex;//第几页

    @JsonProperty(value = "RequestId")
    private String requestId;//本次请求ID



}
