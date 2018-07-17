package com.hp.model;

import com.hp.enums.AudienceType;
import com.hp.enums.PlatformType;
import com.hp.enums.PushMsgType;
import com.hp.util.BeanUtils;
import org.hibernate.annotations.GenericGenerator;



import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 推送请求
 * Created by yaoyasong on 2016/4/28.
 */
@Entity
public class PushRequest implements Serializable {
    private static final long serialVersionUID = -7034897190745766939L;
    public static final long MSG_MAX_TTL = 259200;//离线消息最长存放时间三天（单位S）
    public static final long MSG_MIN_TTL = 300;//离线消息最少存放1小时
    @Id
    @GenericGenerator(name="systemUUID",strategy="uuid")
    @GeneratedValue(generator="systemUUID")
    private String id;
    private String appId;//
    private String appKey;//应用密钥
    private String tenantCode = "";//租户代码
    @Enumerated(EnumType.STRING)
    private PlatformType platform = PlatformType.ALL;
    @Enumerated(EnumType.STRING)
    private AudienceType audienceType = AudienceType.ALIAS;//受众的类型
    @Transient
    private List<String> audiences;//受众
    @Enumerated(EnumType.STRING)
    private PushMsgType pushMsgType = PushMsgType.MSG;
    private String listenFlag = "NOTF";
    //@Transient
    private String extra;
    private String alert;
    private String sound;
    private Integer badge;
    private String smsMessage;//短信消息
    private Date createTime;
    private Boolean needConfirm = false;//需要确认收到
    private Long duration;//持续时间(s)
    private String status = "1";//1.有效
    private String sender;
    @Enumerated(EnumType.STRING)
    private PlatformType sendDevice;
    private String target;

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

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public PlatformType getSendDevice() {
        return sendDevice;
    }

    public void setSendDevice(PlatformType sendDevice) {
        this.sendDevice = sendDevice;
    }

    public PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(PlatformType platform) {
        this.platform = platform;
    }

    public AudienceType getAudienceType() {
        return audienceType;
    }

    public void setAudienceType(AudienceType audienceType) {
        this.audienceType = audienceType;
    }

    public List<String> getAudiences() {
        return audiences;
    }

    public void setAudiences(List<String> audiences) {
        this.audiences = audiences;
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

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public Integer getBadge() {
        return badge;
    }

    public void setBadge(Integer badge) {
        this.badge = badge;
    }

    public String getSmsMessage() {
        return smsMessage;
    }

    public void setSmsMessage(String smsMessage) {
        this.smsMessage = smsMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(Boolean needConfirm) {
        this.needConfirm = needConfirm;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        if (duration > MSG_MAX_TTL || duration < 0) {
            duration = MSG_MAX_TTL;
        }
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * 屏蔽消息内容
     */
    public void mask() {
        this.smsMessage = "***";
        this.extra = "***";
    }

    @SuppressWarnings("uncheck")
    public PushRequest cloneMe() {
        PushRequest pushRequest = new PushRequest();
        try {
            BeanUtils.copyProperties(this,pushRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
        return pushRequest;
    }
}
