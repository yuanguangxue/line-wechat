package com.hp.framework.httpsender;

import ch.qos.logback.core.util.ContentTypeUtil;
import com.hp.framework.common.auth.AWSSigner;
import com.hp.framework.common.auth.AWSSigner.HTTP_METHOD;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.spring.boot.LineBotProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class HttpSender {

    private CloseableHttpClient httpsClient;
    private CloseableHttpClient httpClient;

    private static final int TIMEOUT = 60;

    HttpSender() {
        this.init();
    }

    public void init() {
        SSLContextBuilder builder = new SSLContextBuilder();
        SSLConnectionSocketFactory sslSf;
        try {
            builder.loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true);
            sslSf = new SSLConnectionSocketFactory(builder.build());
            RequestConfig config = RequestConfig.custom().setConnectTimeout(TIMEOUT * 1000).setSocketTimeout(TIMEOUT * 1000).build();
            httpsClient = HttpClients.custom().setSSLSocketFactory(sslSf).setDefaultRequestConfig(config).build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
        } catch (Exception e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        }
    }

    public String httpGetRequest(String url, String region, String service, String accessKey, String secretKey) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            //添加签名
            try {
                Map<String, String> signMap = AWSSigner.sign(url, region, service, accessKey, secretKey, //
                        HTTP_METHOD.GET, new Date(), null, "");
                if (signMap != null && signMap.size() > 0) {
                    for (Map.Entry<String, String> entry : signMap.entrySet()) {
                        httpGet.addHeader(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                log.warn("签名失败", e);
            }
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String rs = EntityUtils.toString(entity, Charset.forName("UTF-8"));//解決了获取微信用户信息乱码
            return rs;
        } catch (ClientProtocolException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public String httpsGetRequest(String url, String region, String service, String accessKey, String secretKey) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            //添加签名
            try {
                Map<String, String> signMap = AWSSigner.sign(url, region, service, accessKey, secretKey, //
                        HTTP_METHOD.GET, new Date(), null, "");
                if (signMap != null && signMap.size() > 0) {
                    for (Map.Entry<String, String> entry : signMap.entrySet()) {
                        httpGet.addHeader(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                log.warn("签名失败", e);
            }
            response = httpsClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String rs = EntityUtils.toString(entity, Charset.forName("UTF-8"));//解決了获取微信用户信息乱码
            return rs;
        } catch (ClientProtocolException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public String httpsPostRequest(String url, String contentType, String param, String region, String service, String accessKey, String secretKey) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            //添加签名
            try {
                Map<String, String> signMap = AWSSigner.sign(url, region, service, accessKey, secretKey, //
                        HTTP_METHOD.POST, new Date(), contentType, param);
                if (signMap != null && signMap.size() > 0) {
                    for (Map.Entry<String, String> entry : signMap.entrySet()) {
                        httpPost.addHeader(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                log.warn("签名失败", e);
            }
            httpPost.setHeader("Content-Type", contentType);
            httpPost.setEntity(new StringEntity(param, "UTF-8"));
            response = httpsClient.execute(httpPost);
            HttpEntity entity1 = response.getEntity();
            String rs = EntityUtils.toString(entity1);
            return rs;
        } catch (ClientProtocolException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public String httpPostRequest(String url, String contentType, String param, String region, String service, String accessKey, String secretKey) {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            //添加签名
            try {
                Map<String, String> signMap = AWSSigner.sign(url, region, service, accessKey, secretKey, //
                        HTTP_METHOD.POST, new Date(), contentType, param);
                if (signMap != null && signMap.size() > 0) {
                    for (Map.Entry<String, String> entry : signMap.entrySet()) {
                        post.addHeader(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                log.warn("签名失败", e);
            }
            StringEntity entity = new StringEntity(param, "UTF-8");
            post.setEntity(entity);
            post.setHeader("Content-Type", contentType);
            response = httpClient.execute(post);
            HttpEntity rsEntity = response.getEntity();
            String rs = EntityUtils.toString(rsEntity);
            return rs;
        } catch (ClientProtocolException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {

            }
        }
        return null;
    }

    public String httpGetRequest(String url) {
        try {
            return IOUtils.toString(new URL(url),Charset.defaultCharset());
        } catch (MalformedURLException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        }
        return null;
    }

    public String httpsGetRequest(String url) {
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            response = httpsClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String rs = EntityUtils.toString(entity, Charset.forName("UTF-8"));//解決了获取微信用户信息乱码
            return rs;
        } catch (ClientProtocolException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public String httpsPostRequest(String url, String contentType, String param) {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", contentType);
            httpPost.setEntity(new StringEntity(param, "UTF-8"));
            response = httpsClient.execute(httpPost);
            HttpEntity entity1 = response.getEntity();
            String rs = EntityUtils.toString(entity1);
            return rs;
        } catch (ClientProtocolException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public String httpPostRequest(String url, String contentType, String param) {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(param, "UTF-8");
            post.setEntity(entity);
            post.setHeader("Content-Type", contentType);
            response = httpClient.execute(post);
            HttpEntity rsEntity = response.getEntity();
            String rs = EntityUtils.toString(rsEntity);
            return rs;
        } catch (ClientProtocolException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {

            }
        }
        return null;
    }

    public String formUpload(String urlStr, String mediaId,LineBotProperties lineBotProperties) throws IOException {
       /* URL url = new URL(mediaUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();*/
        final LineMessagingClient client = LineMessagingClient
                .builder(lineBotProperties.getChannelToken())
                .build();
        InputStream in = null;
        String contentType = null;
        try {
            final MessageContentResponse messageContentResponse = client.getMessageContent(mediaId).get();
            in = messageContentResponse.getStream();
            contentType = messageContentResponse.getMimeType();
        } catch (InterruptedException | ExecutionException e) {
            /*e.printStackTrace();*/
            log.error("error", e);
        }

        return upload(urlStr, in, contentType);
    }

    private String upload(String url, InputStream stream, String contentType) throws IOException {
        if(stream == null || contentType == null){
            throw new IOException("获取资源异常");
        }
        String suffix = ContentTypeUtil.getSubType(contentType);
        if ("mpeg4".equals(suffix)) {
            suffix = "mp4";
        }
        String filename = System.currentTimeMillis() + "." + suffix;
        /**
         * 第一部分
         */
        //			String url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=VoVJL4gd0T20i-KcjC7ZxmKN5-C6t_6qiyZs_p5QZMV3gtFDnG4crKBTbYeHbvMdChDgy3tk9czGMh25QqOsY_qFG_Kcy_mJ_QGN_rCzn8g&type=" + type;
        URL urlObj = new URL(url);
        // 连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);

        // 设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");

        // 设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        // 请求正文信息

        // 第一部分：
        StringBuilder sb = new StringBuilder();
        sb.append("--"); // 必须多两道线
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + filename + "\"\r\n");
        sb.append("Content-Type:" + contentType + "\r\n\r\n");

        byte[] head = sb.toString().getBytes("utf-8");

        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        // 输出表头
        out.write(head);

        // 文件正文部分
        // 把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(stream);
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();

        // 结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线

        out.write(foot);

        out.flush();
        out.close();

        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                // System.out.println(line);
                buffer.append(line);
            }
            return buffer.toString();
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
            throw new IOException("数据读取异常");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void destroy() {
        try {
            if (httpsClient != null)
                this.httpsClient.close();
            if (httpClient != null)
                this.httpClient.close();
        } catch (IOException e) {
            log.error(ExceptionUtils.getFullStackTrace(e));
        }
    }
}
