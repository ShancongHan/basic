package com.example.basic.service;

import com.example.basic.dao.*;
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

    @Resource
    private JdJdbElongDao jdJdbElongDao;
    @Resource
    private JdJdbMeituanDao jdJdbMeituanDao;
    @Resource
    private JdJdbQiantaoDao jdJdbQiantaoDao;
    @Resource
    private JdJdbHsjlDao jdJdbHsjlDao;
    @Resource
    private JdJdbGnDaolvDao jdJdbGnDaolvDao;
    @Resource
    private JdJdbHuazhuDao jdJdbHuazhuDao;
    @Resource
    private JdJdbJinjiangDao jdJdbJinjiangDao;
    @Resource
    private JdJdbDongchengDao jdJdbDongchengDao;

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
        //log.info("mapping表中没有但是有基础数据{}条.分别是{}", notUseList.size(), notUseList);
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

    public void checkElongData() {
        List<String> elongIds = zhJdJdbMappingDao.selectElongIds();
        List<String> ids = jdJdbElongDao.selectIds();

        Map<String, String> elongMap = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            elongMap.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        elongIds = elongIds.stream().distinct().collect(Collectors.toList());
        for (String elongId : elongIds) {
            if (!elongMap.containsKey(elongId)) {
                noExistList.add(elongId);
            }
        }
        //log.info("无法找到elongId共{}条.分别是{}", noExistList.size(), noExistList);
        int start = 0;
        for (int j = 0; j < noExistList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = noExistList.subList(start, j);
                zhJdJdbMappingDao.deleteElongBatch(list);
                start = j;
            }
        }
        List<String> list = noExistList.subList(start, noExistList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            zhJdJdbMappingDao.deleteElongBatch(list);
        }
    }

    public void updateElongReflect() {
        List<String> elongIds = zhJdJdbMappingDao.selectElongIds();
        elongIds = elongIds.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < elongIds.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = elongIds.subList(start, j);
                jdJdbElongDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = elongIds.subList(start, elongIds.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbElongDao.updateBatch(list);
        }
    }

    public void checkMeituanData() {
        List<String> meituanIds = zhJdJdbMappingDao.selectMeituanIds();
        List<String> ids = jdJdbMeituanDao.selectIds();

        Map<String, String> map = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            map.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        meituanIds = meituanIds.stream().distinct().collect(Collectors.toList());
        for (String elongId : meituanIds) {
            if (!map.containsKey(elongId)) {
                noExistList.add(elongId);
            }
        }
        log.info("无法找到meituanId共{}条.分别是{}", noExistList.size(), noExistList);
    }

    public void updateMeituanReflect() {
        List<String> meituanIds = zhJdJdbMappingDao.selectMeituanIds();
        meituanIds = meituanIds.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < meituanIds.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = meituanIds.subList(start, j);
                jdJdbMeituanDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = meituanIds.subList(start, meituanIds.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbMeituanDao.updateBatch(list);
        }
    }

    public void checkQiantaoData() {
        List<String> qiantaoIds = zhJdJdbMappingDao.selectQiantaoIds();
        List<String> ids = jdJdbQiantaoDao.selectIds();

        Map<String, String> map = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            map.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        qiantaoIds = qiantaoIds.stream().distinct().collect(Collectors.toList());
        for (String id : qiantaoIds) {
            if (!map.containsKey(id)) {
                noExistList.add(id);
            }
        }
        log.info("无法找到QiantaoId共{}条.分别是{}", noExistList.size(), noExistList);
    }

    public void updateQiantaoReflect() {
        List<String> Qiantao = zhJdJdbMappingDao.selectQiantaoIds();
        Qiantao = Qiantao.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < Qiantao.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = Qiantao.subList(start, j);
                jdJdbQiantaoDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = Qiantao.subList(start, Qiantao.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbQiantaoDao.updateBatch(list);
        }
    }

    public void checkHsjlData() {
        List<String> hsjlIds = zhJdJdbMappingDao.selectHsjlIds();
        List<String> ids = jdJdbHsjlDao.selectIds();

        Map<String, String> map = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            map.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        hsjlIds = hsjlIds.stream().distinct().collect(Collectors.toList());
        for (String id : hsjlIds) {
            if (!map.containsKey(id)) {
                noExistList.add(id);
            }
        }
        //log.info("无法找到hsjlId共{}条.分别是{}", noExistList.size(), noExistList);
        int start = 0;
        for (int j = 0; j < noExistList.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = noExistList.subList(start, j);
                zhJdJdbMappingDao.deleteHsjlBatch(list);
                start = j;
            }
        }
        List<String> list = noExistList.subList(start, noExistList.size());
        if (CollectionUtils.isNotEmpty(list)) {
            zhJdJdbMappingDao.deleteHsjlBatch(list);
        }
    }

    public void updateHsjlReflect() {
        List<String> ids = zhJdJdbMappingDao.selectHsjlIds();
        ids = ids.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < ids.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = ids.subList(start, j);
                jdJdbHsjlDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = ids.subList(start, ids.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbHsjlDao.updateBatch(list);
        }
    }

    public void checkDaolvData() {
        List<String> daolvIds = zhJdJdbMappingDao.selectDaolvIds();
        List<String> ids = jdJdbGnDaolvDao.selectIds();

        Map<String, String> map = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            map.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        daolvIds = daolvIds.stream().distinct().collect(Collectors.toList());
        for (String id : daolvIds) {
            if (!map.containsKey(id)) {
                noExistList.add(id);
            }
        }
        log.info("无法找到daolvId共{}条.分别是{}", noExistList.size(), noExistList);
    }

    public void updateDaolvReflect() {
        List<String> ids = zhJdJdbMappingDao.selectDaolvIds();
        ids = ids.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < ids.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = ids.subList(start, j);
                jdJdbGnDaolvDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = ids.subList(start, ids.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbGnDaolvDao.updateBatch(list);
        }
    }

    public void checkHuazhuData() {
        List<String> huazhuIds = zhJdJdbMappingDao.selectHuazhuIds();
        List<String> ids = jdJdbHuazhuDao.selectIds();

        Map<String, String> map = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            map.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        huazhuIds = huazhuIds.stream().distinct().collect(Collectors.toList());
        for (String id : huazhuIds) {
            if (!map.containsKey(id)) {
                noExistList.add(id);
            }
        }
        log.info("无法找到huazhuId共{}条.分别是{}", noExistList.size(), noExistList);
    }

    public void updateHuazhuReflect() {
        List<String> ids = zhJdJdbMappingDao.selectHuazhuIds();
        ids = ids.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < ids.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = ids.subList(start, j);
                jdJdbHuazhuDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = ids.subList(start, ids.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbHuazhuDao.updateBatch(list);
        }
    }

    public void checkDongchengData() {
        List<String> dongchengIds = zhJdJdbMappingDao.selectDongchengIds();
        List<String> ids = jdJdbDongchengDao.selectIds();

        Map<String, String> map = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            map.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        dongchengIds = dongchengIds.stream().distinct().collect(Collectors.toList());
        for (String id : dongchengIds) {
            if (!map.containsKey(id)) {
                noExistList.add(id);
            }
        }
        log.info("无法找到dongchengId共{}条.分别是{}", noExistList.size(), noExistList);
    }

    public void updateDongchengReflect() {
        List<String> ids = zhJdJdbMappingDao.selectDongchengIds();
        ids = ids.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < ids.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = ids.subList(start, j);
                jdJdbDongchengDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = ids.subList(start, ids.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbDongchengDao.updateBatch(list);
        }
    }

    public void checkJinjiangData() {
        List<String> jinjiangIds = zhJdJdbMappingDao.selectJinjiangIds();
        List<String> ids = jdJdbJinjiangDao.selectIds();

        Map<String, String> map = Maps.newHashMapWithExpectedSize(ids.size());
        for (String id : ids) {
            map.put(id, "1");
        }
        List<String> noExistList = Lists.newArrayList();
        jinjiangIds = jinjiangIds.stream().distinct().collect(Collectors.toList());
        for (String id : jinjiangIds) {
            if (!map.containsKey(id)) {
                noExistList.add(id);
            }
        }
        log.info("无法找到jinjiangId共{}条.分别是{}", noExistList.size(), noExistList);
    }

    public void updateJinjiangReflect() {
        List<String> ids = zhJdJdbMappingDao.selectJinjiangIds();
        ids = ids.stream().distinct().collect(Collectors.toList());
        int start = 0;
        for (int j = 0; j < ids.size(); j++) {
            if (j != 0 && j % 1000 == 0) {
                List<String> list = ids.subList(start, j);
                jdJdbJinjiangDao.updateBatch(list);
                start = j;
            }
        }
        List<String> list = ids.subList(start, ids.size());
        if (CollectionUtils.isNotEmpty(list)) {
            jdJdbJinjiangDao.updateBatch(list);
        }
    }
}
