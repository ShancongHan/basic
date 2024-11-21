package com.example.basic;

import com.alibaba.excel.util.DateUtils;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.basic.helper.MappingScoreHelper;
import com.example.basic.utils.IOUtils;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author han
 * @date 2024/5/23
 */
public class NoMatterTest {

    public static void main(String[] args) throws Exception {

        test5();
        /*int i = 585;
        int j = 93254;
        double x = (float) i / (double) j * 100;
        NumberFormat format = NumberFormat.getInstance();
        String format1 = format.format(x);
        System.out.println(x);
        System.out.println(format1);

        System.out.println(2<<6);

        List<String> compNos = Lists.newArrayList("1");
        addCompNos(compNos,"111");
        System.out.println(compNos);*/
//        String x = "[[\"汉庭\",\"张家港\",\"高铁站\",\"塘桥\",\"酒店\"]]";

        /*String x = """
                {"data":"\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n全季 / 酒店 / ( / 霍尔果斯 / 国 / 门店 / ) \\n\\n\\n分词次数统计：\\n\\n\\n\\n \\n全季 = 1 次；\\n酒店 = 1 次；\\n( = 1 次；\\n霍尔果斯 = 1 次；\\n国 = 1 次；\\n门店 = 1 次；\\n) = 1 次；\\n \\n\\n\\n\\n","code":100,"msg":"获取数据成功！"}
                """;
        String xx = x.substring(x.indexOf("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n全季"), x.indexOf("\\n\\n\\n分词次数统计：\\n\\n\\n\\n"));
        xx = xx.replace("\\n\\n\\n分词结果：(以 “/” 号为分割)\\n\\n\\n\\n", "");
        String s = xx.replaceAll("<em class=\"\\\\&quot;text-color-999\\\\&quot;\"> / </em>", ",");
        System.out.println(s);*/
        /*String dateTime = "2023-11-23T09:05:37.357Z";
        dateTime = dateTime.replace("T", " ");
        dateTime = dateTime.substring(0, dateTime.indexOf("."));
        System.out.println(dateTime);
        System.out.println(DateUtils.parseDate(dateTime));*/


        /*String s = handlerTel("63-22498745");
        String s2 = handlerTel("+63 2 22498745");
        System.out.println(s);
        System.out.println(s2);*/
        /*System.out.println(10216%10);

        String s= ",,,,";
        System.out.println(s.replaceAll(",",""));
        System.out.println(s);*/
        //System.out.println(URLDecoder.decode("/cn/%E6%9C%BA%E5%9C%BA%E5%9C%A8-%E4%B8%8D%E4%B8%B9-bt", StandardCharsets.UTF_8));
        //System.out.println(510100 % 10);

       /* String str = "";
        row80Char(str, '1');*/
        /*MappingScoreHelper.calculateScore("Hotel Beni Hamad","36.068446"
                ,"4.765295","01, Rue Frantz Fanon Bordj Bou Arreridj Algérie","00213-770521828"
                ,"Bodrum Park Resort",new BigDecimal("36.982981")
                ,new BigDecimal("7.561266"),"塞赖迪Yalıciftlik Mevkii 48410 Bodrum Turkey, 阿尔及利亚","0");*/
    }

    private static void test5() {
        String str1 = "汉庭优佳（上海金运路地铁站店）";
        String str2 = "汉庭优佳上海七莘路地铁站酒店";
        JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
        Double apply = jaroWinklerDistance.apply(str1, str2);
        System.out.println(apply);
    }

    private static void test4() throws Exception {
        String path = "C:\\wst_han\\打杂\\酒店统筹\\支付方式.json";
        String s = IOUtils.inputStreamToString(new FileInputStream(path));
        List<Pay> pay = JSON.parseArray(s, Pay.class);
        List<Pay> collect = pay.stream().sorted(Comparator.comparing(Pay::getCode)).collect(Collectors.toList());
        for (Pay pay1 : collect) {
            System.out.println(pay1.getCode()+","+pay1.getName());
        }
    }

    private static void test3() {
        List<String> test = Lists.newArrayList("1","2","3","4");
        test.remove(0);
        test.add("1");
        System.out.println(test);
    }

    private static void test2() {
        String x = "限时退订:2024-10-10 18:00:00前免费退订";
        System.out.println(x.substring(x.indexOf(":") + 1, 24));
    }

