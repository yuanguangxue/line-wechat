package com.hp.framework.common.auth;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class AWSSigner {
    public enum HTTP_METHOD {
        GET, POST
    }

    private static final String ALGORITHM_HMAC_SHA256 = "HmacSHA256";
    private static final String ALGORITHM_SHA256 = "SHA-256";
    private static final String AUTHORIZATION_ALGORITHM = "AWS4-HMAC-SHA256";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'hh24mmss'Z'");

    /**
     * 从request中获取认证字符串
     * @param request
     * @return
     */
    public static String getAuthorization(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    /**
     * 从request中获取X-Amz-Date
     * @param request
     * @return
     */
    public static String getXAmzDate(HttpServletRequest request) {
        return request.getHeader("X-Amz-Date");
    }

    /**
     * 从认证字符串中提取加密算法
     * @param authorization
     * @return
     */
    public static String getAlgorithm(String authorization) {
        int i = authorization.indexOf(" ");
        return authorization.substring(0, i);
    }

    /**
     * 从认证字符串中提取签名
     * @param authorization
     * @return
     */
    public static String getSignature(String authorization) {
        return getAuthorizationValue(authorization, "Signature");
    }

    /**
     * 从认证字符串中提取accessKey
     * @param authorization
     * @return
     */
    public static String getAccessKey(String authorization) {
        String credential = getAuthorizationValue(authorization, "Credential");
        return credential.split("/")[0];
    }

    /**
     * 从认证字符串中提取已签名的header
     */
    public static String getSignedHeaders(String authorization) {
        return getAuthorizationValue(authorization, "SignedHeaders");
    }

    /**
     * 对请求签名，生成认证相关数据（放入header即可）。
     * 对于GET请求，最后2个参数contentType和contentBody，请传null和""
     * @param url
     * @param region
     * @param service
     * @param accessKey
     * @param secretKey
     * @param mehtod
     * @param signDate
     * @param contentType
     * @param contentBody
     * @return
     * @throws Exception
     */
    public static Map<String, String> sign(String url, String region, String service, String accessKey, String secretKey, //
                                           HTTP_METHOD mehtod, Date signDate, String contentType, String contentBody) throws Exception {
        byte[] contentBytes = null;
        if (HTTP_METHOD.POST.equals(mehtod) && contentBody != null) {
            contentBytes = contentBody.getBytes();
        } else {
            contentBytes = "".getBytes();
        }
        return sign(url, region, service, accessKey, secretKey, mehtod, signDate, contentType, contentBytes);
    }

    /**
     * 对请求签名，生成认证相关数据（放入header即可）。
     * 对于GET请求，最后2个参数contentType和contentBytes，请传null和""
     * @param url
     * @param region
     * @param service
     * @param accessKey
     * @param secretKey
     * @param mehtod
     * @param signDate
     * @param contentType
     * @param contentBytes
     * @return
     * @throws Exception
     */
    public static Map<String, String> sign(String url, String region, String service, String accessKey, String secretKey, //
                                           HTTP_METHOD mehtod, Date signDate, String contentType, byte[] contentBytes) throws Exception {
        String host = null;
        String requestURI = null;
        TreeMap<String, String> queryMap = null;
        //解析URL
        URL u = new URL(url);
        host = u.getHost();
        requestURI = u.getPath();
        String queryString = u.getQuery();
        if (queryString != null && !"".equals(queryString)) {
            queryMap = new TreeMap<String, String>();
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] paramNameAndValue = param.split("=");
                queryMap.put(paramNameAndValue[0], paramNameAndValue[1]);
            }
        }
        return sign(host, region, service, accessKey, secretKey, mehtod, signDate, requestURI, queryMap, contentType, contentBytes);
    }

    /**
     * 对请求签名，生成认证相关数据（放入header即可）。
     * 对于GET请求，最后2个参数contentType和contentBody，请传null和""
     * @param host
     * @param region
     * @param service
     * @param accessKey
     * @param secretKey
     * @param mehtod
     * @param signDate
     * @param requestURI
     * @param queryMap
     * @param contentType
     * @param contentBody
     * @return
     * @throws Exception
     */
    public static Map<String, String> sign(String host, String region, String service, String accessKey, String secretKey, //
                                           HTTP_METHOD mehtod, Date signDate, String requestURI, TreeMap<String, String> queryMap, String contentType, String contentBody) throws Exception {
        byte[] contentBytes = null;
        if (HTTP_METHOD.POST.equals(mehtod) && contentBody != null) {
            contentBytes = contentBody.getBytes();
        } else {
            contentBytes = "".getBytes();
        }
        return sign(host, region, service, accessKey, secretKey, mehtod, signDate, requestURI, queryMap, contentType, contentBytes);
    }

    /**
     * 对请求签名，生成认证相关数据（放入header即可）。
     * 对于GET请求，最后2个参数contentType和contentBytes，请传null和""
     * @param host
     * @param region
     * @param service
     * @param accessKey
     * @param secretKey
     * @param mehtod
     * @param signDate
     * @param requestURI
     * @param queryMap
     * @param contentType
     * @param contentBytes
     * @return
     * @throws Exception
     */
    public static Map<String, String> sign(String host, String region, String service, String accessKey, String secretKey, //
                                           HTTP_METHOD mehtod, Date signDate, String requestURI, TreeMap<String, String> queryMap, String contentType, byte[] contentBytes) throws Exception {
        byte[] signBytes = null;
        if (HTTP_METHOD.POST.equals(mehtod) && contentBytes != null) {
            signBytes = contentBytes;
        } else {
            signBytes = "".getBytes();
        }

        String datetime = sdf.format(signDate);
        String date = datetime.substring(0, 8);
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
        if (HTTP_METHOD.POST.equals(mehtod)) {
            headerMap.put("Content-Type", contentType);
        }
        headerMap.put("Host", host);
        headerMap.put("X-Amz-Date", datetime);

        //生成签名
        String signature = sign(region, service, accessKey, secretKey, AUTHORIZATION_ALGORITHM, //
                mehtod.name(), datetime, requestURI, queryMap, headerMap, signBytes);

        StringBuilder authorization = new StringBuilder();
        authorization.append(AUTHORIZATION_ALGORITHM);
        authorization.append(" Credential=");
        authorization.append(accessKey);
        authorization.append("/");
        authorization.append(date);
        authorization.append("/");
        authorization.append(region);
        authorization.append("/");
        authorization.append(service);
        if (HTTP_METHOD.POST.equals(mehtod)) {
            authorization.append("/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=");
        } else {
            authorization.append("/aws4_request, SignedHeaders=host;x-amz-date, Signature=");
        }
        authorization.append(signature);
        headerMap.put("Authorization", authorization.toString());
        return headerMap;
    }

    /**
     * 验证request中的签名是否正确
     * @param host
     * @param region
     * @param service
     * @param accessKey
     * @param secretKey
     * @param request
     * @return
     * @throws Exception
     */
    public static boolean verifyRequest(String host, String region, String service, String accessKey, String secretKey, //
                                        HttpServletRequest request) throws Exception {
        String authorization = getAuthorization(request);
        String signature = getSignature(authorization);
        String calcSignature = signRequest(host, region, service, accessKey, secretKey, request);
        return signature.equals(calcSignature);
    }

    private static String signRequest(String host, String region, String service, String accessKey, String secretKey, //
                                      HttpServletRequest request) throws Exception {
        String datetime = getXAmzDate(request);
        String authorization = getAuthorization(request);
        String algorithm = getAlgorithm(authorization);
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        //解析请求参数
        TreeMap<String, String> queryMap = new TreeMap<String, String>();
        String queryString = request.getQueryString();
        if (queryString != null && !"".equals(queryString)) {
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] paramNameAndValue = param.split("=");
                queryMap.put(paramNameAndValue[0], paramNameAndValue[1]);
            }
        }
        //解析签名header
        LinkedHashMap<String, String> headerMap = new LinkedHashMap<String, String>();
        String signedHeaders = getSignedHeaders(authorization);
        String[] signedHeaderArray = signedHeaders.split(";");
        for (String signedHeaderName : signedHeaderArray) {
            if ("Host".equalsIgnoreCase(signedHeaderName)) {
                headerMap.put("Host", host);
            } else {
                Enumeration<?> enumeration = request.getHeaderNames();
                while (enumeration.hasMoreElements()) {
                    String headerName = String.valueOf(enumeration.nextElement());
                    if (signedHeaderName.equals(headerName.toLowerCase())) {
                        headerMap.put(headerName, request.getHeader(headerName));
                        break;
                    }
                }
            }
        }
        //获取content
        byte[] contentBytes = null;
        if (request.getMethod().equals(HTTP_METHOD.GET.name())) {
            contentBytes = "".getBytes();
        } else {
            contentBytes = IOUtils.toByteArray(request.getInputStream());
        }
        //签名
        return sign(region, service, accessKey, secretKey, algorithm, //
                method, datetime, requestURI, queryMap, headerMap, contentBytes);
    }

    private static String sign(String region, String service, String accessKey, String secretKey, String algorithm, //
                               String method, String datetime, String requestURI, TreeMap<String, String> queryMap, LinkedHashMap<String, String> headerMap, byte[] contentBytes) throws Exception {
        //日期
        String date = datetime.substring(0, 8);
        //规范URI
        String canonicalURI = requestURI;
        //规范请求字符串
        StringBuilder canonicalQuerystring = new StringBuilder();
        if (queryMap != null && !queryMap.isEmpty()) {
            for (String name : queryMap.keySet()) {
                canonicalQuerystring.append(name);
                canonicalQuerystring.append("=");
                canonicalQuerystring.append(queryMap.get(name));
                canonicalQuerystring.append("&");
            }
            canonicalQuerystring.deleteCharAt(canonicalQuerystring.length() - 1);
        }
        //签名的header
        StringBuilder canonicalHeaderNames = new StringBuilder();
        //规范header
        StringBuilder canonicalHeaderValues = new StringBuilder();
        if (headerMap != null && !headerMap.isEmpty()) {
            for (String signedHeaderName : headerMap.keySet()) {
                canonicalHeaderNames.append(signedHeaderName.toLowerCase() + ";");
                canonicalHeaderValues.append(signedHeaderName.toLowerCase() + ":");
                canonicalHeaderValues.append(headerMap.get(signedHeaderName));
                canonicalHeaderValues.append("\n");
            }
            canonicalHeaderNames.deleteCharAt(canonicalHeaderNames.length() - 1);
        }
        //请求体hash
        String payloadHash = HexSHA256(contentBytes);

        //规范请求
        String canonicalRequest = method + "\n" + canonicalURI + "\n" + canonicalQuerystring + "\n" + canonicalHeaderValues + "\n" + canonicalHeaderNames + "\n" + payloadHash;
        //凭证范围
        String credentialScope = date + "/" + region + "/" + service + "/" + "aws4_request";
        //待签名字符串
        String stringToSign = algorithm + "\n" + datetime + "\n" + credentialScope + "\n" + HexSHA256(canonicalRequest.getBytes());
        //签名秘钥
        byte[] signingKey = makeSignatureKey(secretKey, date, region, service);

        //计算签名
        return HexMacSHA256(signingKey, stringToSign);
    }

    private static byte[] MacSHA256(byte[] key, String message) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM_HMAC_SHA256);
        Mac mac = Mac.getInstance(ALGORITHM_HMAC_SHA256);
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(message.getBytes());
        return rawHmac;
    }

    private static String HexMacSHA256(byte[] key, String message) throws Exception {
        return Hex.encodeHexString(MacSHA256(key, message));
    }

    private static byte[] SHA256(byte[] key) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM_SHA256);
        messageDigest.update(key);
        return messageDigest.digest();
    }

    private static String HexSHA256(byte[] key) throws Exception {
        return Hex.encodeHexString(SHA256(key));
    }

    private static String getAuthorizationValue(String authorization, String name) {
        int i = authorization.indexOf(name);
        String temp = authorization.substring(i + name.length() + 1);
        i = temp.indexOf(",");
        if (i == -1) {
            return temp;
        } else {
            return temp.substring(0, i);
        }
    }

    private static byte[] makeSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kDate = MacSHA256(("AWS4" + key).getBytes(), dateStamp);
        byte[] kRegion = MacSHA256(kDate, regionName);
        byte[] kService = MacSHA256(kRegion, serviceName);
        byte[] kSigning = MacSHA256(kService, "aws4_request");
        return kSigning;
    }
}
