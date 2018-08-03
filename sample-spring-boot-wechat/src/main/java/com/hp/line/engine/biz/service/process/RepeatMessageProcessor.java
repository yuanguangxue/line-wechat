package com.hp.line.engine.biz.service.process;

import com.hp.line.engine.biz.service.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service("repeatMsgProcessor")
public class RepeatMessageProcessor extends AbstractMsgProcessor{

    private static final Map<String, LineMsg> msgCache = new ConcurrentHashMap<String, LineMsg>();

    private int cacheTime = 60000;

    @Resource(name ="msgForwardProcessor")
    private LineProcessor msgProcessor;

    @Override
    public String process(LineMsg lineMsg) {

        lineMsg.set("timestamp", System.currentTimeMillis());
        //清理缓存中过期的记录
        cleanCacheByTimestamp();

//		if(this.isOverTime(lineMsg))
//			return "过时的消息";

        //将消息保存至缓存
        String msgId = this.getMsgId(lineMsg);
        if(!msgCache.containsKey(msgId)){
            msgCache.put(msgId, lineMsg);
            //如果消息不重复,继续处理
            return this.msgProcessor.process(lineMsg);
        }
        //如果消息重复,不予处理并返回空字符串
        return "";
    }

    //获取消息id
    private String getMsgId(LineMsg lineMsg){
        //如果存在MsgId字段,则以msgId字段作为消息id
        String msgId = (String) lineMsg.get(Constant.MSG_ID);
        //否则以fromUserName+createTime作为msgId
        if(msgId == null){
            String from = lineMsg.getFrom();
            Long createTime = lineMsg.getCreateTime();
            msgId = from + createTime;
        }
        return msgId;
    }

    private void cleanCacheByCreateTime(){
        long cur = System.currentTimeMillis() / 1000;
        for (String key : msgCache.keySet()) {
            LineMsg msg = msgCache.get(key);
            Long createTime = msg.getCreateTime();
            if(createTime == null)
                msgCache.remove(key);
            if(cur > createTime + 20)
                msgCache.remove(key);
        }
        log.info("cache size: " + msgCache.size());
    }

    private void cleanCacheByTimestamp(){
        long cur = System.currentTimeMillis();
        for (String key : msgCache.keySet()) {
            LineMsg msg = msgCache.get(key);
            long timestamp = (Long) msg.get("timestamp");
            if(cur > timestamp + cacheTime)
                msgCache.remove(key);
        }
        log.debug("cache size: {}", msgCache.size());
    }

    //判断消息是否是规定时间之前的
    private boolean isOverTime(LineMsg lineMsg){
        Long createTime = lineMsg.getCreateTime();
        long cur = System.currentTimeMillis() / 1000;
        if(createTime == null || (cur - 30 > createTime.longValue())){
            log.info("过时的消息: " + lineMsg.getFrom() + "-" + lineMsg.getCreateTime());
            return true;
        }
        return false;
    }
}
