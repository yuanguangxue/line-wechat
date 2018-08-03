package com.hp.framework.httpsender;

import com.linecorp.bot.spring.boot.LineBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
public class HttpRequestSender {

    @Autowired
    private LineBotProperties lineBotProperties;

    public String httpGetRequest(String url, String region, String service, String accessKey, String secretKey){
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            return sender.httpGetRequest(url, region, service, accessKey, secretKey);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public String httpsGetRequest(String url, String region, String service, String accessKey, String secretKey){
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            String rs = sender.httpsGetRequest(url, region, service, accessKey, secretKey);
            return rs;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public String httpsPostRequest(String url, String contentType, String param, String region, String service, String accessKey, String secretKey){
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            String rs = sender.httpsPostRequest(url, contentType, param, region, service, accessKey, secretKey);
            return rs;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if(sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public String httpPostRequest(String url, String contentType, String param, String region, String service, String accessKey, String secretKey){
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            String rs = sender.httpPostRequest(url, contentType, param, region, service, accessKey, secretKey);
            return rs;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public String httpsGetRequest(String url){
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            return sender.httpsGetRequest(url);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public String httpsPostRequest(String url, String contentType, String param){
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            String rs = sender.httpsPostRequest(url, contentType, param);
            return rs;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if(sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public String httpPostRequest(String url, String contentType, String param){
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            String rs = sender.httpPostRequest(url, contentType, param);
            return rs;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    public String httpGetRequest(String url){
        HttpURLConnection con = null;
        InputStream in = null;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();

            int respCode = con.getResponseCode();
            if(respCode == 200){
                in = con.getInputStream();
                byte[] buff = new byte[in.available()];
                in.read(buff);
                String str = new String(buff);
                return str;
            }
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("inputStream already closed!");
                }
            }
            if(con != null)
                con.disconnect();
        }
        return null;
    }

    public String formUpload(String urlStr, String mediaId) {
        HttpSender sender = null;
        try {
            sender = (HttpSender) this.pool.borrowObject();
            return sender.formUpload(urlStr, mediaId, lineBotProperties);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            try {
                if (sender != null)
                    this.pool.returnObject(sender);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return null;
    }

    private GenericObjectPool pool = null;

    @PostConstruct
    public void init(){
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.lifo = false;
        config.maxActive = 200;
        config.maxIdle = 5;
        config.minIdle = 1;
        config.maxWait = 30 * 1000;
        pool = new GenericObjectPool(new HttpSenderFactory(), config);
    }

    private static class HttpSenderFactory extends BasePoolableObjectFactory {
        public Object makeObject() {
            return new HttpSender();
        }

        public void destroyObject(Object obj) {
            HttpSender sender = (HttpSender) obj;
            sender.destroy();
        }
    }
}
