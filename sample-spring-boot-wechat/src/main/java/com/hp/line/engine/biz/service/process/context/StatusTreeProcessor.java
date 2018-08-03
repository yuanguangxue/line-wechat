package com.hp.line.engine.biz.service.process.context;

import com.hp.line.engine.biz.service.process.AbstractMsgProcessor;
import com.hp.line.engine.biz.service.process.LineMsg;
import org.springframework.stereotype.Service;

@Service("statusTreeProcessor")
public class StatusTreeProcessor extends AbstractMsgProcessor {
    @Override
    public String process(LineMsg lineMsg) {
        return "";
    }
}
