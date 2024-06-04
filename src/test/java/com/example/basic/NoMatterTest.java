package com.example.basic;

import com.example.basic.helper.MappingScoreHelper;

import java.math.BigDecimal;

/**
 * @author han
 * @date 2024/5/23
 */
public class NoMatterTest {

    public static void main(String[] args) {

        MappingScoreHelper.calculateScore("Hotel Beni Hamad","36.068446"
                ,"4.765295","01, Rue Frantz Fanon Bordj Bou Arreridj Algérie","00213-770521828"
                ,"Bodrum Park Resort",new BigDecimal("36.982981")
                ,new BigDecimal("7.561266"),"塞赖迪Yalıciftlik Mevkii 48410 Bodrum Turkey, 阿尔及利亚","0");
    }
}
