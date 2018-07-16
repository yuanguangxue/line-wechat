package com.hp.service;


import com.hp.config.PushServerConfig;
import com.notnoop.apns.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ios apns 推送服务
 * Created by yaoyasong on 2016/5/5.
 */
@Service
public class ApnsHandler {
    private static final Log log = LogFactory.getLog(ApnsHandler.class);

    @Autowired
    private PushServerConfig pushServerConfig;

    @SuppressWarnings("uncheck")
    public void pushMsg(String certFile, String certPassword, Collection<String> tokens, String alertBody, String sound, int badge) {
        String payload = APNS.newPayload().alertBody(alertBody).badge(badge).sound(sound)
                .build();

        List<String> deviceTokens = new ArrayList<>();

        for (String token : tokens) {
            String tmpToken = token.replace(" ", "").replace("<", "").replace(">", "");
            deviceTokens.add(tmpToken);
        }

        ApnsService service = buildService(certFile,certPassword);
        service.start();
        Collection<? extends ApnsNotification> notifications = service.push(tokens, payload);//暂时未处理返回消息
        service.stop();
    }

    private ApnsService buildService(String certFile, String certPassword) {

        final ApnsDelegate delegate = new ApnsDelegate() {
            public void messageSent(final ApnsNotification message, final boolean resent) {
                log.info("Sent message " + message + " Resent: " + resent);
            }

            public void messageSendFailed(final ApnsNotification message, final Throwable e) {
                log.warn("Failed message " + message);
                log.warn("Error msg: " + e.getMessage());
            }

            public void connectionClosed(final DeliveryError e, final int messageIdentifier) {
                log.warn("Closed connection: " + messageIdentifier + "\n   deliveryError " + e.toString());
            }

            public void cacheLengthExceeded(final int newCacheLength) {
                log.warn("cacheLengthExceeded " + newCacheLength);
            }

            public void notificationsResent(final int resendCount) {
                log.warn("notificationResent " + resendCount);
            }
        };

        ApnsService service = APNS.newService()
                .withCert(certFile, certPassword).withAppleDestination(pushServerConfig.isProd())
                .withDelegate(delegate)
                .build();
        return service;
    }

}
