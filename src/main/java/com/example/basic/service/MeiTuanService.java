package com.example.basic.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import com.example.basic.dao.*;
import com.example.basic.domain.*;
import com.example.basic.entity.*;
import com.example.basic.helper.GnMappingScoreHelper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/11/11
 */
@Slf4j
@Service
public class MeiTuanService {

    @Resource
    private MeituanInfoDao meituanInfoDao;

    @Resource
    private DongchengInfoDao dongchengInfoDao;

    @Resource
    private MeituanDongchengMatchLabDao meituanDongchengMatchLabDao;

    @Resource
    private MeituanHuazhuMatchLabDao meituanHuazhuMatchLabDao;

    @Resource
    private MeituanJinjiangMatchLabDao meituanJinjiangMatchLabDao;

    @Resource
    private HuazhuInfoDao huazhuInfoDao;

    @Resource
    private JinjiangInfoDao jinjiangInfoDao;

    @Resource
    private HotelHsjlDao hotelHsjlDao;

    @Resource
    private MeituanHsjlMatchLabDao meituanHsjlMatchLabDao;

    @Resource
    private WstHotelInfoDao wstHotelInfoDao;
    @Resource
    private WstHotelPolicyDao wstHotelPolicyDao;
    @Resource
    private WstHotelFacilitiesDao wstHotelFacilitiesDao;
    @Resource
    private WstHotelStatisticsDao wstHotelStatisticsDao;

    @Resource
    private WstHotelTypeDao wstHotelTypeDao;
    @Resource
    private WstHotelCityDao wstHotelCityDao;
    @Resource
    private MeituanFacilitiesDao meituanFacilitiesDao;
    @Resource
    private WstHotelImagesDao wstHotelImagesDao;

    @Resource
    private MeituanStatisticsDao meituanStatisticsDao;

    @Resource
    private MeituanImagesDao meituanImagesDao;
    @Resource
    private MeituanPolicyDao meituanPolicyDao;

    private static final Integer CORE_POOL_SIZE = 200;
    private static final Integer MAXIMUM_POOL_SIZE = 250;

