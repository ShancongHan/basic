package com.example.basic.domain.hsjl.req;

import lombok.Data;

import java.util.List;

@Data
public class HsjlHotelInfoReq {

  private List<Long> hotelIds;

  /**
   * //按需请求，需要的内容请传入对应的参数；未传时，返回酒店的最基础的信息：酒店名称、城市、地址、集团品牌等等 "hotelFacilityNew", //酒店设施（含房型设施）
   * "hotelPolicysNew", //酒店政策（文本，非结构化，待开发） "breakfast", //酒店政策：早餐政策 "importantNotices", //酒店政策：重要通知
   * "parking", //酒店政策：停车场 "chargingParking", //酒店政策：充电车位 "hotelCertificates", //酒店资质，可能没有
   * "comment", //酒店评分 "hotelMeetingInfos", //酒店会议室信息 "hotelVideoInfos" //酒店视频信息
   * "hotelStructuredPolicies.childPolicy", //儿童政策 "hotelStructuredPolicies.extraBedPolicy" //加床政策
   */
  private List<String> settings;
}
