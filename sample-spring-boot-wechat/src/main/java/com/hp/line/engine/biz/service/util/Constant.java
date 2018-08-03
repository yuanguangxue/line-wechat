package com.hp.line.engine.biz.service.util;

public interface Constant {
    //消息体属性
    String FROM_USER_NAME = "FromUserName";
    String TO_USER_NAME = "ToUserName";
    String CREATE_TIME = "CreateTime";
    String MSG_TYPE = "MsgType";
    String EVENT = "Event";
    String EVENT_KEY = "EventKey";
    String CONTENT = "Content";
    String MSG_ID = "MsgId";
    String PIC_URL = "PicUrl";
    String MEDIA_ID = "MediaId";
    String FORMAT = "Format";
    String LOCATION_X = "Location_X";
    String LOCATION_Y = "Location_Y";
    String SCALE = "Scale";
    String LABEL = "Label";
    String LATITUDE = "Latitude";
    String LONGITUDE = "Longitude";

    //Event类型
    String EVENT_SUBSCRIBE = "subscribe";
    String EVENT_UNSUBSCRIBE = "unsubscribe";
    String EVENT_SCAN = "SCAN";
    String EVENT_LOCATION = "LOCATION";
    String EVENT_CLICK = "CLICK";
    String EVENT_VIEW = "VIEW";

    //消息的xml文本
    String MSG_XML_SOURCE = "MSG_XML_SOURCE";
    //获取消息的json源
    String MSG_JSON_SOURCE = "MSG_JSON_SOURCE";

    //消息类型
    String MSG_TYPE_TEXT = "text";
    String MSG_TYPE_IMAGE = "image";
    String MSG_TYPE_VOICE = "voice";
    String MSG_TYPE_SHORTVIDEO = "shortvideo";

    //消息中状态的字段名称
    String CUSTOMER_STATUS = "CUSTOMER_STATUS";

    //状态处理完成后的动作定义
    int STATUS_COMPLETE_EXIT = 0;
    int STATUS_COMPLETE_BACK = 1;
    int STATUS_COMPLETE_NOACTION = 2;

    //内容的备份.
    String CONTENT_BACKUP = "CONTENT_BACKUP";
    String PIC_URL_BACKUP = "PIC_URL_BACKUP";
}