    private static void test() {
        String json = """
                {
                    "8098707":
                    {
                        "property_id": "8098707",
                        "name": "Apartments Mare",
                        "phone": "385-95-951313142",
                        "fax": "385-20-418450",
                        "category":
                        {
                            "id": "6",
                            "name": "Guesthouse"
                        },
                        "business_model":
                        {
                            "expedia_collect": true,
                            "property_collect": false
                        },
                        "rank": 104467,
                        "checkin":
                        {
                            "begin_time": "1:00 PM",
                            "end_time": "11:00 PM",
                            "instructions": "<ul>  <li>Extra-person charges may apply and vary depending on property policy</li><li>Government-issued photo identification and a cash deposit may be required at check-in for incidental charges</li><li>Special requests are subject to availability upon check-in and may incur additional charges; special requests cannot be guaranteed</li><li>This property is professionally cleaned</li><li>Please note that cultural norms and guest policies may differ by country and by property; the policies listed are provided by the property</li>  </ul>",
                            "special_instructions": "This property offers transfers from the airport (surcharges may apply). To arrange pick-up, guests must contact the property 24 hours prior to arrival, using the contact information on the booking confirmation. There is no front desk at this property. To make arrangements for check-in please contact the property at least 72 hours before arrival using the information on the booking confirmation. If you are planning to arrive after 10:00 PM please contact the property in advance using the information on the booking confirmation. Guests must contact the property in advance for check-in instructions. The host will greet guests on arrival. For more details, please contact the property using the information on the booking confirmation.  Guests should notify this property in advance of their arrival time.",
                            "min_age": 18
                        },
                        "checkout":
                        {
                            "time": "10:00 AM"
                        },
                        "fees":
                        {
                            "mandatory": "<p>You'll be asked to pay the following charges at the property. Fees may include applicable taxes:</p> <ul><li>A tax is imposed by the city and collected at the property. This tax is adjusted seasonally and might not apply year round. Other exemptions or reductions might apply. For more details, please contact the property using the information on the reservation confirmation received after booking. </li><li>A tax is imposed by the city: From 1 October - 31 March, EUR 1.85 per person, per night for adults; EUR 0.93 per night for guests aged 12-17 years old. This tax does not apply to children under 12 years of age. </li> <li> A tax is imposed by the city: From 1 April - 30 September, EUR 2.65 per person, per night for adults; EUR 1.33 per night for guests aged 12-17 years old. This tax does not apply to children under 12 years of age. </li></ul> <p>We have included all charges provided to us by the property. </p>",
                            "optional": "<ul> <li>Airport shuttle fee: EUR 45 per vehicle (one way)</li><li>Late check-out is available for a fee (subject to availability)</li></ul> <p>The above list may not be comprehensive. Fees and deposits may not include tax and are subject to change. </p>"
                        },
                        "policies":
                        {
                            "know_before_you_go": "<ul>  <li>The property is professionally cleaned.</li><li>Contactless check-in and contactless check-out are available.</li> </ul>"
                        },
                        "attributes":
                        {
                            "pets":
                            {
                                "2050":
                                {
                                    "id": "2050",
                                    "name": "Pets not allowed"
                                }
                            },
                            "general":
                            {
                                "1073745142":
                                {
                                    "id": "1073745142",
                                    "name": "Professional property host/manager"
                                },
                                "1073745055":
                                {
                                    "id": "1073745055",
                                    "name": "Contactless check-out is available"
                                },
                                "2557":
                                {
                                    "id": "2557",
                                    "name": "Cash deposit required"
                                },
                                "1073745053":
                                {
                                    "id": "1073745053",
                                    "name": "Property confirms they are implementing guest safety measures"
                                },
                                "2545":
                                {
                                    "id": "2545",
                                    "name": "No cribs (infant beds) available"
                                },
                                "2544":
                                {
                                    "id": "2544",
                                    "name": "No rollaway/extra beds available"
                                },
                                "1073744990":
                                {
                                    "id": "1073744990",
                                    "name": "Property uses a professional cleaning service"
                                },
                                "1073745013":
                                {
                                    "id": "1073745013",
                                    "name": "Property confirms they are implementing enhanced cleaning measures"
                                },
                                "1073744992":
                                {
                                    "id": "1073744992",
                                    "name": "Property is cleaned with disinfectant"
                                },
                                "1073745049":
                                {
                                    "id": "1073745049",
                                    "name": "Bed sheets and towels are washed at a temperature of at least 60°C/140°F"
                                },
                                "1073745006":
                                {
                                    "id": "1073745006",
                                    "name": "Contactless check-in is available"
                                },
                                "1073745051":
                                {
                                    "id": "1073745051",
                                    "name": "Commonly-touched surfaces are cleaned with disinfectant"
                                },
                                "2549":
                                {
                                    "id": "2549",
                                    "name": "No elevators"
                                }
                            }
                        },
                        "amenities":
                        {
                            "56":
                            {
                                "id": "56",
                                "name": "Airport transportation (surcharge)",
                                "categories":
                                [
                                    "airport_transfer"
                                ]
                            },
                            "2137":
                            {
                                "id": "2137",
                                "name": "Smoke-free property"
                            },
                            "103":
                            {
                                "id": "103",
                                "name": "Snorkeling nearby"
                            },
                            "1073744740":
                            {
                                "id": "1073744740",
                                "name": "Wheelchair accessible – no"
                            },
                            "347":
                            {
                                "id": "347",
                                "name": "Hiking/biking trails nearby"
                            },
                            "1073745332":
                            {
                                "id": "1073745332",
                                "name": "No accessible shuttle"
                            },
                            "4454":
                            {
                                "id": "4454",
                                "name": "Free parking nearby",
                                "categories":
                                [
                                    "parking"
                                ]
                            },
                            "4003":
                            {
                                "id": "4003",
                                "name": "Luggage storage"
                            },
                            "4514":
                            {
                                "id": "4514",
                                "name": "Terrace"
                            },
                            "2390":
                            {
                                "id": "2390",
                                "name": "Free WiFi",
                                "categories":
                                [
                                    "wifi"
                                ]
                            }
                        },
                   \s
                        "onsite_payments":
                        {
                            "currency": "EUR"
                        },
                   \s
                        "dates":
                        {
                            "added": "1995-01-01T05:00:00.000Z",
                            "updated": "2024-09-18T19:39:51.137Z"
                        },
                        "descriptions":
                        {
                            "amenities": "Take in the views from a terrace and make use of amenities such as complimentary wireless internet access.",
                            "business_amenities": "A roundtrip airport shuttle is provided for a surcharge (available 24 hours).",
                            "rooms": "Make yourself at home in one of the 6 individually decorated guestrooms, featuring refrigerators and LCD televisions. Complimentary wireless internet access keeps you connected, and satellite programming is available for your entertainment. Conveniences include desks, housekeeping is provided weekly, and irons/ironing boards can be requested.",
                            "attractions": "Distances are displayed to the nearest 0.1 mile and kilometer. <br /> <p>Bellevue Beach - 0.5 km / 0.3 mi <br /> Boninovo Cemetery - 1 km / 0.6 mi <br /> Mercante - 1.2 km / 0.8 mi <br /> Luka Gruz - 1.4 km / 0.9 mi <br /> Dubrovnik Shopping Minčeta - 1.5 km / 0.9 mi <br /> Rochester Institute of Technology Croatia - 1.8 km / 1.1 mi <br /> Gruz Open Market - 1.8 km / 1.1 mi <br /> Šulić Beach - 1.9 km / 1.2 mi <br /> Gradac Park - 1.9 km / 1.2 mi <br /> Lovrijenac Fort - 2 km / 1.2 mi <br /> Pile Gate - 2 km / 1.2 mi <br /> Danče Beach - 2.1 km / 1.3 mi <br /> Visia Dubrovnik 5D Multimedia Museum - 2.1 km / 1.3 mi <br /> St. Saviour Church - 2.1 km / 1.3 mi <br /> Gruz Harbor - 2.1 km / 1.3 mi <br /> </p><p>The preferred airport for Apartments Mare is Dubrovnik Airport (DBV) - 21.1 km / 13.1 mi </p>",
                            "location": "With a stay at Apartments Mare in Dubrovnik (Gorica), you'll be within a 5-minute drive of Pile Gate and Dubrovnik Ferry Port.  This guesthouse is 1.6 mi (2.6 km) from Lapad Beach and 1.8 mi (2.9 km) from Gruz Harbor.",
                            "headline": "In Dubrovnik (Gorica)"
                        },
                        "statistics":
                        {
                            "52":
                            {
                                "id": "52",
                                "name": "Total number of rooms - 6",
                                "value": "6"
                            }
                        },
                        "chain":
                        {
                            "id": "0",
                            "name": "Independent"
                        },
                        "brand":
                        {
                            "id": "0",
                            "name": "Independent"
                        },
                        "spoken_languages":
                        {
                            "en":
                            {
                                "id": "en",
                                "name": "English"
                            },
                            "hr":
                            {
                                "id": "hr",
                                "name": "Croatian"
                            }
                        },
                        "multi_unit": true,
                        "payment_registration_recommended": false,
                        "supply_source": "expedia"
                    }
                }
                """;
        JSONObject jsonObject = JSON.parseObject(json);
        JSONObject jsonObject2 = (JSONObject) jsonObject.get("8098707");
        System.out.println(jsonObject2);
    }

