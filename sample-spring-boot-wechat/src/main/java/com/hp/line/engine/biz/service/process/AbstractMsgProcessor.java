package com.hp.line.engine.biz.service.process;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMsgProcessor implements LineProcessor,ApplicationContextAware {

    protected ApplicationContext context = null;

    private String msgType;

    protected Map<String, AbstractMsgProcessor> processlist = new HashMap<String, AbstractMsgProcessor>();

    private String processNames;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getProcessNames() {
        return processNames;
    }

    public void setProcessNames(String processNames) {
        this.processNames = processNames;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
        if(getProcessNames() != null){
            for (String beanName : getProcessNames().split(",")) {
                LineProcessor bean = (LineProcessor) ctx.getBean(beanName.trim());

               /* if(bean instanceof AbstractQRCodeProcessor){
                    AbstractQRCodeProcessor scanProcessor = (AbstractQRCodeProcessor) bean;
                    this.processlist.put(scanProcessor.getQrcodeType(), scanProcessor);
                } else if(bean instanceof AbstractClickEventProcessor){
                    AbstractClickEventProcessor clickEventProcessor = (AbstractClickEventProcessor) bean;
                    this.processlist.put(clickEventProcessor.getEventKey(), clickEventProcessor);
                } else if(bean instanceof AbstractEventProcessor){
                    AbstractEventProcessor eventProcessor = (AbstractEventProcessor) bean;
                    this.processlist.put(eventProcessor.getEvent(), eventProcessor);
                } else if(bean instanceof AbstractMsgProcessor){
                    AbstractMsgProcessor msgProcessor = (AbstractMsgProcessor) bean;
                    this.processlist.put(msgProcessor.getMsgType(), msgProcessor);
                }*/
            }
        }
    }
}
