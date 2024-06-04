package com.example.basic.domain;

import com.example.basic.annotation.FastXmlBean;
import lombok.Data;

/**
 * @author han
 * @date 2024/4/12
 */
@Data
public class City {

    /**
     * 城市三字码
     */
    @FastXmlBean(element = "CityPor", attribute = "CityCode")
    private String cityCode;

    /**
     * 国家二字码
     */
    @FastXmlBean(element = "CityPor", attribute = "CountryCode")
    private String countryCode;

    /**
     * 城市名称
     */
    @FastXmlBean(element = "CityPor", attribute = "CityName")
    private String cityName;

    /**
     * 城市所在省名称
     */
    @FastXmlBean(element = "CityPor", attribute = "Province")
    private String province;

    /**
     * 行政区域集合
     */
    @FastXmlBean(start = "AdministrativeArea", stop = "AdministrativeArea", subElement = true)
    private AdministrativeArea administrativeArea;

}
