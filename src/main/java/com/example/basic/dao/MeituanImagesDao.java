package com.example.basic.dao;

import com.example.basic.entity.MeituanImages;

import java.util.List;
import java.util.Set;

/**
 * @author han
 * @date 2024/11/18
 */
public interface MeituanImagesDao {
    List<MeituanImages> selectLiatByIds(Set<Long> mtIds);
}
