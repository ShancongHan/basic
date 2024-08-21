package com.example.basic.service;

import com.example.basic.dao.JdJdbDao;
import com.example.basic.dao.ZhJdJdbDao;
import com.example.basic.dao.ZhJdJdbMappingDao;
import com.example.basic.entity.WebbedsDaolvMatchLab;
import com.example.basic.entity.ZhJdJdbMapping;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/8/16
 */
@Slf4j
@Service
public class DomesticHotelService {

    @Resource
    private ZhJdJdbDao zhJdJdbDao;

    @Resource
    private ZhJdJdbMappingDao zhJdJdbMappingDao;

    @Resource
    private JdJdbDao jdJdbDao;

    public void checkOldData() {
        List<String> jdIds = jdJdbDao.selectAllIds();
        List<String> mappingLocalIds = null;//zhJdJdbMappingDao.selectLocalIds();
        Map<String, String> localIdMap = Maps.newHashMapWithExpectedSize(jdIds.size());
        for (String localId : jdIds) {
            localIdMap.put(localId, localId);
        }
        List<String> notExistIds = Lists.newArrayListWithCapacity(2000000);
        for (String mappingLocalId : mappingLocalIds) {
            if (!localIdMap.containsKey(mappingLocalId)) {
                notExistIds.add(mappingLocalId);
            }
        }
        log.info("总表中存在分表中没有的数据{}条.", notExistIds.size());
        for (String notExistId : notExistIds) {
            zhJdJdbMappingDao.deleteByLocalId(notExistId);
        }
    }

    public void checkBasicData() {
        List<String> jdIds = jdJdbDao.selectAllIds();
        List<ZhJdJdbMapping> mappingList = zhJdJdbMappingDao.selectLocalIds();
        Map<String, Long> localIdMap = mappingList.stream().collect(Collectors.toMap(ZhJdJdbMapping::getLocalId, ZhJdJdbMapping::getId, (v1, v2) -> v1));
        List<String> notUseList = Lists.newArrayListWithCapacity(10000);
        for (String jdId : jdIds) {
            if (!localIdMap.containsKey(jdId)) {
                notUseList.add(jdId);
            }
        }
        log.info("总表中存在分表中没有的数据{}条.分别是{}", notUseList.size(), notUseList);
        deleteBatch(notUseList);
    }

    private void deleteBatch(List<String> deleteList) {
        int start = 0;
        for (int j = 0; j < deleteList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = deleteList.subList(start, j);
                jdJdbDao.deleteBatch(list);
                start = j;
            }
        }
        List<String> list = deleteList.subList(start, deleteList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbDao.deleteBatch(list);
        }
    }
}
