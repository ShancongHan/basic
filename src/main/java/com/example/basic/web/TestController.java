package com.example.basic.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.example.basic.dao.ExpediaInfoDao;
import com.example.basic.dao.JdJdbGjDao;
import com.example.basic.dao.ZhJdJdbGjMappingDao;
import com.example.basic.domain.DidaAndEpsMapping;
import com.example.basic.domain.MatchResult;
import com.example.basic.domain.SanYiExcelDataBean;
import com.example.basic.entity.ExpediaInfo;
import com.example.basic.entity.JdJdbGj;
import com.example.basic.entity.SanyiNearHotel;
import com.example.basic.entity.ZhJdJdbGjMapping;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hankcs.hanlp.restful.HanLPClient;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author han
 * @date 2024/9/14
 */
@RestController
@RequestMapping(value = "test")
public class TestController {

    @Resource
    private ZhJdJdbGjMappingDao zhJdJdbGjMappingDao;
    @Resource
    private JdJdbGjDao jdJdbGjDao;
    @Resource
    private ExpediaInfoDao expediaInfoDao;

    @PostMapping(value = "match1")
    public void match1() throws Exception {
        String fileName = "C:\\Users\\WST\\Documents\\WXWork\\1688858213530902\\Cache\\File\\2025-01\\比对111111.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<MatchResult> dataList = Lists.newArrayListWithCapacity(600000);
        EasyExcel.read(inputStream, MatchResult.class, new PageReadListener<MatchResult>(dataList::addAll, 1000)).headRowNumber(1).sheet().doRead();
        List<MatchResult> results = dataList.stream().filter(e -> !"1".equals(e.getTest())).toList();
        List<String> didaHotelIds = results.stream().map(MatchResult::getDidaHotelId).toList();
        List<ZhJdJdbGjMapping> mappings = zhJdJdbGjMappingDao.selectLocalIdByPlatIds(didaHotelIds);
        Map<String, Long> map = mappings.stream().collect(Collectors.toMap(ZhJdJdbGjMapping::getPlatId, ZhJdJdbGjMapping::getLocalId));
        List<ZhJdJdbGjMapping> saveList = Lists.newArrayListWithCapacity(80);
        for (MatchResult result : results) {
            String didaHotelId = result.getDidaHotelId();
            Long localId = map.get(didaHotelId);
            ZhJdJdbGjMapping zhJdJdbGjMapping = new ZhJdJdbGjMapping();
            zhJdJdbGjMapping.setLocalId(localId);
            zhJdJdbGjMapping.setPlat(2000073);
            zhJdJdbGjMapping.setPlatId(result.getEpsHotelId());
            saveList.add(zhJdJdbGjMapping);
        }
        System.out.println(saveList);
        zhJdJdbGjMappingDao.insertBatch(saveList);
    }

