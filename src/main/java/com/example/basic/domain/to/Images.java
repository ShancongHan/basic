package com.example.basic.domain.to;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/1
 */
@Data
public class Images {

    /**
     * 图片标题
     */
    private String caption;

    /**
     * 是否是封面
     */
    private boolean hero_image;

    /**
     * 分类id
     */
    private int category;

    /**
     * 图片链接，各种尺寸
     */
    private String links;
}