    private static final Executor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(3000));

    public void matchDongcheng() throws ExecutionException, InterruptedException {
        List<DongchengInfo> dongchengInfos = dongchengInfoDao.selectAll();
        List<MeituanInfo> meituanInfos = meituanInfoDao.selectDongchengList();
        Map<String, List<MeituanInfo>> provineceMap = meituanInfos.stream().collect(Collectors.groupingBy(MeituanInfo::getProvinceName));
        Map<String, List<DongchengInfo>> provineceDongchengMap = dongchengInfos.stream().collect(Collectors.groupingBy(DongchengInfo::getProvinceName));

        List<String> notFoundId = Lists.newArrayListWithCapacity(100);
        for (Map.Entry<String, List<DongchengInfo>> entry : provineceDongchengMap.entrySet()) {
            List<DongchengInfo> oneProvineceList = entry.getValue();
            String provinceName = entry.getKey();
            List<MeituanInfo> subList = provineceMap.get(provinceName);
            for (DongchengInfo dongchengInfo : oneProvineceList) {
                if (CollectionUtils.isEmpty(subList)) {
                    System.out.println("省份数据被跳过" + provinceName);
                    continue;
                }
                String name = dongchengInfo.getName();
                String tel = dongchengInfo.getTel();
                BigDecimal latitude = dongchengInfo.getLatitude();
                BigDecimal longitude = dongchengInfo.getLongitude();

                List<CompletableFuture<MeiTuanMatchResult>> futures = Lists.newArrayListWithCapacity(oneProvineceList.size());
                futures.add(CompletableFuture.supplyAsync(() -> {
                    Map<Integer, List<MeituanInfo>> scoreMap = Maps.newConcurrentMap();
                    for (MeituanInfo meituanInfo : subList) {
                        Integer score = GnMappingScoreHelper.calculateScore(name, latitude, longitude, tel,
                                meituanInfo.getName(), meituanInfo.getLatBaidu(),
                                meituanInfo.getLonBaidu(), meituanInfo.getPhone());
                        if (score == 0) continue;
                        if (score == 11 || score == -11) {
                            MeiTuanMatchResult result = new MeiTuanMatchResult();
                            Map<Integer, List<MeituanInfo>> fullScoreMap = Maps.newConcurrentMap();
                            fullScoreMap.put(score, Lists.newArrayList(meituanInfo));
                            result.setScoreMap(fullScoreMap);
                            result.setZero(Boolean.FALSE);
                            return result;
                        }
                        if (scoreMap.containsKey(score)) {
                            scoreMap.get(score).add(meituanInfo);
                        } else {
                            scoreMap.put(score, Lists.newArrayList(meituanInfo));
                        }
                    }
                    MeiTuanMatchResult result = new MeiTuanMatchResult();
                    result.setScoreMap(scoreMap);
                    if (scoreMap.isEmpty()) {
                        result.setZero(Boolean.TRUE);
                    } else {
                        result.setZero(Boolean.FALSE);
                    }
                    return result;
                }, executor));
                Map<Integer, List<MeituanInfo>> scoreMap = Maps.newHashMapWithExpectedSize(oneProvineceList.size());
                for (CompletableFuture<MeiTuanMatchResult> future : futures) {
                    MeiTuanMatchResult matchResult = future.get();
                    // 如果0分，跳过此部分评分
                    if (matchResult.getZero()) {
                        System.out.println("省份数据被跳过" + provinceName);
                        continue;
                    }
                    for (Map.Entry<Integer, List<MeituanInfo>> reslutEntry : matchResult.getScoreMap().entrySet()) {
                        Integer score = reslutEntry.getKey();
                        List<MeituanInfo> sameScoreHotels = reslutEntry.getValue();
                        if (scoreMap.containsKey(score)) {
                            scoreMap.get(score).addAll(sameScoreHotels);
                        } else {
                            scoreMap.put(score, sameScoreHotels);
                        }
                    }
                }

                // 改酒店没有得分
                if (scoreMap.isEmpty()) {
                    System.out.println("酒店数据被跳过" + name);
                    log.info("酒店{}数据被跳过", name);
                    notFoundId.add(dongchengInfo.getHid());
                    continue;
                }
                List<MeituanDongchengMatchLab> uniqueList = Lists.newArrayListWithCapacity(1);
                List<MeituanDongchengMatchLab> multiList = Lists.newArrayListWithCapacity(oneProvineceList.size());

                scoreMap.entrySet().stream()
                        .max(Comparator.comparingInt(entry1 -> Math.abs(entry1.getKey())))
                        .ifPresent(max -> createMatchResult(dongchengInfo, max, uniqueList, multiList));
                if (CollectionUtils.isNotEmpty(uniqueList)) {
                    meituanDongchengMatchLabDao.saveBatch(uniqueList);
                }
                if (CollectionUtils.isNotEmpty(multiList)) {
                    meituanDongchengMatchLabDao.saveBatch(multiList);
                }
            }
            System.out.println(String.join(",", notFoundId));
        }
    }

    private void createMatchResult(DongchengInfo dongchengInfo, Map.Entry<Integer, List<MeituanInfo>> max,
                                   List<MeituanDongchengMatchLab> uniqueList, List<MeituanDongchengMatchLab> multiList) {
        Integer score = max.getKey();
        List<MeituanInfo> meituanInfos = max.getValue();
        if (meituanInfos.size() > 1) {
            // -1分数过多，直接丢弃
            /*if (score.equals(-1)) {
                return;
            }*/
            for (MeituanInfo meituanInfo : meituanInfos) {
                multiList.add(createOneMatchLab(dongchengInfo, meituanInfo, score, true));
            }
            return;
        }
        uniqueList.add(createOneMatchLab(dongchengInfo, meituanInfos.get(0), score, false));
    }

    private MeituanDongchengMatchLab createOneMatchLab(DongchengInfo dongchengInfo, MeituanInfo meituanInfo, Integer score, boolean b) {
        MeituanDongchengMatchLab lab = new MeituanDongchengMatchLab();
        lab.setMtId(meituanInfo.getId());
        lab.setMtName(meituanInfo.getName());
        lab.setMtLatitude(meituanInfo.getLatBaidu());
        lab.setMtLongitude(meituanInfo.getLonBaidu());
        lab.setMtAddress(meituanInfo.getAddress());
        lab.setMtTel(meituanInfo.getPhone());
        lab.setDongchengId(String.valueOf(dongchengInfo.getHid()));
        lab.setDongchengName(dongchengInfo.getName());
        lab.setDongchengAddress(dongchengInfo.getAddr());
        lab.setDongchengLatitude(dongchengInfo.getLatitude());
        lab.setDongchengLongitude(dongchengInfo.getLongitude());
        lab.setDongchengTel(dongchengInfo.getTel());
        lab.setScore(score);
        lab.setMultiMatch(b);
        double meter = GnMappingScoreHelper.calculateMeter(meituanInfo.getLatBaidu(), meituanInfo.getLonBaidu(), dongchengInfo.getLatitude(), dongchengInfo.getLongitude());
        lab.setDiffMeter(BigDecimal.valueOf(meter));
        return lab;
    }

    public void exportDongcheng() {
        List<MeituanDongchengMatchLab> meituanDongchengMatchLabs = meituanDongchengMatchLabDao.selectExport();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\国内匹配酒店\\美团东呈代码匹配结果.xlsx";
        EasyExcel.write(fileName, MeituanDongchengMatchLab.class).sheet("匹配明细").doWrite(meituanDongchengMatchLabs);
    }

    public void exportDongcheng2() {
        Set<Long> notMatchIdList = meituanDongchengMatchLabDao.selectNotMatchId();
        List<MeituanInfo> meituanInfos = meituanInfoDao.selectDongchengExport();
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\国内匹配酒店\\需要匹配.xlsx";
        EasyExcel.write(fileName, MeituanInfo.class).sheet("匹配明细").doWrite(meituanInfos);
    }

    public void exportDongcheng2Fake() {
        List<String> notMatch = Lists.newArrayList("592002", "599001", "2891004", "2853006", "27075",
                "715021", "716015", "717008", "718004", "719003", "20997", "20999", "755034", "757004",
                "759027", "763003", "28027", "836002", "2996001", "512020", "2510007", "2512014", "391002",
                "770014", "771005", "771028", "771046", "772055", "773062", "773064", "773072", "775021",
                "776024", "2772024", "2774018", "2775030", "2778022", "2779012", "794002", "2791015", "2796002",
                "2023043", "888006", "2871010", "10003", "2010028", "571014");
        /*List<String> notMatchIdList = meituanDongchengMatchLabDao.selectNotMatchId();
        notMatch.addAll(notMatchIdList);
        List<DongchengInfo> dongchengInfos = dongchengInfoDao.selectListByIds(notMatch);
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\国内匹配酒店\\东呈待匹配.xlsx";
        EasyExcel.write(fileName, DongchengInfo.class).sheet("匹配明细").doWrite(dongchengInfos);*/

    }

    public void matchHuazhu() throws ExecutionException, InterruptedException {
        List<HuazhuInfo> huazhuInfos = huazhuInfoDao.selectAll();
        List<MeituanInfo> meituanInfos = meituanInfoDao.selectHuazhuList();
        Map<String, List<MeituanInfo>> provineceMap = meituanInfos.stream().collect(Collectors.groupingBy(MeituanInfo::getProvinceName));
        Map<String, List<HuazhuInfo>> provineceHuazhuMap = huazhuInfos.stream().collect(Collectors.groupingBy(HuazhuInfo::getProvinceName));

        List<Long> notFoundId = Lists.newArrayListWithCapacity(2000);
        for (Map.Entry<String, List<MeituanInfo>> entry : provineceMap.entrySet()) {
            List<MeituanInfo> oneProvineceList = entry.getValue();
            String provinceName = entry.getKey();
            List<HuazhuInfo> subList = provineceHuazhuMap.get(provinceName);
            for (MeituanInfo meituanInfo : oneProvineceList) {
                if (CollectionUtils.isEmpty(subList)) {
                    System.out.println("省份数据被跳过" + provinceName);
                    continue;
                }
                String name = meituanInfo.getName();
                String tel = meituanInfo.getPhone();
                BigDecimal latitude = meituanInfo.getLatGaode();
                BigDecimal longitude = meituanInfo.getLonGaode();

                List<CompletableFuture<MeiTuanMatchResult>> futures = Lists.newArrayListWithCapacity(oneProvineceList.size());
                futures.add(CompletableFuture.supplyAsync(() -> {
                    Map<Integer, List<HuazhuInfo>> scoreMap = Maps.newConcurrentMap();
                    for (HuazhuInfo info : subList) {
                        Integer score = GnMappingScoreHelper.calculateScore(name, latitude, longitude, tel,
                                info.getHotelName(), new BigDecimal(info.getLatitude()),
                                new BigDecimal(info.getLongitude()), info.getHotelTelephone());
                        if (score == 0) continue;
                        if (score == 11 || score == -11) {
                            MeiTuanMatchResult result = new MeiTuanMatchResult();
                            Map<Integer, List<HuazhuInfo>> fullScoreMap = Maps.newConcurrentMap();
                            fullScoreMap.put(score, Lists.newArrayList(info));
                            result.setHuazhuScoreMap(fullScoreMap);
                            result.setZero(Boolean.FALSE);
                            return result;
                        }
                        if (scoreMap.containsKey(score)) {
                            scoreMap.get(score).add(info);
                        } else {
                            scoreMap.put(score, Lists.newArrayList(info));
                        }
                    }
                    MeiTuanMatchResult result = new MeiTuanMatchResult();
                    result.setHuazhuScoreMap(scoreMap);
                    if (scoreMap.isEmpty()) {
                        result.setZero(Boolean.TRUE);
                    } else {
                        result.setZero(Boolean.FALSE);
                    }
                    return result;
                }, executor));
                Map<Integer, List<HuazhuInfo>> scoreMap = Maps.newHashMapWithExpectedSize(oneProvineceList.size());
                for (CompletableFuture<MeiTuanMatchResult> future : futures) {
                    MeiTuanMatchResult matchResult = future.get();
                    // 如果0分，跳过此部分评分
                    if (matchResult.getZero()) {
                        System.out.println("省份数据被跳过" + provinceName);
                        continue;
                    }
                    for (Map.Entry<Integer, List<HuazhuInfo>> reslutEntry : matchResult.getHuazhuScoreMap().entrySet()) {
                        Integer score = reslutEntry.getKey();
                        List<HuazhuInfo> sameScoreHotels = reslutEntry.getValue();
                        if (scoreMap.containsKey(score)) {
                            scoreMap.get(score).addAll(sameScoreHotels);
                        } else {
                            scoreMap.put(score, sameScoreHotels);
                        }
                    }
                }

                // 改酒店没有得分
                if (scoreMap.isEmpty()) {
                    System.out.println("酒店数据被跳过" + name);
                    log.info("酒店{}数据被跳过", name);
                    notFoundId.add(meituanInfo.getId());
                    continue;
                }
                List<MeituanHuazhuMatchLab> uniqueList = Lists.newArrayListWithCapacity(1);
                List<MeituanHuazhuMatchLab> multiList = Lists.newArrayListWithCapacity(oneProvineceList.size());

                scoreMap.entrySet().stream()
                        .max(Comparator.comparingInt(entry1 -> Math.abs(entry1.getKey())))
                        .ifPresent(max -> createMatchResult2(meituanInfo, max, uniqueList, multiList));
                if (CollectionUtils.isNotEmpty(uniqueList)) {
                    meituanHuazhuMatchLabDao.saveBatch(uniqueList);
                }
                if (CollectionUtils.isNotEmpty(multiList)) {
                    meituanHuazhuMatchLabDao.saveBatch(multiList);
                }
            }
            System.out.println(notFoundId.stream().map(Objects::toString).collect(Collectors.joining(",")));
        }
    }

    private void createMatchResult2(MeituanInfo meituanInfo, Map.Entry<Integer, List<HuazhuInfo>> max,
                                    List<MeituanHuazhuMatchLab> uniqueList, List<MeituanHuazhuMatchLab> multiList) {
        Integer score = max.getKey();
        List<HuazhuInfo> infos = max.getValue();
        if (infos.size() > 1) {
            // -1分数过多，直接丢弃
            /*if (score.equals(-1)) {
                return;
            }*/
            for (HuazhuInfo info : infos) {
                multiList.add(createOneMatchLab2(meituanInfo, info, score, true));
            }
            return;
        }
        uniqueList.add(createOneMatchLab2(meituanInfo, infos.get(0), score, false));
    }

    private MeituanHuazhuMatchLab createOneMatchLab2(MeituanInfo meituanInfo, HuazhuInfo info, Integer score, boolean b) {
        MeituanHuazhuMatchLab lab = new MeituanHuazhuMatchLab();
        lab.setMtId(meituanInfo.getId());
        lab.setMtName(meituanInfo.getName());
        lab.setMtLatitude(meituanInfo.getLatGaode());
        lab.setMtLongitude(meituanInfo.getLonGaode());
        lab.setMtAddress(meituanInfo.getAddress());
        lab.setMtTel(meituanInfo.getPhone());
        lab.setHuazhuId(info.getHotelId());
        lab.setHuazhuName(info.getHotelName());
        lab.setHuazhuAddress(info.getHotelAddress());
        lab.setHuazhuLatitude(new BigDecimal(info.getLatitude()));
        lab.setHuazhuLongitude(new BigDecimal(info.getLongitude()));
        lab.setHuazhuTel(info.getHotelTelephone());
        lab.setScore(score);
        lab.setMultiMatch(b);
        double meter = GnMappingScoreHelper.calculateMeter(meituanInfo.getLatGaode(), meituanInfo.getLonGaode(),
                new BigDecimal(info.getLatitude()), new BigDecimal(info.getLongitude()));
        lab.setDiffMeter(BigDecimal.valueOf(meter));
        return lab;
    }

    public void matchJinjiang() throws ExecutionException, InterruptedException {
        List<JinjiangInfo> jinjiangInfos = jinjiangInfoDao.selectAll();
        List<MeituanInfo> meituanInfos = meituanInfoDao.selectJinjiangList();

        List<Long> notFoundId = Lists.newArrayListWithCapacity(10000);
        for (MeituanInfo meituanInfo : meituanInfos) {
            String name = meituanInfo.getName();
            String tel = meituanInfo.getPhone();
            BigDecimal latitude = meituanInfo.getLatBaidu();
            BigDecimal longitude = meituanInfo.getLonBaidu();

            List<CompletableFuture<MeiTuanMatchResult>> futures = Lists.newArrayListWithCapacity(meituanInfos.size());
            futures.add(CompletableFuture.supplyAsync(() -> {
                Map<Integer, List<JinjiangInfo>> scoreMap = Maps.newConcurrentMap();
                for (JinjiangInfo info : jinjiangInfos) {
                    Integer score = GnMappingScoreHelper.calculateScore(name, latitude, longitude, tel,
                            info.getInnName(), info.getBaiduLag(),
                            info.getBaiduLng(), info.getInnPhone());
                    if (score == 0) continue;
                    if (score == 11 || score == -11) {
                        MeiTuanMatchResult result = new MeiTuanMatchResult();
                        Map<Integer, List<JinjiangInfo>> fullScoreMap = Maps.newConcurrentMap();
                        fullScoreMap.put(score, Lists.newArrayList(info));
                        result.setJinjiangScoreMap(fullScoreMap);
                        result.setZero(Boolean.FALSE);
                        return result;
                    }
                    if (scoreMap.containsKey(score)) {
                        scoreMap.get(score).add(info);
                    } else {
                        scoreMap.put(score, Lists.newArrayList(info));
                    }
                }
                MeiTuanMatchResult result = new MeiTuanMatchResult();
                result.setJinjiangScoreMap(scoreMap);
                if (scoreMap.isEmpty()) {
                    result.setZero(Boolean.TRUE);
                } else {
                    result.setZero(Boolean.FALSE);
                }
                return result;
            }, executor));
            Map<Integer, List<JinjiangInfo>> scoreMap = Maps.newHashMapWithExpectedSize(meituanInfos.size());
            for (CompletableFuture<MeiTuanMatchResult> future : futures) {
                MeiTuanMatchResult matchResult = future.get();
                // 如果0分，跳过此部分评分
                if (matchResult.getZero()) {
                    continue;
                }
                for (Map.Entry<Integer, List<JinjiangInfo>> reslutEntry : matchResult.getJinjiangScoreMap().entrySet()) {
                    Integer score = reslutEntry.getKey();
                    List<JinjiangInfo> sameScoreHotels = reslutEntry.getValue();
                    if (scoreMap.containsKey(score)) {
                        scoreMap.get(score).addAll(sameScoreHotels);
                    } else {
                        scoreMap.put(score, sameScoreHotels);
                    }
                }
            }

            // 改酒店没有得分
            if (scoreMap.isEmpty()) {
                //System.out.println("酒店数据被跳过" + name);
                log.info("酒店{}数据被跳过", name);
                notFoundId.add(meituanInfo.getId());
                continue;
            }
            List<MeituanJinjiangMatchLab> uniqueList = Lists.newArrayListWithCapacity(1);
            List<MeituanJinjiangMatchLab> multiList = Lists.newArrayListWithCapacity(meituanInfos.size());

            scoreMap.entrySet().stream()
                    .max(Comparator.comparingInt(entry1 -> Math.abs(entry1.getKey())))
                    .ifPresent(max -> createMatchResult3(meituanInfo, max, uniqueList, multiList));
            if (CollectionUtils.isNotEmpty(uniqueList)) {
                meituanJinjiangMatchLabDao.saveBatch(uniqueList);
            }
            if (CollectionUtils.isNotEmpty(multiList)) {
                meituanJinjiangMatchLabDao.saveBatch(multiList);
            }
        }
        System.out.println(notFoundId.stream().map(Objects::toString).collect(Collectors.joining(",")));
    }

    private void createMatchResult3(MeituanInfo meituanInfo, Map.Entry<Integer, List<JinjiangInfo>> max,
                                    List<MeituanJinjiangMatchLab> uniqueList, List<MeituanJinjiangMatchLab> multiList) {
        Integer score = max.getKey();
        List<JinjiangInfo> infos = max.getValue();
        if (infos.size() > 1) {
            // -1分数过多，直接丢弃
            /*if (score.equals(-1)) {
                return;
            }*/
            for (JinjiangInfo info : infos) {
                multiList.add(createOneMatchLab3(meituanInfo, info, score, true));
            }
            return;
        }
        uniqueList.add(createOneMatchLab3(meituanInfo, infos.get(0), score, false));
    }

    private MeituanJinjiangMatchLab createOneMatchLab3(MeituanInfo meituanInfo, JinjiangInfo info, Integer score, boolean b) {
        MeituanJinjiangMatchLab lab = new MeituanJinjiangMatchLab();
        lab.setMtId(meituanInfo.getId());
        lab.setMtName(meituanInfo.getName());
        lab.setMtLatitude(meituanInfo.getLatBaidu());
        lab.setMtLongitude(meituanInfo.getLonBaidu());
        lab.setMtAddress(meituanInfo.getAddress());
        lab.setMtTel(meituanInfo.getPhone());
        lab.setJinjiangId(info.getInnId());
        lab.setJinjiangName(info.getInnName());
        lab.setJinjiangAddress(info.getAddress());
        lab.setJinjiangLatitude(info.getBaiduLag());
        lab.setJinjiangLongitude(info.getBaiduLng());
        lab.setJinjiangTel(info.getInnPhone());
        lab.setScore(score);
        lab.setMultiMatch(b);
        double meter = GnMappingScoreHelper.calculateMeter(meituanInfo.getLatBaidu(), meituanInfo.getLonBaidu(),
                info.getBaiduLag(), info.getBaiduLng());
        lab.setDiffMeter(BigDecimal.valueOf(meter));
        return lab;
    }

    public void importCity() throws FileNotFoundException {
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\美团静态资源\\province-city-location.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<City> list = Lists.newArrayListWithCapacity(310000);
        EasyExcel.read(inputStream, WebbedsImportBean.class, new PageReadListener<City>(list::addAll, 1000)).headRowNumber(1).sheet().doRead();

    }

    public void initHotel() {
        /*List<WstHotelType> wstHotelTypes = wstHotelTypeDao.selectAll();
        Map<Integer, String> typeMap = wstHotelTypes.stream().collect(Collectors.toMap(WstHotelType::getCode, WstHotelType::getName));
        List<WstHotelThemes> wstHotelThemes = wstHotelThemesDao.selectAll();
        Map<Integer, String> themesMap = wstHotelThemes.stream().collect(Collectors.toMap(WstHotelThemes::getCode, WstHotelThemes::getName));*/
        List<MeituanHuazhuMatchLab> meituanHuazhuMatchLabs = meituanHuazhuMatchLabDao.selectMap();
        Map<Long, String> huazhuIdMap = meituanHuazhuMatchLabs.stream().collect(Collectors.toMap(MeituanHuazhuMatchLab::getMtId, MeituanHuazhuMatchLab::getHuazhuId));
        List<MeituanJinjiangMatchLab> meituanJinjiangMatchLabs = meituanJinjiangMatchLabDao.selectMap();
        Map<Long, String> jinjiangIdMap = meituanJinjiangMatchLabs.stream().collect(Collectors.toMap(MeituanJinjiangMatchLab::getMtId, MeituanJinjiangMatchLab::getJinjiangId));
        List<MeituanDongchengMatchLab> meituanDongchengMatchLabs = meituanDongchengMatchLabDao.selectMap();
        Map<Long, String> dongchengIdMap = meituanDongchengMatchLabs.stream().collect(Collectors.toMap(MeituanDongchengMatchLab::getMtId, MeituanDongchengMatchLab::getDongchengId));
        Map<Integer, String> map = Maps.newHashMap();
        map.put(101050, "接送机服务");
        map.put(101006, "停车场");
        map.put(101100, "商务中心");

        int size = 1000;
        long pages = 1093;
        for (int page = 1; page < pages; page++) {
            PageHelper.startPage(page, size);
            Page<MeituanInfo> meituanInfos = meituanInfoDao.selectPageList();
            List<Long> mtIds = meituanInfos.stream().map(MeituanInfo::getId).toList();
            if (CollectionUtils.isEmpty(mtIds)) continue;
            List<MeituanFacilities> meituanFacilities = meituanFacilitiesDao.selectIdMap(mtIds);
            Map<Long, List<MeituanFacilities>> mtIdMap = meituanFacilities.stream().collect(Collectors.groupingBy(MeituanFacilities::getMtId));
            List<WstHotelInfo> infos = Lists.newArrayListWithCapacity(size);
            for (MeituanInfo meituanInfo : meituanInfos) {
                WstHotelInfo info = new WstHotelInfo();
                String name = meituanInfo.getName();
                Integer countryCode = meituanInfo.getCountryCode();
                if (name.contains("测试") || countryCode != 10000001) {
                    continue;
                }
                BeanUtils.copyProperties(meituanInfo, info);

                if (StringUtils.hasLength(meituanInfo.getBusinessDistrictsCode())) {
                    info.setBusinessDistrictsCode(Integer.parseInt(meituanInfo.getBusinessDistrictsCode()));
                }
                info.setGaodeLatitude(meituanInfo.getLonGaode());
                info.setGaodeLongitude(meituanInfo.getLatGaode());
                info.setBaiduLatitude(meituanInfo.getLonBaidu());
                info.setBaiduLongitude(meituanInfo.getLatBaidu());
                info.setGoogleLatitude(meituanInfo.getLonGoogle());
                info.setGoogleLongitude(meituanInfo.getLatGoogle());
                if (meituanInfo.getScore() != null) {
                    StringBuilder score = new StringBuilder(String.valueOf(meituanInfo.getScore())).insert(1, ".");
                    info.setScore(score.toString());
                }
                String themes = meituanInfo.getThemes();
                if (StringUtils.hasLength(themes)) {
                    List<WstHotelThemes> themesList = JSON.parseArray(themes, WstHotelThemes.class);
                    String themesIds = themesList.stream().map(WstHotelThemes::getCode).map(Objects::toString).collect(Collectors.joining(","));
                    info.setThemes(themesIds);
                }
                String types = meituanInfo.getTypes();
                if (StringUtils.hasLength(types)) {
                    List<WstHotelType> typeList = JSON.parseArray(types, WstHotelType.class);
                    String typeIds = typeList.stream().map(WstHotelType::getCode).map(Objects::toString).collect(Collectors.joining(","));
                    info.setTypes(typeIds);
                }
                String paymentMethod = meituanInfo.getPaymentMethod();
                if (StringUtils.hasLength(paymentMethod)) {
                    List<WstHotelType> parseArray = JSON.parseArray(paymentMethod, WstHotelType.class);
                    String paymentMethodIds = parseArray.stream().map(WstHotelType::getCode).map(Objects::toString).collect(Collectors.joining(","));
                    info.setPaymentMethod(paymentMethodIds);
                }
                Long mtId = meituanInfo.getId();
                info.setMtId(mtId);
                info.setStatus(0);
                info.setHuazhuId(huazhuIdMap.get(mtId));
                info.setJinjiangId(jinjiangIdMap.get(mtId));
                info.setDongchengId(dongchengIdMap.get(mtId));
                info.setCreateName("WST");

                StringBuilder tags = new StringBuilder();
                List<MeituanFacilities> facilities = mtIdMap.get(info.getMtId());
                if (CollectionUtils.isNotEmpty(facilities)) {
                    for (MeituanFacilities facility : facilities) {
                        if (map.containsKey(facility.getFacilityId())) {
                            tags.append(facility.getFacilityName()).append(",");
                        }
                    }
                    if (tags.length() > 0) {
                        info.setTags(tags.substring(0, tags.length() - 1));
                    }
                }
                infos.add(info);
            }
            wstHotelInfoDao.saveBatch(infos);
        }
    }

    public void initCity() {
        //province();
        //city();
        //county();
        businessArea();

    }

    private void businessArea() {
        List<WstHotelCity> wstHotelCities = wstHotelCityDao.selectAreaList();
        List<WstHotelCity> businessAreaList = Lists.newArrayListWithCapacity(22000);
        for (WstHotelCity area : wstHotelCities) {
            Integer areaCode = area.getCode();
            List<WstHotelInfo> infos = wstHotelInfoDao.selectBusinessAreaByArea(areaCode);
            for (WstHotelInfo info : infos) {
                WstHotelCity businessArea = new WstHotelCity();
                businessArea.setParent(areaCode);
                businessArea.setCode(info.getBusinessDistrictsCode());
                businessArea.setName(info.getBusinessDistrictsName());
                businessArea.setType(4);
                businessAreaList.add(businessArea);
            }
        }
        wstHotelCityDao.saveBatch(businessAreaList);
    }

    private void county() {
        List<WstHotelCity> wstHotelCities = wstHotelCityDao.selectCityList();
        List<WstHotelCity> areaList = Lists.newArrayListWithCapacity(3000);
        for (WstHotelCity city : wstHotelCities) {
            Integer cityCode = city.getCode();
            List<WstHotelInfo> infos = wstHotelInfoDao.selectAreaByCity(cityCode);
            for (WstHotelInfo info : infos) {
                WstHotelCity area = new WstHotelCity();
                area.setParent(cityCode);
                area.setCode(info.getAreaCode());
                area.setName(info.getAreaName());
                area.setType(3);
                areaList.add(area);
            }
        }
        wstHotelCityDao.saveBatch(areaList);
    }

    private void city() {
        List<WstHotelCity> wstHotelCities = wstHotelCityDao.selectProvinceList();
        List<WstHotelCity> cityList = Lists.newArrayListWithCapacity(400);
        for (WstHotelCity province : wstHotelCities) {
            Integer provinceCode = province.getCode();
            List<WstHotelInfo> infos = wstHotelInfoDao.selectCityByProvince(provinceCode);
            for (WstHotelInfo info : infos) {
                WstHotelCity city = new WstHotelCity();
                city.setParent(provinceCode);
                city.setCode(info.getCityCode());
                city.setName(info.getCityName());
                city.setType(2);
                cityList.add(city);
            }
        }
        wstHotelCityDao.saveBatch(cityList);
    }

    private void province() {
        List<WstHotelInfo> infos = wstHotelInfoDao.selectProvince();
        List<WstHotelCity> provinceList = Lists.newArrayListWithCapacity(infos.size());
        for (WstHotelInfo info : infos) {
            WstHotelCity city = new WstHotelCity();
            city.setType(1);
            city.setCode(info.getProvinceCode());
            city.setName(info.getProvinceName());
            provinceList.add(city);
        }
        wstHotelCityDao.saveBatch(provinceList);
    }

    public void initImage() {
        List<WstHotelInfo> infos = wstHotelInfoDao.selectAllIdMatch();
        int batchSize = 1000;
        int total = infos.size();
        for (int i = 0; i < total; ) {
            List<WstHotelInfo> onceList = infos.subList(i, Math.min(total, i + batchSize));
            Map<Long, Long> mtIdHotelIdMap = onceList.stream().collect(Collectors.toMap(WstHotelInfo::getMtId, WstHotelInfo::getHotelId));
            Set<Long> mtIds = mtIdHotelIdMap.keySet();
            List<MeituanImages> meituanImages = meituanImagesDao.selectLiatByIds(mtIds);
            List<WstHotelImages> wstHotelImages = transfer(meituanImages, mtIdHotelIdMap);
            saveBatch(wstHotelImages);
            i += batchSize;
        }
    }

    private void saveBatch(List<WstHotelImages> wstHotelImages) {
        if (CollectionUtils.isEmpty(wstHotelImages)) return;
        int start = 0;
        for (int j = 0; j < wstHotelImages.size(); j++) {
            if (j != 0 && j % 5000 == 0) {
                List<WstHotelImages> list = wstHotelImages.subList(start, j);
                wstHotelImagesDao.saveBatch(list);
                start = j;
            }
        }
        List<WstHotelImages> list = wstHotelImages.subList(start, wstHotelImages.size());
        if (CollectionUtils.isNotEmpty(list)) {
            wstHotelImagesDao.saveBatch(list);
        }
    }

    private List<WstHotelImages> transfer(List<MeituanImages> meituanImages, Map<Long, Long> mtIdHotelIdMap) {
        List<WstHotelImages> images = Lists.newArrayListWithExpectedSize(meituanImages.size());
        Date now = new Date();
        String creator = "WST";
        for (MeituanImages meituanImage : meituanImages) {
            WstHotelImages hotelImages = new WstHotelImages();
            BeanUtils.copyProperties(meituanImage, hotelImages);
            hotelImages.setHotelId(mtIdHotelIdMap.get(meituanImage.getMtId()));
            hotelImages.setCreateDate(now);
            hotelImages.setCreateName(creator);
            String linkDescription = meituanImage.getLinkDescription();
            if (StringUtils.hasLength(linkDescription)) {
                hotelImages.setLinkDescription(linkDescription.trim());
            }
            images.add(hotelImages);
        }
        return images;
    }

    public void initPolicy() {
        List<WstHotelInfo> infos = wstHotelInfoDao.selectAllIdMatch();
        Map<Long, Long> mtIdHotelIdMap = infos.stream().collect(Collectors.toMap(WstHotelInfo::getMtId, WstHotelInfo::getHotelId));
        List<MeituanPolicy> meituanPolicies = meituanPolicyDao.selectAll();
        saveBatch2(transfer2(meituanPolicies, mtIdHotelIdMap));
    }

    private List<WstHotelPolicy> transfer2(List<MeituanPolicy> meituanPolicies, Map<Long, Long> mtIdHotelIdMap) {
        Date now = new Date();
        String creator = "WST";
        List<WstHotelPolicy> policies = Lists.newArrayListWithCapacity(meituanPolicies.size());
        for (MeituanPolicy meituanPolicy : meituanPolicies) {
            Long hotelId = mtIdHotelIdMap.get(meituanPolicy.getMtId());
            if (hotelId == null) continue;
            WstHotelPolicy policy = new WstHotelPolicy();
            BeanUtils.copyProperties(meituanPolicy, policy);
            policy.setHotelId(hotelId);
            policy.setCreateDate(now);
            policy.setCreateName(creator);
            String checkinPolicy = meituanPolicy.getCheckinPolicy();
            if (StringUtils.hasLength(checkinPolicy)) {
                CheckPolicy checkPolicy = JSON.parseObject(checkinPolicy, CheckPolicy.class);
                policy.setCheckinStart(checkPolicy.getStart());
                policy.setCheckinEnd(checkPolicy.getEnd());
            }
            String checkoutPolicy = meituanPolicy.getCheckoutPolicy();
            if (StringUtils.hasLength(checkoutPolicy)) {
                CheckPolicy checkPolicy = JSON.parseObject(checkoutPolicy, CheckPolicy.class);
                policy.setCheckoutStart(checkPolicy.getStart());
                policy.setCheckoutEnd(checkPolicy.getEnd());
                policy.setCheckoutHours(checkPolicy.getHours());
            }
            String petPolicy = meituanPolicy.getPetPolicy();
            if (StringUtils.hasLength(petPolicy)) {
                PetPolicy pet = JSON.parseObject(petPolicy, PetPolicy.class);
                policy.setPetPolicy(pet.getAllowed());
            }
            policies.add(policy);
        }
        return policies;
    }

    private void saveBatch2(List<WstHotelPolicy> meituanPolicies) {
        if (CollectionUtils.isEmpty(meituanPolicies)) return;
        int start = 0;
        for (int j = 0; j < meituanPolicies.size(); j++) {
            if (j != 0 && j % 5000 == 0) {
                List<WstHotelPolicy> list = meituanPolicies.subList(start, j);
                wstHotelPolicyDao.saveBatch(list);
                start = j;
            }
        }
        List<WstHotelPolicy> list = meituanPolicies.subList(start, meituanPolicies.size());
        if (CollectionUtils.isNotEmpty(list)) {
            wstHotelPolicyDao.saveBatch(list);
        }
    }

    public void initFacilities() {
        List<WstHotelInfo> infos = wstHotelInfoDao.selectAllIdMatch();
        int batchSize = 10000;
        int total = infos.size();
        for (int i = 0; i < total; ) {
            List<WstHotelInfo> onceList = infos.subList(i, Math.min(total, i + batchSize));
            Map<Long, Long> mtIdHotelIdMap = onceList.stream().collect(Collectors.toMap(WstHotelInfo::getMtId, WstHotelInfo::getHotelId));
            Set<Long> mtIds = mtIdHotelIdMap.keySet();
            List<MeituanFacilities> meituanFacilities = meituanFacilitiesDao.selectLiatByIds(mtIds);
            List<WstHotelFacilities> facilities = transfer3(meituanFacilities, mtIdHotelIdMap);
            saveBatch3(facilities);
            i += batchSize;
        }
    }

    private void saveBatch3(List<WstHotelFacilities> wstHotelFacilities) {
        if (CollectionUtils.isEmpty(wstHotelFacilities)) return;
        int start = 0;
        for (int j = 0; j < wstHotelFacilities.size(); j++) {
            if (j != 0 && j % 5000 == 0) {
                List<WstHotelFacilities> list = wstHotelFacilities.subList(start, j);
                wstHotelFacilitiesDao.saveBatch(list);
                start = j;
            }
        }
        List<WstHotelFacilities> list = wstHotelFacilities.subList(start, wstHotelFacilities.size());
        if (CollectionUtils.isNotEmpty(list)) {
            wstHotelFacilitiesDao.saveBatch(list);
        }
    }

    private List<WstHotelFacilities> transfer3(List<MeituanFacilities> meituanFacilities, Map<Long, Long> mtIdHotelIdMap) {
        Date now = new Date();
        String creator = "WST";
        List<WstHotelFacilities> facilities = Lists.newArrayListWithCapacity(meituanFacilities.size());
        for (MeituanFacilities one : meituanFacilities) {
            WstHotelFacilities facilitiy = new WstHotelFacilities();
            BeanUtils.copyProperties(one, facilitiy);
            facilitiy.setHotelId(mtIdHotelIdMap.get(one.getMtId()));
            facilitiy.setCreateDate(now);
            facilitiy.setCreateName(creator);
            facilities.add(facilitiy);
        }
        return facilities;
    }

    public void initStatistics() {
        List<WstHotelInfo> infos = wstHotelInfoDao.selectAllIdMatch();
        Map<Long, Long> mtIdHotelIdMap = infos.stream().collect(Collectors.toMap(WstHotelInfo::getMtId, WstHotelInfo::getHotelId));
        List<MeituanStatistics> meituanStatistics = meituanStatisticsDao.selectAll();
        saveBatch4(transfer4(meituanStatistics, mtIdHotelIdMap));
    }

    private void saveBatch4(List<WstHotelStatistics> statistics) {
        if (CollectionUtils.isEmpty(statistics)) return;
        int start = 0;
        for (int j = 0; j < statistics.size(); j++) {
            if (j != 0 && j % 5000 == 0) {
                List<WstHotelStatistics> list = statistics.subList(start, j);
                wstHotelStatisticsDao.saveBatch(list);
                start = j;
            }
        }
        List<WstHotelStatistics> list = statistics.subList(start, statistics.size());
        if (CollectionUtils.isNotEmpty(list)) {
            wstHotelStatisticsDao.saveBatch(list);
        }
    }

    private List<WstHotelStatistics> transfer4(List<MeituanStatistics> meituanStatistics, Map<Long, Long> mtIdHotelIdMap) {
        Date now = new Date();
        String creator = "WST";
        List<WstHotelStatistics> statistics = Lists.newArrayListWithCapacity(meituanStatistics.size());
        for (MeituanStatistics one : meituanStatistics) {
            WstHotelStatistics hotelStatistics = new WstHotelStatistics();
            BeanUtils.copyProperties(one, hotelStatistics);
            hotelStatistics.setHotelId(mtIdHotelIdMap.get(one.getMtId()));
            hotelStatistics.setCreateDate(now);
            hotelStatistics.setCreateName(creator);
            statistics.add(hotelStatistics);
        }
        return statistics;
    }

    public void matchHsjl() throws ExecutionException, InterruptedException {
        List<HotelHsjl> hotelHsjls = hotelHsjlDao.selectAll();
        List<MeituanInfo> meituanInfos = meituanInfoDao.selectHsjlList();

        List<Long> notFoundId = Lists.newArrayListWithCapacity(2000);
        for (MeituanInfo meituanInfo : meituanInfos) {
            String name = meituanInfo.getName();
            String tel = meituanInfo.getPhone();
            BigDecimal latitude = meituanInfo.getLatGaode();
            BigDecimal longitude = meituanInfo.getLonGaode();

            List<CompletableFuture<MeiTuanMatchResult>> futures = Lists.newArrayListWithCapacity(meituanInfos.size());
            futures.add(CompletableFuture.supplyAsync(() -> {
                Map<Integer, List<HotelHsjl>> scoreMap = Maps.newConcurrentMap();
                for (HotelHsjl info : hotelHsjls) {
                    Integer score = GnMappingScoreHelper.calculateScore(name, latitude, longitude, tel,
                            info.getHotelName(), info.getLatitude(),
                            info.getLongitude(), info.getTelephone());
                    if (score == 0) continue;
                    if (score == 11 || score == -11) {
                        MeiTuanMatchResult result = new MeiTuanMatchResult();
                        Map<Integer, List<HotelHsjl>> fullScoreMap = Maps.newConcurrentMap();
                        fullScoreMap.put(score, Lists.newArrayList(info));
                        result.setHsjlScoreMap(fullScoreMap);
                        result.setZero(Boolean.FALSE);
                        return result;
                    }
                    if (scoreMap.containsKey(score)) {
                        scoreMap.get(score).add(info);
                    } else {
                        scoreMap.put(score, Lists.newArrayList(info));
                    }
                }
                MeiTuanMatchResult result = new MeiTuanMatchResult();
                result.setHsjlScoreMap(scoreMap);
                if (scoreMap.isEmpty()) {
                    result.setZero(Boolean.TRUE);
                } else {
                    result.setZero(Boolean.FALSE);
                }
                return result;
            }, executor));
            Map<Integer, List<HotelHsjl>> scoreMap = Maps.newHashMapWithExpectedSize(meituanInfos.size());
            for (CompletableFuture<MeiTuanMatchResult> future : futures) {
                MeiTuanMatchResult matchResult = future.get();
                // 如果0分，跳过此部分评分
                if (matchResult.getZero()) {
                    System.out.println("城市数据被跳过" + name);
                    notFoundId.add(meituanInfo.getId());
                    continue;
                }
                for (Map.Entry<Integer, List<HotelHsjl>> reslutEntry : matchResult.getHsjlScoreMap().entrySet()) {
                    Integer score = reslutEntry.getKey();
                    List<HotelHsjl> sameScoreHotels = reslutEntry.getValue();
                    if (scoreMap.containsKey(score)) {
                        scoreMap.get(score).addAll(sameScoreHotels);
                    } else {
                        scoreMap.put(score, sameScoreHotels);
                    }
                }
            }

            // 改酒店没有得分
            if (scoreMap.isEmpty()) {
                System.out.println("酒店数据被跳过" + name);
                log.info("酒店{}数据被跳过", name);
                notFoundId.add(meituanInfo.getId());
                continue;
            }
            List<MeituanHsjlMatchLab> uniqueList = Lists.newArrayListWithCapacity(1);
            List<MeituanHsjlMatchLab> multiList = Lists.newArrayListWithCapacity(meituanInfos.size());

            scoreMap.entrySet().stream()
                    .max(Comparator.comparingInt(entry1 -> Math.abs(entry1.getKey())))
                    .ifPresent(max -> createMatchResult7(meituanInfo, max, uniqueList, multiList));
            if (CollectionUtils.isNotEmpty(uniqueList)) {
                meituanHsjlMatchLabDao.saveBatch(uniqueList);
            }
            if (CollectionUtils.isNotEmpty(multiList)) {
                meituanHsjlMatchLabDao.saveBatch(multiList);
            }
        }
        System.out.println("找不到的酒店" + notFoundId.stream().map(Objects::toString).collect(Collectors.joining(",")));
    }

    private void createMatchResult7(MeituanInfo meituanInfo, Map.Entry<Integer, List<HotelHsjl>> max,
                                    List<MeituanHsjlMatchLab> uniqueList, List<MeituanHsjlMatchLab> multiList) {
        Integer score = max.getKey();
        List<HotelHsjl> infos = max.getValue();
        if (infos.size() > 1) {
            // -1分数过多，直接丢弃
            /*if (score.equals(-1)) {
                return;
            }*/
            for (HotelHsjl info : infos) {
                multiList.add(createOneMatchLab7(meituanInfo, info, score, true));
            }
            return;
        }
        uniqueList.add(createOneMatchLab7(meituanInfo, infos.get(0), score, false));
    }

    private MeituanHsjlMatchLab createOneMatchLab7(MeituanInfo meituanInfo, HotelHsjl info, Integer score, boolean b) {
        MeituanHsjlMatchLab lab = new MeituanHsjlMatchLab();
        lab.setMtId(meituanInfo.getId());
        lab.setMtName(meituanInfo.getName());
        lab.setMtLatitude(meituanInfo.getLatBaidu());
        lab.setMtLongitude(meituanInfo.getLonBaidu());
        lab.setMtAddress(meituanInfo.getAddress());
        lab.setMtTel(meituanInfo.getPhone());
        lab.setHsjlId(info.getHotelId().toString());
        lab.setHsjlName(info.getHotelName());
        lab.setHsjlAddress(info.getAddress());
        lab.setHsjlLatitude(info.getLatitude());
        lab.setHsjlLongitude(info.getLongitude());
        lab.setHsjlTel(info.getTelephone());
        lab.setScore(score);
        lab.setMultiMatch(b);
        double meter = GnMappingScoreHelper.calculateMeter(meituanInfo.getLatBaidu(), meituanInfo.getLonBaidu(),
                info.getLatitude(), info.getLongitude());
        lab.setDiffMeter(BigDecimal.valueOf(meter));
        return lab;
    }
}