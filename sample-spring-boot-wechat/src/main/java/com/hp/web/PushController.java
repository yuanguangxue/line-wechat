package com.hp.web;

import com.hp.exception.PushParameterException;
import com.hp.model.*;
import com.hp.service.LineMessageService;
import com.hp.service.PushRequestService;
import com.hp.service.UserDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 推送服务web 接口
 * Created by yaoyasong on 2016/4/28.
 */
@RestController
@RequestMapping("/msgpush")
public class PushController {

    @Autowired
    private UserDeviceService deviceService;

    @Autowired
    private PushRequestService pushRequestService;

    @Autowired
    private PushValidator validator;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LineMessageService lineMessageService;

    /**
     * 发送消息
     * @param pushRequest
     * @return
     */
    @RequestMapping(value="/send", method= RequestMethod.POST)
    PushResponse push(@Valid @RequestBody PushRequest pushRequest, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            throw new PushParameterException(getErrorString(bindResult));
        }
        pushRequestService.push(pushRequest);
        return new PushResponse(pushRequest.getId());
    }

    /**
     * 注册设备信息
     * @param userDevice
     * @return
     */
    @RequestMapping(value="/register", method= RequestMethod.POST)
    Map<String,String> register(@Valid @RequestBody UserDevice userDevice, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            throw new PushParameterException(getErrorString(bindResult));
        }
        deviceService.register(userDevice);
        Map<String,String> result = new HashMap<String,String>();
        result.put("id",userDevice.getId());
        return result;
    }

    private String getErrorString(BindingResult bindResult) {
        StringBuilder sb = new StringBuilder();
        for (ObjectError err: bindResult.getAllErrors()) {
            sb.append(messageSource.getMessage(
                    new DefaultMessageSourceResolvable(err.getCodes(),err.getArguments(),err.getDefaultMessage()), Locale.SIMPLIFIED_CHINESE));
            sb.append(";");
        }
        return sb.toString();
    }


    /**
     * 取消注册设备
     * @param deviceId
     */
    @RequestMapping(value="/unregister/{deviceId}", method= RequestMethod.POST)
    void unregister(@PathVariable String deviceId) {
        deviceService.unregister(deviceId);
    }

    @InitBinder
    public void initBinder(DataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = "/msgList")
    List<PushMsg> msgList(){
        return pushRequestService.listMsg();
    }

    @RequestMapping(value = "/pushRequestList")
    List<PushRequest> pushRequestList(){
        return pushRequestService.pushRequestList();
    }

    @RequestMapping(value = "/userDeviceList")
    List<UserDevice> userDeviceList(){
        return pushRequestService.userDeviceList();
    }

    @RequestMapping(value = "/applicationList")
    List<Application> applicationList(){
        return pushRequestService.applicationList();
    }

    @RequestMapping(value = "/lineMessageList")
    List<LineMessage> lineMessageList(){
        return lineMessageService.findAll();
    }
}
