package com.hp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务器配置参数
 * Created by yaoyasong on 2016/5/6.
 */
@Component
@ConfigurationProperties(prefix="pushServer")
public class PushServerConfig {

    private String mode;
    private String env;
    private String url;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public Boolean isProd() {
        return getEnv().equals("PROD");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "pushServerConfig {" +
                "mode='" + mode + '\'' +
                ", env='" + env + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
