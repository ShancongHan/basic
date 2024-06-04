package com.example.basic.domain;

import com.example.basic.annotation.FastXmlBean;
import lombok.Data;

/**
 * 最小节点
 * @author han
 * @date 2024/4/12
 */
@Data
public class Por {

    /**
     * id
     */
    @FastXmlBean(element = "Por", attribute = "Id")
    private String id;

    /**
     * 名字
     */
    @FastXmlBean(element = "Por", attribute = "Name")
    private String name;

    /**
     * 经度
     */
    @FastXmlBean(element = "Por", attribute = "Longitude")
    private String longitude;

    /**
     * 纬度
     */
    @FastXmlBean(element = "Por", attribute = "Latitude")
    private String latitude;
}
