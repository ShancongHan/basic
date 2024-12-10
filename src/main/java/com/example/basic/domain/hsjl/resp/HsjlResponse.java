package com.example.basic.domain.hsjl.resp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class HsjlResponse {

  /** 返回码，用于标识请求处理结果的状态码，例如 "000"表示成功 */
  private String returnCode;

  /** 返回消息，对请求处理结果的文字描述，例如 "成功" */
  private String returnMsg;
}
