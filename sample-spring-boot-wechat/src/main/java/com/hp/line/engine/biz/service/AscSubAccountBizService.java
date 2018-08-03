package com.hp.line.engine.biz.service;

import com.hp.line.engine.biz.service.convertor.MessageConvertor;
import com.hp.line.engine.biz.service.process.LineMsg;
import com.hp.line.engine.biz.service.process.LineProcessor;
import com.linecorp.bot.model.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Slf4j
@Component
public class AscSubAccountBizService {

    @Resource(name = "repeatMsgProcessor")
    private LineProcessor msgProcessor;

    /**
     * 处理Line发过来的消息
     * @param message
     * @return
     */
    public String processMessageOrEvent(Message message) {
        LineMsg lineMsg = MessageConvertor.convert(message);
        return this.msgProcessor.process(lineMsg);
    }
}
