package com.hp.web;

import com.hp.config.PushServerConfig;
import com.hp.service.LineUserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping
public class ThymeleafController {

    @Autowired
    private PushServerConfig pushServerConfig;

    @Autowired
    private LineUserProfileService profileService;


    @RequestMapping("/push")
    public ModelAndView push(ModelAndView model){
        log.info("pushServerConfig : {}", pushServerConfig);
        model.addObject("pushServerConfig",pushServerConfig);
        model.setViewName("push");
        return model;
    }

    @RequestMapping("/weChat")
    public ModelAndView weChat(ModelAndView model){
        log.info("pushServerConfig : {}", pushServerConfig);
        Pageable pageable = new PageRequest(0,5);
        model.addObject("pushServerConfig",pushServerConfig);
        model.addObject("userList",profileService.findExistsUserId(pageable));
        model.setViewName("weChat");
        return model;
    }
}
