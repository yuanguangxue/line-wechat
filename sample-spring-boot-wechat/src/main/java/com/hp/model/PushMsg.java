package com.hp.model;

import com.hp.enums.PushMsgType;
import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 推送到客户端的提醒消息
 * Created by yaoyasong on 2016/4/29.
 */
@Entity
public class PushMsg implements Serializable{
    private static final long serialVersionUID = -7034897190745766939L;
    /**
     * 唯一标志
     */
    @Id
    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    private String id;
    private String appId;
    private String tenantCode;
    /**
     * 提醒标题
     */
    private String alert;
    /**
     * 消息内容
     */
    //@Transient
    private String extra;
    /**
     * 消息类型
     */
    @Enumerated(EnumType.STRING)
    private PushMsgType pushMsgType;
    /**
     * 客户端监听标志
     */
    private String listenFlag;
    /**
     * 是否需要确认
     */
    private Boolean needConfirm;
    /**
     * 到期时间
     */
    private Date expireAt;
    /**
     * 接收的设备ID
     */
    private String audienceDeviceId;

    /**
     * 创建时间
     * @param pushRequest
     */
    private Date createdAt;

    private String target;

    private String requestId;

    private String sender;

    public PushMsg() {
    }

    public PushMsg(PushRequest pushRequest) {

        this.appId = pushRequest.getAppId();
        this.tenantCode = pushRequest.getTenantCode();
        this.alert = pushRequest.getAlert();
        this.extra = pushRequest.getExtra();
        this.pushMsgType = pushRequest.getPushMsgType();
        this.listenFlag = pushRequest.getListenFlag();
        this.needConfirm = pushRequest.getNeedConfirm();
        this.target = pushRequest.getTarget();
        this.createdAt = pushRequest.getCreateTime();
        this.requestId = pushRequest.getSenderUid();
        this.sender = pushRequest.getSender();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public PushMsgType getPushMsgType() {
        return pushMsgType;
    }

    public void setPushMsgType(PushMsgType pushMsgType) {
        this.pushMsgType = pushMsgType;
    }

    public String getListenFlag() {
        return listenFlag;
    }

    public void setListenFlag(String listenFlag) {
        this.listenFlag = listenFlag;
    }

    public Boolean getNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(Boolean needConfirm) {
        this.needConfirm = needConfirm;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getAudienceDeviceId() {
        return audienceDeviceId;
    }

    public void setAudienceDeviceId(String audienceDeviceId) {
        this.audienceDeviceId = audienceDeviceId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
