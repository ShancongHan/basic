package com.example.basic.dao;

import com.example.basic.domain.HotelExport;
import com.example.basic.entity.SupplierDongCheng;

import java.util.List;

/**
 * @author han
 * @date 2024/8/22
 */
public interface SupplierDongChengDao {

    void saveBatch(List<SupplierDongCheng> insertList);

    List<String> selectAllHotelIds();

    List<HotelExport> selectDongchengList(List<String> ids);

    List<HotelExport> selectHuazhuList(List<String> ids);

    List<HotelExport> selectJinjiangList(List<String> ids);

    List<HotelExport> selectElongList(List<String> ids);

    List<HotelExport> selectMeituans();
}
