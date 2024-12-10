package com.example.basic.domain.hsjl.resp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 表示整个响应的类，对应最外层的结构
@Setter
@Getter
public class HsjlCityResp extends HsjlResponse {

  /** 业务响应数据，包含省份及城市等相关信息 */
  private BussinessResponse bussinessResponse;

  // 内部类，对应 "bussinessResponse" 节点的结构
  @Data
  public static class BussinessResponse {
    /** 省份信息列表，包含每个省份及其下属城市等详情 */
    private List<Province> provinces;
  }

  // 省份相关信息的类，对应JSON中每个省份的结构
  @Data
  public static class Province {
    /** 该省份下包含的城市信息列表 */
    private List<City> citys;

    /** 国家代码，如 "CN" 表示中国 */
    private String countryCode;

    /** 国家名称，如 "中国" */
    private String countryName;

    /** 国家英文名称，如 "CHINA" */
    private String countryEngName;

    /** 省份代码，例如 "BJ" 表示北京省份 */
    private String provinceCode;

    /** 省份名称，例如 "北京" */
    private String provinceName;

    /** 省份英文名称，例如 "beijing" */
    private String provinceEngName;
  }

  // 城市相关信息的类，对应JSON中每个城市的结构
  @Data
  public static class City {
    /** 城市代码，例如 "PEK" 表示北京城市 */
    private String cityCode;

    /** 城市名称，例如 "北京" */
    private String cityName;

    /** 城市英文名称（部分城市有该项，有的可能为空），例如 "Beijing Peking" */
    private String areaEngName;

    /** 上级城市代码（如果有，像都江堰有对应的上级成都的代码，有的城市可能为空） */
    private String parentCityCode;

    /** 上级城市名称（如果有，有的城市可能为空） */
    private String parentCityName;

    /** 上级城市英文名称（如果有，有的城市可能为空） */
    private String parentCityEngName;
  }
}
