package com.hp.web;

import com.hp.config.PushServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
@RequestMapping
public class ThymeleafController {

    @Autowired
    PushServerConfig pushServerConfig;

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
        model.addObject("pushServerConfig",pushServerConfig);
        model.setViewName("weChat");
        return model;
    }
}
