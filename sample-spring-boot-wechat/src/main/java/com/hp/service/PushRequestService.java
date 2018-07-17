package com.hp.service;


import com.hp.config.PushServerConfig;
import com.hp.enums.AudienceType;
import com.hp.enums.PlatformType;
import com.hp.enums.PushMsgType;
import com.hp.exception.PushParameterException;
import com.hp.model.*;
import com.hp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 接收推送请求，推送消息到客户端
 *
 * Created by yaoyasong on 2016/5/3.
 */
@Slf4j
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

    @Autowired
    private SenderLineMessageService senderLineMessageService;

    public void saveRequest(PushRequest req) {
        req.setCreateTime(new Date());
        req.mask();
        req.setAppKey(null);
        pushRequestRepository.save(req);
    }

    private String generateRandomKey() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 推送消息
     * @param pushRequest
     */
    public void push(PushRequest pushRequest) {
        //分页获取受众
        Map<String,Object> params = getQueryParams(pushRequest);
        pushRequest.setSenderUid(generateRandomKey());
        PushRequest orgiPushRequest = pushRequest.cloneMe();
        this.saveRequest(pushRequest);
        Pageable pageRequest = new PageRequest(0,200);
        Page<UserDevice> devicePage = deviceRepositoryCustom.findAll(params,pageRequest);
        pushMsgByPool(orgiPushRequest, devicePage);
        while (devicePage.hasNext()) {
            pageRequest = pageRequest.next();
            devicePage = deviceRepositoryCustom.findAll(params, pageRequest);
            pushMsgByPool(orgiPushRequest, devicePage);
        }
    }

    public synchronized void pushLineMsg(){
        PushRequest pushRequest = new PushRequest();
        pushRequest.setAppId(pushServerConfig.getAppId());
        pushRequest.setAudienceType(AudienceType.ALIAS);
        pushRequest.setPushMsgType(PushMsgType.LINE);
        List<LineMessage> lineMessageList = lineMessageService.findAll(new HashMap<>());
        for (LineMessage lineMessage : lineMessageList) {
            pushRequest.setCreateTime(new Date());
            pushRequest.setAlert("line message");
            pushRequest.setSmsMessage(lineMessage.getText());
            pushRequest.setExtra(lineMessage.getText());
            pushRequest.setSender(lineMessage.getUserId());
            pushRequest.setTarget("me");
            pushRequest.setTenantCode(lineMessage.getId());
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

    public List<PushMsg> getHistoryPushMsg(final String userId){
        List<PushMsg> msgList = new ArrayList<>();
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC,"createTime"));

        //获得今天聊天的历史记录
        List<PushRequest> list = pushRequestRepository.findAll((root, query, cb) -> {
            Predicate statusPredicate = cb.equal(root.get("status"),"1");
            Predicate tenantCodePredicate = cb.and(
                cb.isNotNull(root.get("tenantCode")),
                cb.notEqual(root.get("tenantCode"),"")
            );
            Predicate userIdPredicate = cb.equal(root.get("target"),userId);
            Predicate senderPredicate = cb.equal(root.get("sender"),userId);
            Predicate predicate = cb.or(userIdPredicate,senderPredicate);
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, day - 1);
            Predicate createTimePredicate = cb.greaterThanOrEqualTo(root.get("createTime"),calendar.getTime());
            return cb.and(statusPredicate,predicate,tenantCodePredicate,createTimePredicate);
        },sort);
        log.info("PushRequest list size is {} ",list.size());
        log.info("userId is {} ",userId);
        for (PushRequest pushRequest : list){
            PushMsg pushMsg = new PushMsg(pushRequest);
            //获取相关消息
            if(!StringUtils.isEmpty(pushMsg.getTenantCode())){
                //发给我的信息存在 LineMessage 中
                if(pushMsg.getTarget().equals("me")){
                    try {
                        LineMessage lineMessage = lineMessageService.findOne(pushMsg.getTenantCode());
                        pushMsg.setCreatedAt(new Date(lineMessage.getTimestamp()));
                        pushMsg.setExtra(lineMessage.getText());
                    }catch (Exception e){
                        log.error("error :",e);
                    }
                }else {
                    //我发出去的信息存在 SenderLineMessage 中
                    try {
                        SenderLineMessage senderLineMessage = senderLineMessageService.findOne(pushMsg.getTenantCode());
                        pushMsg.setCreatedAt(new Date(senderLineMessage.getTimestamp()));
                        pushMsg.setExtra(senderLineMessage.getText());
                    }catch (Exception e){
                        log.error("error :",e);
                    }
                }
            }
            msgList.add(pushMsg);
        }
        return msgList;
    }
}
