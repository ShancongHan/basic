package com.example.basic.domain;

import com.example.basic.annotation.FastXmlBean;
import lombok.Data;

import java.util.List;

/**
 * 行政区域
 * @author han
 * @date 2024/4/12
 */
@Data
public class AdministrativeArea {

    /**
     * 节点列表
     */
    @FastXmlBean(start = "Por", selfClose = true, multi = true)
    private List<Por> porList;
}
