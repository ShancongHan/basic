package com.example.basic.dao;

import com.example.basic.domain.MainHotelImport;
import com.example.basic.entity.ExpediaContentBasic;

import java.util.List;

/**
 * @author han
 * @date 2024/9/5
 */
public interface ExpediaContentBasicDao {

    void saveBatch(List<ExpediaContentBasic> insertList);

    List<ExpediaContentBasic> selectNeedUpdateHotelIds();

    void update(ExpediaContentBasic expediaContentBasic);

    List<ExpediaContentBasic> selectNeedPriceHotelIds();

    void updatePrice(ExpediaContentBasic update);

    void updateV1Sale(List<String> list);

    void updateMainPrice(List<MainHotelImport> list);

    void updateMatch(List<String> list);

    List<String> selectAllHotelIds();

    void updateByHotelId(ExpediaContentBasic update);

    void updatePriceByHotelId(ExpediaContentBasic update);

    List<ExpediaContentBasic> selectMatchList();
    List<ExpediaContentBasic> selectNotMatchList();

    List<ExpediaContentBasic> selectSalingList();
}
