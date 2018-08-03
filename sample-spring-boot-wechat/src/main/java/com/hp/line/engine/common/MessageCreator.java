package com.hp.line.engine.common;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageCreator {

    public static final String ROOT_TAG = "xml";
   /* @Autowired
    private MessageEntityDaoService messageDaoService;
    @Autowired
    private AutoReplyDaoService autoReplyDao;
    @Autowired
    private MsgGenerator msgGenerator;



    public String createXmlTextMessage(String fromUserName, String toUserName, String content){
        JSONObject obj = new JSONObject();
        obj.put("ToUserName", fromUserName);
        obj.put("FromUserName", toUserName);
        obj.put("CreateTime", System.currentTimeMillis() / 1000);
        obj.put("MsgType", "text");
        obj.put("Content", content);

        JSONObject rootObj = new JSONObject();
        rootObj.put(ROOT_TAG, obj);
        String xmlStr = XML.toString(rootObj);
        return xmlStr;
    }

    public String createJsonTextMessage(String touser, String content){
        net.sf.json.JSONObject msg = new net.sf.json.JSONObject();
        msg.put("touser", touser);
        msg.put("msgtype", "text");
        net.sf.json.JSONObject text = new net.sf.json.JSONObject();
        text.put("content", content);
        msg.put("text", text);
        return msg.toString();
    }

    public String createJsonImageMessage(String touser, String media_id){
        net.sf.json.JSONObject msg = new net.sf.json.JSONObject();
        msg.put("touser", touser);
        msg.put("msgtype", "image");

        net.sf.json.JSONObject image = new net.sf.json.JSONObject();
        image.put("media_id", media_id);

        msg.put("image", image);
        return msg.toString();
    }

    //**************************根据key创建消息**************************

    public String getJsonMsgByKey(String openid, String key){
        AutoReply autoReply = this.autoReplyDao.findByEventKey(key);
        if(autoReply != null && autoReply.getMessage() != null){
            String msg = this.msgGenerator.getJsonMessage(autoReply.getMessage(), openid);
            return msg;
        }
        return "";
    }

    public String getXmlMsgByKey(String openid, String weixId, String key){
        AutoReply autoReply = this.autoReplyDao.findByEventKey(key);
        if(autoReply != null && autoReply.getMessage() != null){
            String msg = this.msgGenerator.getXmlMessage(autoReply.getMessage(), weixId, openid);
            return msg;
        }
        return "";
    }

    //************************根据消息id创建消息***************************

    public String getJsonMsgByMessageId(String openid, Long messageid){
        MessageEntity message = messageDaoService.findById(messageid);
        if(message != null){
            String msg = this.msgGenerator.getJsonMessage(message, openid);
            return msg;
        }
        return "";
    }

    public String getXmlMsgByMessageId(String openid, String weixId, Long messageid){
        MessageEntity message = messageDaoService.findById(messageid);
        if(message != null){
            String msg = this.msgGenerator.getXmlMessage(message, weixId, openid);
            return msg;
        }
        return "";
    }*/
}
