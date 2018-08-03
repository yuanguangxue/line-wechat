package com.hp.line.engine.biz.service.process;

import com.hp.asc.custom.biz.service.DkfServiceHistoryBizService;
import com.hp.framework.httpsender.HttpRequestSender;
import com.hp.line.engine.biz.service.util.Constant;
import com.hp.line.engine.common.MessageCreator;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service("msgForwardProcessor")
public class MsgForwardProcessor extends AbstractMsgProcessor {

    @Value("com.hp.webChatMsgInputUrl")
    private String webChatMsgInputUrl;

    @Value("com.hp.webChatFileUploadUrl")
    private String webChatFileUploadUrl;

    @Autowired
    private HttpRequestSender httpRequestSender;

    @Resource(name = "statusTreeProcessor")
    private LineProcessor msgProcessor;

    @Autowired
    private DkfServiceHistoryBizService dkfServiceHistoryBizService;

    private String getWebChatMsgInputUrl() {
        return webChatMsgInputUrl;
    }

    private String getWebChatFileUploadUrl() {
        return webChatFileUploadUrl;
    }

    @Override
    public String process(LineMsg lineMsg) {
        if(dkfServiceHistoryBizService.exists(lineMsg.getFrom())){
            //转发到多客服
            String msgType = lineMsg.getMsgType();
            //将video类型转换成 shortvideo
            if("video".equals(msgType)){
                msgType = Constant.MSG_TYPE_SHORTVIDEO;
                lineMsg.set(Constant.MSG_TYPE, msgType);
            }
            if(Constant.MSG_TYPE_TEXT.equals(msgType))
                return this.forwardTextMsgToDKF(lineMsg);
            else if(Constant.MSG_TYPE_IMAGE.equals(msgType))
                return this.forwardImageMsgToDKF(lineMsg);
            else if (Constant.MSG_TYPE_VOICE.equals(msgType)
                    || Constant.MSG_TYPE_SHORTVIDEO.equals(msgType))
                return this.forwardMediaMsgToDKF(lineMsg);
        }
        return this.msgProcessor.process(lineMsg);
    }

    private String forwardTextMsgToDKF(LineMsg lineMsg){
        String webChatUrl = this.getWebChatMsgInputUrl();
        this.httpRequestSender.httpsPostRequest(webChatUrl, "text/xml;charset=UTF-8", lineMsg.getSource());
        return "";
    }

    private String forwardImageMsgToDKF(LineMsg lineMsg){
        String webChatUrl = this.getWebChatMsgInputUrl();
        this.httpRequestSender.httpsPostRequest(webChatUrl, "text/xml;charset=UTF-8", lineMsg.getSource());
        return "";
    }

    private String forwardMediaMsgToDKF(final LineMsg lineMsg){
        new Thread(() -> {
            String mediaId = lineMsg.getMediaId();
            //上传多媒体文件到多客服
            String rs;
            String urlStr = getWebChatFileUploadUrl();
            //String mediaUrl = "";//http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+accessTokenUpdater.getAccessToken()+"&media_id=" + mediaId;
            rs = httpRequestSender.formUpload(urlStr, mediaId);
            JSONObject jsonResult = null;
            try {
                jsonResult = new JSONObject(rs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String state = jsonResult.optString("state");
            log.info(state);
            if("SUCCESS".equals(state)){
                Long fileid = jsonResult.optLong("fileid");
                JSONObject msgObj = XML.toJSONObject(lineMsg.getSource()).optJSONObject(MessageCreator.ROOT_TAG);
                msgObj.put("FileId", fileid);

                JSONObject rootObj = new JSONObject();
                rootObj.put(MessageCreator.ROOT_TAG, msgObj);
                String tmpStr = XML.toString(rootObj);
                //将video转化为shortvideo
                log.info(tmpStr);
                String tmp = tmpStr.replace("video", Constant.MSG_TYPE_SHORTVIDEO);
                log.info(tmp);
                //转发到多客服
                String webChatUrl = getWebChatMsgInputUrl();
                rs = httpRequestSender.httpPostRequest(webChatUrl, "text/xml;charset=UTF-8", tmp);
            }
        }).start();
        return "";
    }
}