    private static void addCompNos(List<String> compNos, String s) {
        if (CollectionUtils.isEmpty(compNos)){
            compNos = new ArrayList<>();
        }
        if (StringUtils.hasLength(s)){
            compNos.add("看吧");
        }
    }

    public static String handlerTel(String telephone){
        return StringUtils.hasLength(telephone) ? telephone.replaceAll("-", "")
                .replaceAll("\\+", "")
                .replaceAll(" ", " ")
                .replaceAll(" +", "")
                .replaceAll("\\s+", "")
                .toUpperCase() : "***";
    }

    public static List<String> row80Char(String str, Character featureSymbol) {
        int count = 0;
        int last1Index = -1;
        int subStartIndex = 0;
        List<String> tmpStrList = new ArrayList<>();
        for (int k = 0; k < str.length(); k++) {
            char ch = str.charAt(k);
            if (LdkjStr.isChinese(ch)) {
                count += 2;
            } else {
                count++;
            }
            if (featureSymbol != null) {
                if (featureSymbol == ch) {
                    last1Index = k;
                }
            } else {
                last1Index = k;
            }

            if (count >= 80) {
                tmpStrList.add(str.substring(subStartIndex, last1Index));
                count = 0;
                k = subStartIndex = last1Index;
            }
        }
        if (tmpStrList.size() > 0) {
            if (subStartIndex < str.length() - 1) {
                tmpStrList.add(str.substring(subStartIndex));
            }
        }
        return tmpStrList;
    }



}
