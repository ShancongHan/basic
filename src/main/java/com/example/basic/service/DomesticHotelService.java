package com.example.basic.service;

import com.example.basic.dao.ZhJdJdbDao;
import com.example.basic.dao.ZhJdJdbMappingDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    public void checkOldData() {
        List<String> localIds = zhJdJdbDao.selectAllIds();
        List<String> mappingLocalIds = zhJdJdbMappingDao.selectLocalIds();
        Map<String, String> localIdMap = Maps.newHashMapWithExpectedSize(localIds.size());
        for (String localId : localIds) {
            localIdMap.put(localId, localId);
        }
        List<String> notExistIds = Lists.newArrayListWithCapacity(10000);
        for (String mappingLocalId : mappingLocalIds) {
            if (!localIdMap.containsKey(mappingLocalId)) {
                notExistIds.add(mappingLocalId);
            }
        }
        log.info("mapping表中存在表中没有的数据{}条。具体如下：{}",notExistIds.size(), notExistIds);
        /*System.out.println("mapping表中存在表中没有的数据：" + notExistIds.size() + "条。具体如下：");
        System.out.println(notExistIds);*/
    }
}
