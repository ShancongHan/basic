package com.example.basic.dao;

import com.example.basic.domain.HotelExport2;
import com.example.basic.domain.WebbedsHotelExport;
import com.example.basic.domain.WebbedsImportBean;
import com.example.basic.entity.WebbedsHotelData;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/5/28
 */
public interface WebbedsHotelDataDao {

    void saveBatch(List<WebbedsImportBean> webbedsHotelDataList);

    List<WebbedsHotelData> selectAll();

    Set<Integer> selectHotelIdList();

    WebbedsHotelData selectInfo(Integer code);

    List<WebbedsHotelData> selectInfoByIds(List<Integer> dotwHotelCodes);

    List<WebbedsHotelData> selectListByCountry(String countryCode);

    List<String> selectCountryCodes();

    List<String> selectEffectIds(List<String> webbedsHotelIds);

    WebbedsHotelData selectByHotelId(String webbedsHotelId);

    void updateSaleByIds(List<Long> list);

    List<Long> selectAllIds();

    List<Long> selectSaleIds();

    List<HotelExport2> selectExport();

    List<WebbedsHotelData> selectSimpleAll();

    void updateSale(List<Long> list);

    void updateAlreadyPrice(List<Long> list);

    List<WebbedsHotelExport> selectExport2();
}