    @PostMapping(value = "match")
    public Map<String, List> match() throws Exception {
        List<String> list = Lists.newArrayList("159","6496","6837","7914","9407","11651","11946","20269","22131","24105","31453","35857","39579","39595","53039","54880","62647","69833","69846","91183","107264","120050","142254","158278","162934","168192","182181","185029","187369","196576","199615","209662","264279","276338","276755","293267","293939","330526","376872","402465","407544","423042","423061","452138","455811","469759","471762","622459","835682","860350","871152","883477","922546","933284","943979","979100","989457","1002298","1002738","1024809","1026587","1115471","1116791","1159718","1205671","1387341","1412927","1415986","1420970","1421404","1425411","1435020","1556214","1557934","1561972","1562307","1562573","1568325","1571359","1573728","1577341","1578596","1578803","1581620","1583277","1585640","1587645","1592993","1595174","1599478","1609041","1638961","1641220","1650107","2143888");
        String fileName = "C:\\wst_han\\打杂\\酒店统筹\\国际酒店匹配\\拿回来的\\EPS和dida匹配关系forWSTLY0108.xlsx";
        InputStream inputStream = new FileInputStream(fileName);
        List<DidaAndEpsMapping> dataBeans = Lists.newArrayListWithCapacity(600000);
        EasyExcel.read(inputStream, DidaAndEpsMapping.class, new PageReadListener<DidaAndEpsMapping>(dataBeans::addAll, 1000)).headRowNumber(1).sheet().doRead();
        Map<String, List<DidaAndEpsMapping>> didaMap = dataBeans.stream().collect(Collectors.groupingBy(DidaAndEpsMapping::getDidaHotelId));
        List<MatchResult> results = Lists.newArrayListWithCapacity(100);
        List<String> notFound = Lists.newArrayListWithCapacity(100);
        boolean test = true;
        for (String didaHotelId : list) {
            List<DidaAndEpsMapping> didaAndEpsMappings = didaMap.get(didaHotelId);
            if (CollectionUtils.isNotEmpty(didaAndEpsMappings)) {
                if (didaAndEpsMappings.size() > 1) {
                    System.out.println("----------------------" + didaAndEpsMappings);
                    continue;
                }
                MatchResult result = new MatchResult();
                result.setDidaHotelId(didaHotelId);
                String epsHotelId = didaAndEpsMappings.get(0).getEpsHotelId();
                result.setEpsHotelId(epsHotelId);
                results.add(result);
            } else {
                test = false;
                notFound.add(didaHotelId);
            }
        }
        System.out.println(String.join(",", notFound));
        if (!test) {
            return null;
        }
        List<String> epsHotelIds = results.stream().map(MatchResult::getEpsHotelId).toList();
        List<ExpediaInfo> expediaInfos = expediaInfoDao.selectByHotelIds(epsHotelIds);
        Map<String, ExpediaInfo> epsHotelIdMap = expediaInfos.stream().collect(Collectors.toMap(ExpediaInfo::getHotelId, e -> e));
        List<String> didaHotelIds = results.stream().map(MatchResult::getDidaHotelId).toList();
        List<ZhJdJdbGjMapping> mappings = zhJdJdbGjMappingDao.selectLocalIdByPlatIds(didaHotelIds);
        Map<String, Long> map = mappings.stream().collect(Collectors.toMap(ZhJdJdbGjMapping::getPlatId, ZhJdJdbGjMapping::getLocalId));
        List<Long> localIds = mappings.stream().map(ZhJdJdbGjMapping::getLocalId).collect(Collectors.toList());
        List<JdJdbGj> jdJdbGjs = jdJdbGjDao.selectInfoByLocalIds(localIds);
        Map<Long, JdJdbGj> idMap = jdJdbGjs.stream().collect(Collectors.toMap(JdJdbGj::getId, e -> e));
        for (MatchResult result : results) {
            String epsHotelId = result.getEpsHotelId();
            ExpediaInfo expediaInfo = epsHotelIdMap.get(epsHotelId);
            if (expediaInfo!= null) {
                result.setEpsHotelName(expediaInfo.getName());
                result.setEpsHotelNameEn(expediaInfo.getNameEn());
            }
            String didaHotelId = result.getDidaHotelId();
            Long localId = map.get(didaHotelId);
            JdJdbGj jdJdbGj = idMap.get(localId);
            if (jdJdbGj != null) {
                result.setDidaHotelName(jdJdbGj.getNameCn());
                result.setDidaHotelNameEn(jdJdbGj.getName());
            }
        }
        String outFile = "C:\\wst_han\\打杂\\酒店统筹\\比对111111.xlsx";
        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(outFile, MatchResult.class).sheet("比对一下").doWrite(results);
        return null;
    }

