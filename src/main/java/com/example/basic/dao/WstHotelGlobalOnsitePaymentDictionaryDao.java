package com.example.basic.dao;


import com.example.basic.entity.WstHotelGlobalOnsitePaymentDictionary;

import java.util.List;

/**
 * @author han
 * @date 2024/12/24
 */
public interface WstHotelGlobalOnsitePaymentDictionaryDao {
    void saveBatch(List<WstHotelGlobalOnsitePaymentDictionary> list);
    void updateByName(WstHotelGlobalOnsitePaymentDictionary dictionary);
}
