package com.example.basic.dao;

import com.example.basic.entity.WstHotelImages;

import java.util.List;

/**
 * @author han
 * @date 2024/11/18
 */
public interface WstHotelImagesDao {
    void saveBatch(List<WstHotelImages> wstHotelImages);
}