    @PostMapping(value = "fenci")
    public Map<String, List> fenci(String key) {
        HanLPClient client = new HanLPClient("https://hanlp.hankcs.com/api", null); // Replace null with your auth
        String[] tasks = new String[2];
        tasks[0] = "tok/fine";
        tasks[1] = "tok/coarse";
        try {
            return client.parse(key, tasks, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "fenci2")
    public List<String> fenci2(String key) {
        String url = "http://www.foryet.net/api/actions.aspx?action=segwords";
        Connection connection = Jsoup.connect(url).data("content", key).timeout(10000)
                .method(Connection.Method.POST);
        Document doc = null;
        try {
            doc = connection.execute().parse();
            String data = doc.text();
            String body = data.substring(data.indexOf("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n全季"), data.indexOf("\\n\\n\\n分词次数统计：\\n\\n\\n\\n"));
            body = body.replace("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n", "");
            String[] split = body.split(" / ");
            return Arrays.asList(split);
        } catch (IOException e) {
            System.out.println(Throwables.getStackTraceAsString(e));
        }
        return null;
    }

    @PostMapping(value = "check")
    public List<String> check(String key) {
        List<String> list = Lists.newArrayList("598802594326491136",
                "598802661728956416",
                "598785908340797440",
                "598785875524562944",
                "598801280792113152",
                "598784765736890368",
                "598782946432364544",
                "598801010787987456",
                "598802766641082368",
                "598803775614464000",
                "598810540628357120",
                "598803819910508544",
                "598803144199745536",
                "598802231351422976",
                "598783076636143616",
                "598802661666041856",
                "598788712862167040",
                "598785250984308736",
                "598782948147834880",
                "598803612112105472",
                "598803169256517632",
                "598802952276783104",
                "598794187137724416",
                "598802501531709440",
                "598789815309807616",
                "598803066542206976",
                "598802384888115200",
                "598801072196792320",
                "598802624672280576",
                "598794888744120320",
                "598803327792820224",
                "598799029587521536",
                "598802775746916352",
                "598813477073170432",
                "598802824765747200",
                "598801860642058240",
                "598801728068497408",
                "598802906248491008",
                "598801004416839680",
                "598803639081480192",
                "598814837755719680",
                "598801277382144000",
                "598785565062180864",
                "598796638611943424",
                "598788093434769408",
                "598792538222604288",
                "598797340499357696",
                "598783268869484544",
                "598788776888217600",
                "598784125526716416",
                "598802456950452224",
                "598793140537241600",
                "598803374626418688",
                "598797301270032384",
                "598798170975744000",
                "598797230537289728",
                "598799408870043648",
                "598783236963414016",
                "598783276154990592",
                "598786150045954048",
                "598785136756633600",
                "598782752877817856",
                "598796624129011712",
                "598799356441243648",
                "598792481712746496",
                "598787417090666496",
                "598787481884274688",
                "598784273547898880",
                "598789521968574464",
                "598792856448643072",
                "598786892127383552",
                "598784532567142400",
                "598802324104261632",
                "598785474242916352",
                "598803746401136640",
                "598803171827625984",
                "598788608168144896",
                "598784168631578624",
                "598784125342167040",
                "598784395518259200",
                "598797333897523200",
                "598799155534082048",
                "598799189717659648",
                "598801097098375168",
                "598783589180092416",
                "598783590358691840",
                "598783590002176000",
                "598799179936542720",
                "598790724462948352",
                "598784838604533760",
                "598802925370322944",
                "598783715579637760",
                "598783716435275776",
                "598783086803136512",
                "598786248977002496",
                "598785935113039872",
                "598786797524856832",
                "598797110345314304",
                "598802958060728320",
                "598783357683871744",
                "598803616134443008",
                "598797419083837440",
                "598790868059140096",
                "598783031295717376",
                "598787489626959872",
                "598784532596502528",
                "598801085379489792",
                "598789826491822080",
                "598799170239311872",
                "598799179621969920",
                "598784149883039744",
                "598785685635837952",
                "598796315398877184",
                "598784170317688832",
                "598800961186148352",
                "598783415036784640",
                "598810095734337536",
                "598784531975745536",
                "598803888592236544",
                "598796014432399360",
                "598787153742901248",
                "598788887617843200",
                "598788184694435840",
                "598782779184492544",
                "598785159825305600",
                "598796504587153408",
                "598784169285890048",
                "598786206509674496",
                "598795765697589248",
                "598787903575404544",
                "598792805194248192",
                "598798666616647680",
                "598783308400799744",
                "598787678966231040",
                "598786423380357120",
                "598802406404894720",
                "598801152794537984",
                "598796428645085184",
                "598788699830464512",
                "598783194500280320",
                "598783196366745600",
                "598783198455508992",
                "598804033127952384",
                "598783226100166656",
                "598800961492332544",
                "598805042013253632",
                "598785330088882176",
                "598802478676946944",
                "598783416026640384",
                "598799186408353792",
                "598782863007657984",
                "598788821922459648",
                "598803615010369536",
                "598802239949746176",
                "598784612133089280",
                "598784142303932416",
                "598784357610139648",
                "598783088761876480",
                "598783807531364352",
                "600210658876743680",
                "598783598139125760",
                "598782838378704896",
                "598793586957987840",
                "598803039501529088",
                "598791721914576896",
                "598795546977218560",
                "598788348712693760",
                "598785353946083328",
                "598785343674232832",
                "598785343762313216",
                "598801074134560768",
                "598798034266599424",
                "598782988127940608",
                "598783075524653056",
                "598782937230061568",
                "598782852219908096",
                "598797759535493120",
                "598784931038605312",
                "598788018943930368",
                "598783152137809920",
                "598793244413374464",
                "598802358422056960",
                "598784438933499904",
                "598784099652055040",
                "598801156225478656",
                "598783415070339072",
                "598787025715965952",
                "598800958304661504",
                "598788665428783104",
                "598784477244272640",
                "598784243353104384",
                "598797323231408128",
                "598794563853332480",
                "598802042205089792",
                "598787981920808960",
                "598802609631506432",
                "598784066735157248",
                "598787375453810688",
                "598787041343942656",
                "598788495261675520",
                "598803556831178752",
                "598786665471389696",
                "598802636290502656",
                "598800962192781312",
                "598794029540945920",
                "598785381074841600",
                "598786860770766848",
                "598803089430523904",
                "598782945832579072",
                "598783078238367744",
                "598788643010228224",
                "598803233072852992",
                "598782947954896896",
                "598802756423757824",
                "598786806542610432",
                "598783434175393792",
                "598802091123257344",
                "598783273466441728",
                "598786441550082048",
                "598783087658774528",
                "598802438969470976",
                "598798712896598016",
                "598785618833158144",
                "598785139818475520",
                "598786575176413184",
                "598795816243146752",
                "598782836742926336",
                "598799145396449280",
                "598782789116604416",
                "598782788978192384",
                "598782838059937792",
                "598782841553793024",
                "598782792572710912",
                "598799156855287808",
                "598801314858250240",
                "598801008837636096",
                "598784392288645120",
                "598783014409449472",
                "598801163527761920",
                "598802669320646656",
                "598791999896268800",
                "598783681689661440",
                "598784105658298368",
                "598797288418684928",
                "598796320415264768",
                "598802832298717184",
                "598782973703729152",
                "598793407022346240",
                "598784532009299968",
                "598785352255778816",
                "598784563072315392",
                "598797523752693760",
                "598801151880179712",
                "598784838906523648",
                "598799211704201216",
                "598787674482520064",
                "598802246870347776",
                "598783285181132800",
                "598782928770150400",
                "598795354949398528",
                "598784156321296384",
                "598784197224148992",
                "598802561292152832",
                "598784907642777600",
                "598782794560811008",
                "598784505186725888",
                "598786804969746432",
                "598801037979660288",
                "598788040452321280",
                "598787532287225856",
                "598786424085000192",
                "598795853983494144",
                "598783034177204224",
                "598810178290823168",
                "598786713458421760",
                "598784169654988800",
                "598787029927047168",
                "598791617665150976",
                "598790015650738176",
                "598795640938016768",
                "598786355868839936",
                "598798725097828352",
                "598802472167387136",
                "598784858850439168",
                "598784858821079040",
                "598783766674649088",
                "598793186511007744",
                "598795932496670720",
                "598782890320965632",
                "598802697061773312",
                "598782780082073600",
                "598801099715620864",
                "598800352563277824",
                "598783358921191424",
                "598784946016464896",
                "598795615017218048",
                "598791503949180928",
                "598787708812898304",
                "598788736769699840",
                "598796073446256640",
                "598785383469789184",
                "598793852390322176",
                "598788871020982272",
                "598795368241147904",
                "598801632815853568",
                "598785925235453952",
                "598794330561949696",
                "598786207029768192",
                "598802125013233664",
                "598786514526777344",
                "598799189403086848",
                "598786236410867712",
                "598784588821147648",
                "598801930842124288",
                "598802755501010944",
                "598797753055293440",
                "598783266113826816",
                "598797532522983424",
                "598785343732953088",
                "598782946621108224",
                "598792987487088640",
                "598816022910840832",
                "598803603006271488",
                "598793691450683392",
                "598784663894994944",
                "598788703211073536",
                "598785767605121024",
                "598785984429666304",
                "598797209100201984",
                "598796590473916416",
                "598795513984823296",
                "598787233736667136",
                "598785019341287424",
                "598799180590854144",
                "598786812376887296",
                "598795141048283136",
                "598801109287022592",
                "598799238216396800",
                "598800958275301376",
                "598798057373020160",
                "598785932160249856",
                "598795953749209088",
                "598786466430693376",
                "598799188174155776",
                "598783227777888256",
                "598784663442010112",
                "598783911755624448",
                "598787690865471488",
                "598792718749642752",
                "598799586540761088",
                "598797549149204480",
                "598795670440751104",
                "598799232956739584",
                "598797503204798464",
                "598785250925588480",
                "598783806981910528",
                "598786100335063040",
                "598790613720739840",
                "598785330059522048",
                "598793737491558400",
                "598799103000424448",
                "598787825632653312",
                "598797540957728768",
                "598794659907088384",
                "598788139647610880",
                "598801263788404736",
                "598783659971555328",
                "598785353207885824",
                "598784356154716160",
                "598799923976712192",
                "598784356578340864",
                "598784355689148416",
                "598786272725151744",
                "598787744368013312",
                "598786530238640128",
                "598799938417700864",
                "598786875115286528",
                "598799120226430976",
                "598784356699975680",
                "598799159392841728",
                "598782836906504192",
                "598786546814529536",
                "598794588985602048",
                "598785279178420224",
                "598802914079256576",
                "598796322109763584",
                "598783526588493824",
                "598783525577666560",
                "598784564464824320",
                "598785797627949056",
                "598783297067790336",
                "598797061666222080",
                "598802589482070016",
                "598785418169266176",
                "598791999527170048",
                "598802087256109056",
                "598785607726641152",
                "598785133283749888",
                "598784155587293184",
                "598787494081310720");

        List<ZhJdJdbGjMapping> epsList = zhJdJdbGjMappingDao.selectEpsListByLocalIds(list);
        Set<Long> epsLocalIdSet = epsList.stream().map(ZhJdJdbGjMapping::getLocalId).collect(Collectors.toSet());
        Set<String> notFound = Sets.newHashSetWithExpectedSize(400);
        for (String localId : list) {
            if (!epsLocalIdSet.contains(Long.valueOf(localId))) {
                notFound.add(localId);
            }
        }
        System.out.printf("总共酒店：%s，eps有映射酒店：%s，没有映射的：%s%n", list.size(), epsList.size(), notFound.size());
        List<String> strings = zhJdJdbGjMappingDao.selectListByLocalIds(notFound);
        System.out.println(strings.size());
        System.out.printf("eps找不到的映射的道旅id为：%s", String.join(",", strings));
        System.out.printf("eps找不到的映射的本地id为：%s", String.join(",", notFound));
        return null;
    }
}
