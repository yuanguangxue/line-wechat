package com.hp.line.engine.biz.service.process;

import com.hp.line.engine.biz.service.util.Constant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LineMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Object> map = new HashMap<String, Object>();

    public String getFrom(){
        return (String)get(Constant.FROM_USER_NAME);
    }

    public String getTo(){
        return (String)get(Constant.TO_USER_NAME);
    }

    public Long getCreateTime(){
        String createTime = (String)get(Constant.CREATE_TIME);
        if(createTime != null)
            return Long.parseLong(createTime);
        return null;
    }

    public String getMsgType(){
        return (String)get(Constant.MSG_TYPE);
    }

    public String getEvent(){
        return (String)get(Constant.EVENT);
    }

    public String getEventKey(){
        return (String)get(Constant.EVENT_KEY);
    }

    public String getContent(){
        return (String)get(Constant.CONTENT);
    }

    public String getPicUrl(){
        return (String)get(Constant.PIC_URL);
    }

    public String getMediaId(){
        return (String)get(Constant.MEDIA_ID);
    }

    public Double getLocationX(){
        String locationx = (String) get(Constant.LOCATION_X);
        if(locationx != null)
            return Double.parseDouble(locationx);
        return null;
    }

    public Double getLocationY(){
        String locationy = (String) get(Constant.LOCATION_Y);
        if(locationy != null)
            return Double.parseDouble(locationy);
        return null;
    }

    public Double getLongitude(){
        String longitude = (String) get(Constant.LONGITUDE);
        if(longitude != null)
            return Double.parseDouble(longitude);
        return null;
    }

    public Double getLatitude(){
        String latitude = (String) get(Constant.LATITUDE);
        if(latitude != null)
            return Double.parseDouble(latitude);
        return null;
//		return (Double) get(Constant.LATITUDE);
    }

   /* public IStatusTreeNode getStatus(){
        if(map.containsKey(Constant.CUSTOMER_STATUS))
            return (IStatusTreeNode) map.get(Constant.CUSTOMER_STATUS);
        return null;//没有任何状态
    }

    public void setStatus(IStatusTreeNode status){
        this.map.put(Constant.CUSTOMER_STATUS, status);
    }*/

    public Object get(String key){
        return this.map.get(key);
    }

    public Object set(String key, Object value){
        return this.map.put(key, value);
    }

    /**
     * 获取消息的xml源
     * @return
     */
    public String getSource(){
        return (String)this.get(Constant.MSG_XML_SOURCE);
    }
    /**
     * 获取消息的json源
     * @return
     *//*
    public JSONObject getJsonSource(){
        return (JSONObject)this.get(Constant.MSG_JSON_SOURCE);
    }*/

}
