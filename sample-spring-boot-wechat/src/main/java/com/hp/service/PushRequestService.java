package com.hp.service;


import com.hp.config.PushServerConfig;
import com.hp.enums.AudienceType;
import com.hp.enums.PlatformType;
import com.hp.enums.PushMsgType;
import com.hp.exception.PushParameterException;
import com.hp.model.*;
import com.hp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 接收推送请求，推送消息到客户端
 *
 * Created by yaoyasong on 2016/5/3.
 */
@Service
public class PushRequestService {

    private final int cpuSize = Runtime.getRuntime().availableProcessors();
    private final ExecutorService servicePool = Executors.newFixedThreadPool(cpuSize);

    @Autowired
    private PushRequestRepository pushRequestRepository;

    @Autowired
    private DeviceRepositoryCustom<UserDevice> deviceRepositoryCustom;

    @Autowired
    private PushMsgHandler pushMsgHandler;

    @Autowired
    private PushMsgRepository msgRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private PushServerConfig pushServerConfig;

    @Autowired
    private LineMessageService lineMessageService;

    public void saveRequest(PushRequest req) {
        req.setCreateTime(new Date());
        req.mask();
        req.setAppKey(null);
        pushRequestRepository.save(req);
    }

    /**
     * 推送消息
     * @param pushRequest
     */
    public void push(PushRequest pushRequest) {

        //分页获取受众
        Map<String,Object> params = getQueryParams(pushRequest);
        PushRequest orgiPushRequest = pushRequest.cloneMe();

        Pageable pageRequest = new PageRequest(0,200);
        Page<UserDevice> devicePage = deviceRepositoryCustom.findAll(params,pageRequest);
        pushMsgByPool(orgiPushRequest, devicePage);
        while (devicePage.hasNext()) {
            pageRequest = pageRequest.next();
            devicePage = deviceRepositoryCustom.findAll(params, pageRequest);
            pushMsgByPool(orgiPushRequest, devicePage);
        }
        this.saveRequest(pushRequest);
    }

    public synchronized void pushLineMsg(){
        PushRequest pushRequest = new PushRequest();
        pushRequest.setAppId(pushServerConfig.getAppId());
        pushRequest.setAudienceType(AudienceType.ALIAS);
        pushRequest.setPushMsgType(PushMsgType.LINE);
        List<LineMessage> lineMessageList = lineMessageService.findAll(new HashMap<>());
        for (LineMessage lineMessage : lineMessageList) {
            pushRequest.setCreateTime(new Date());
            pushRequest.setAlert(lineMessage.getText());
            pushRequest.setSmsMessage(lineMessage.getText());
            pushRequest.setSender(lineMessage.getUserId());
            push(pushRequest);
        }
    }

    private void pushMsgByPool(PushRequest orgiPushRequest, Page<UserDevice> devicePage) {
        final List<UserDevice> devices = devicePage.getContent();
        servicePool.submit(() -> pushMsgHandler.pushMsg(orgiPushRequest,devices));
    }

    private  Map<String, Object> getQueryParams(PushRequest pushRequest) {
        String appId = pushRequest.getAppId();
        List<String> as = pushRequest.getAudiences();
        PlatformType deviceType = pushRequest.getPlatform();
        AudienceType audienceType = pushRequest.getAudienceType();

        if (AudienceType.ALIAS != audienceType) {
            throw new PushParameterException("不支持的audience type : " + audienceType);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appId",appId);
        if (!CollectionUtils.isEmpty(as) && !as.get(0).equalsIgnoreCase("ALL")) {
            params.put("alias",as);
        }
        if (PlatformType.ALL != deviceType) {
            params.put("deviceType",deviceType);
        }
        return params;
    }

    public List<PushMsg> listMsg(){
        return msgRepository.findAll();
    }

    public List<PushRequest> pushRequestList(){
        return pushRequestRepository.findAll();
    }

    public List<UserDevice> userDeviceList(){
        return deviceRepository.findAll();
    }

    public List<Application>  applicationList(){
        return appRepository.findAll();
    }
}
