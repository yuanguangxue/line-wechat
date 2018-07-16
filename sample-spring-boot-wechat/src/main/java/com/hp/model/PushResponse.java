package com.hp.model;

import java.io.Serializable;

/**
 * 推送结果
 * Created by yaoyasong on 2016/4/28.
 */
public class PushResponse implements Serializable{
    private static final long serialVersionUID = -7034897190745766939L;
    private String requestId;

    public PushResponse(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }
}
