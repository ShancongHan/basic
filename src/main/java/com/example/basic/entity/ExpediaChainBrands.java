package com.example.basic.entity;

import lombok.Data;

/**
 * @author han
 * @date 2024/8/7
 */
@Data
public class ExpediaChainBrands {
    private Long id;	// id

    private String chainId;	// 连锁id

    private String name;	// 名称

    private String brandsId;	// 品牌id,分割

    private String brandsName;	// 品牌名称,分割
}
