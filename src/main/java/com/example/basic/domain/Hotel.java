package com.example.basic.domain;

import lombok.Data;

/**
 * @author han
 * @date 2024/5/6
 */
@Data
public class Hotel {

    private String col1;
    private String col2;
    private String col3;
    private String col4;
    private String col5;
    private String col6;
    private String col7;
    private String col8;
    private String col9;
    private String col10;
    private String col11;
    private String col12;
    private String col13;
    private String col14;
    private String col15;

    @Override
    public String toString() {
        return col1 + col2 + col3 + col4 + col5 + col6 + col7 + col8 + col9 + col10 + col11 + col12 + col13 + col14 + col15;
    }
}
