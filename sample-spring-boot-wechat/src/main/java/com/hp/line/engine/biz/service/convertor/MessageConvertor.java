package com.hp.line.engine.biz.service.convertor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hp.line.engine.biz.service.process.LineMsg;
import com.hp.line.engine.biz.service.util.Constant;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class MessageConvertor {

    private static final ObjectMapper OBJECT_MAPPER = ModelObjectMapper.createNewObjectMapper();

    private static final XmlMapper XML = new XmlMapper();

    public static LineMsg convert(Message message){
        LineMsg lineMsg = new LineMsg();
        try {
            String json = OBJECT_MAPPER.writeValueAsString(message);
            String xml = XML.writeValueAsString(message);
            lineMsg.set(Constant.MSG_XML_SOURCE, xml);
            lineMsg.set(Constant.MSG_JSON_SOURCE, json);
            JsonNode jsonNode = OBJECT_MAPPER.readTree(json);
            Iterator<Map.Entry<String, JsonNode>> it = jsonNode.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String key = entry.getKey();
                JsonNode node = entry.getValue();
                if(node.isValueNode()){
                    lineMsg.set(key, node.toString());
                }else {
                    lineMsg.set(key, node);
                }
            }
            if(!StringUtils.isEmpty(lineMsg.get("type"))){
                lineMsg.set(Constant.MSG_TYPE,lineMsg.get("type"));
            }
            if(!StringUtils.isEmpty(lineMsg.get("id"))){
                lineMsg.set(Constant.MSG_ID,lineMsg.get("id"));
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return lineMsg;
    }
}
