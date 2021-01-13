package com.dcits.zabbixagentapiservice.Util;
/**
 * 通用工具类-实现公共参数(请求头部分)的封装
 *
 * @author fxc
 * @date 2018-01-03
 */

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.dcits.zabbixagentapiservice.Model.CTCloud.CTCloudResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

public class CTCloudApiUtils {
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    //平台类型（融合接口统一用3）
    private static final String PLATFORM = "3";
    //url加密前缀
    private static final String PREFIX_PATH = "/apiproxy/";
    //url通用前缀
    private static final String PREFIX_URL = "http://api.ctyun.cn/apiproxy/";
    private static final Logger logger = LoggerFactory.getLogger(CTCloudApiUtils.class);
    /**
     * 1.accessKey *必填 HEADER 天翼云分配给用户的公钥
     * 2.contentMD5 *必填 HEADER
     * 业务参数值的MD5摘要：para1\npara2\n...paraN-1\nparaN contentMD5的加密方式：
     * contentMD5内容为业务参数的MD5信息摘要，构成MD5原始信息格式为（参数的拼接顺序以API签名为准，错误的顺序将导致验证失败）：
     * para1\npara2\n...paraN-1\nparaN，即将每个业务参数通过字符“\n”进行连接。若所调用的接口没有参数直接传空字符串“”
     * 进行转换为md5
     * 3.requestDate *必填 HEADER “EEE, d MMM yyyy HH:mm:ss z”格式的请求日期（只接收英文格式）
     * 4.hmac *必填 HEADER 使用HMAC算法生成的信息摘要。 HMAC原始信息中需要的字段：
     * 使用HMAC加密码时需要密钥和待加密的消息两部分内容。所以，系统中使用secretKey（用户密钥）作为加密密钥，待加密的消息由下面三部分构成：
     * contentMD5，requestDate，servicePath（REST服务名称，例如“/apiproxy/v3/order/
     * cancelOrder”），
     * 三部分信息通过“\n”进行连接（要注意连接的顺序）：contentMD5\nrequestDate\nservicePath。
     * 5.platform *必填 HEADER 平台类型，整数类型，现在默认传3，该参数不需要加密，后续该字段的具体值会补充。
     */

    /**
     * 生产请求-需要的header
     *
     * @param contentMD5Source 需要MD5校验的业务参数值
     * @param servicePath      REST服务名称，例如“/apiproxy/v3/order/
     * @param accessKey        公钥
     * @param privateKey       MD5校验需要的私钥
     * @return Map
     */
    public static Map<String, String> getExfoVmHeader(String contentMD5Source, String servicePath, String accessKey, String privateKey) {
        Map<String, String> httpHeader = new HashMap<String, String>();
//		httpHeader.put("Content-Type", "application/json;charset=UTF-8");
        httpHeader.put("accessKey", accessKey);
        String contentMD5 = toMD5Base64(contentMD5Source);
//		注意：contentMD5Source 中多个参数以 \n 分割，参数拼接时需要使用 "\\n" !!!
        logger.info("contentMD5Source: " + contentMD5Source);
        httpHeader.put("contentMD5", contentMD5);
        Date date = new Date();
//		测试时，可以默认成一个固定时间
//		String defaultDateStr="2018-01-01 01:00:00";
//		try {
//			date=new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").parse(defaultDateStr);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
        String requestDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH).format(date);
        logger.info("requestDate=" + requestDate);
        logger.info("servicePath=" + servicePath);
        httpHeader.put("requestDate", requestDate);
//		注意：calculateHMAC中待加密字符串 多个参数以 \n 分割，参数拼接时需要使用 "\n" !!!
        String hmac = calculateHMAC(privateKey, contentMD5 + "\n" + requestDate + "\n" + servicePath);
        httpHeader.put("hmac", hmac);
        httpHeader.put("platform", "3");
        return httpHeader;
    }

    /**
     * 将输入字符串转换为MD5
     *
     * @param contentMD5Source 目标字符串
     * @return contentMD5        MD5加密后的字符串
     */
    public static String toMD5Base64(String contentMD5Source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(contentMD5Source.getBytes());
            byte[] digest = md.digest();
            String result = new String(Base64.encodeBase64(digest));
            return result;
        } catch (Exception e) {
            logger.info("toMD5Base64-MD5转换报错-" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 根据HmacSHA1算法生成HMAC信息摘要
     *
     * @param secret 密钥
     * @param data   消息输入
     * @return 信息摘要
     */
    public static String calculateHMAC(String secret, String data) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(),
                    HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String result = new String(Base64.encodeBase64(rawHmac));
            return result;
        } catch (Exception e) {
            logger.info("calculateHMAC-生成HMAC信息摘要报错-" + e.getMessage());
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }


    //	测试类
    public static void main(String[] args) {
        /*   天翼云账号 密钥  公钥   */
		/*xiongantest@163.com
		accesskey公钥：36065bacf0b94c57a85dc72c16cb029f
		secretKey密钥：0a2eba050000491ba84b22c5d2a63d25*/
        String accessKey = "36065bacf0b94c57a85dc72c16cb029f";
        String privateKey = "0a2eba050000491ba84b22c5d2a63d25";
        String contentMD5Source = "";
        String servicePath = "/apiproxy/v3/order/getZoneConfig";
        Map headerMap = CTCloudApiUtils.getExfoVmHeader(contentMD5Source, servicePath, accessKey, privateKey);
        JSONObject jsobRst = JSONObject.fromObject(headerMap);
        logger.info("加密结果:\n" + jsobRst.toString().replaceAll("\",", "\n"));


    }

    /**
     * 功能描述 通用请求头参数
     *
     * @param
     * @param accesskey
     * @param secretKey
     * @param url              访问路径
     * @param contentMD5Source 参数拼接的字符串
     * @return * @return java.util.Map<java.lang.String,java.lang.String>
     * @author taolfa
     */
    public static Map<String, String> setHeaderParam(String accesskey, String secretKey, String url, String contentMD5Source) {
        //定义请求头参数集合
        HashMap<String, String> headerParam = new HashMap<>();
        //调用天翼云加密工具
        Map headerMap = CTCloudApiUtils.getExfoVmHeader(contentMD5Source, url, accesskey, secretKey);
        //设置参数
        headerParam.put("contentMD5", String.valueOf(headerMap.get("contentMD5")));
        headerParam.put("accessKey", String.valueOf(headerMap.get("accessKey")));
        headerParam.put("hmac", String.valueOf(headerMap.get("hmac")));
        headerParam.put("requestDate", String.valueOf(headerMap.get("requestDate")));
        headerParam.put("platform", String.valueOf(headerMap.get("platform")));
        return headerParam;
    }

    /**
     * 生成表头
     *
     * @param params      业务参数
     * @param servicePath api
     * @return 请求头map
     */
    public static Map<String, String> getVmHeader(String accessKey, String secretKey, String params, String servicePath) {
        //Gson gson = new Gson();
        Map<String, String> headerMap = new HashMap<>();
        //添加accessKey
        headerMap.put("accessKey", accessKey);
        String contentMD5 = toMD5Base64(params);
        //添加contentMD5
        headerMap.put("contentMD5", contentMD5);
        String requestDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH).format(new Date());
        //添加requestDate
        headerMap.put("requestDate", requestDate);
        servicePath = PREFIX_PATH + servicePath;
        String hmac = calculateHMAC(secretKey, contentMD5 + "\n" + requestDate + "\n" + servicePath);
        headerMap.put("hmac", hmac);
        headerMap.put("platform", PLATFORM);

        logger.info("加密结果:\n" +
                "accessKey: " + headerMap.get("accessKey") + "\n" +
                "contentMD5: " + headerMap.get("contentMD5") + "\n" +
                "requestDate: " + headerMap.get("requestDate") + "\n" +
                "hmac: " + headerMap.get("hmac") + "\n" +
                "platform: " + PLATFORM);
        return headerMap;
    }


    /**
     * 获取custom参数json串
     *
     * @param name
     * @param email
     * @param phone
     * @param type
     * @param crmBizid
     * @param accountId
     * @return
     */
    private static Map<String, Object> getCustomInfoParamsJson(String name, String email, String phone, int type, String crmBizid, String accountId) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("phone", phone);
        if (type == 0) {
            map.put("type", 0);
        } else {
            map.put("type", type);
            Map<String, Object> subMap = new HashMap<>();
            subMap.put("crmBizid", crmBizid);
            subMap.put("accountId", accountId);
            map.put("identity", subMap);
        }

        return map;
    }

    /**
     * 获取Get请求 body
     *
     * @param params    业务参数Map 没有传null
     * @param uri       接口
     * @param accessKey 公钥
     * @param secretKey 私钥
     * @return body
     */
    public static String getBodyByGet(Map<String, String> params, String uri, String accessKey, String secretKey) {
        StringBuilder apiParams = new StringBuilder();
        //1.拼接业务参数
        if (params != null) {
            for (String param : params.values()) {
                apiParams.append(param).append("\n");
            }
            apiParams.deleteCharAt(apiParams.length() - 1);
        }
        logger.info("参数拼接：" + apiParams.toString());
        //2.获取公共请求头
        Map<String, String> publicParamHeader = getVmHeader(accessKey, secretKey, apiParams.toString(), uri);
        String result = null;
        result = HttpRequest.get(PREFIX_URL + uri).headerMap(publicParamHeader, false).headerMap(params, false).timeout(20000).execute().body();
        return result;
    }

    /**
     * 获取Post请求 body
     *
     * @param params    业务参数Map
     * @param uri       接口
     * @param accessKey 公钥
     * @param secretKey 密钥
     * @return body
     */
    private static String getBodyByPost(Map<String, Object> params, String uri, String accessKey, String secretKey) {
        StringBuilder apiParams = new StringBuilder();
        //1.拼接业务参数
        if (params != null) {
            for (Object param : params.values()) {
                apiParams.append(param).append("\n");
            }
            apiParams.deleteCharAt(apiParams.length() - 1);
        }
        //2.获取公共请求头
        Map<String, String> publicParamHeader = getVmHeader(accessKey, secretKey, apiParams.toString(), uri);

        //3.设置传参格式
        publicParamHeader.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        String result = null;
        result = HttpRequest.post(PREFIX_URL + uri).headerMap(publicParamHeader, false).form(params).timeout(20000).execute().body();
        return result;
    }

    /**
     * 解析body
     *
     * @param body body
     * @return body
     */
    public static String systemBody(String body) {
        CTCloudResponse ctYun = JSON.parseObject(body, CTCloudResponse.class);
        if ("800".equals(ctYun.getStatusCode())) {
            //成功
            //List<Map<String,Object>> regionList = (List<Map<String, Object>>) ctYun.getReturnObj();
            //for (Map<String, Object> map : regionList) {
            //    logger.info(map.toString());
            //}
            logger.info(ctYun.toString());
            return JSON.toJSONString(ctYun.getReturnObj());
        } else if ("900".equals(ctYun.getStatusCode())) {
            //返回失败信息message
            logger.info("错误信息：" + ctYun.getMessage());
            return null;
        } else {
            //客户端操作有误
            logger.info("访问失败" + body);
            return null;
        }
    }

    public static Map<String, Object> getCTYunHttpBody(List<NameValuePair> body, String url, Map<String, String> headerMap, String flag) throws IOException {
        logger.info("url:" + url);
        logger.info("flag:" + flag);
        logger.info("header:" + headerMap.toString());
        //判断url是否为空  如果为空 就直接 返回null
        if (url == null || "".equals(url)) {
            return null;
        }
        //不为空  处理url 发送请求
        logger.info("URL: " + url);
        logger.info("BodyJSON: " + JSON.toJSONString(body));
        url = url.trim();
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        //判断请求类型
        if (StringUtils.isNotEmpty(flag)) {
            //请求类型为get
            if (ParamUtils.HTTPS_GET.equalsIgnoreCase(flag)) {
                HttpGet httpGet = new HttpGet(url);
                if ((headerMap != null) && (headerMap.size() > 0)) {
                    Set<String> set = headerMap.keySet();
                    for (String key : set) {
                        httpGet.setHeader(key, headerMap.get(key));
                    }
                }
                response = client.execute(httpGet);
            }
            else if (ParamUtils.HTTPS_POST.equalsIgnoreCase(flag)){
                HttpPost httpPost = new HttpPost(url);
                if ((headerMap != null) && (headerMap.size() > 0)) {
                    Set<String> set = headerMap.keySet();
                    for (String key : set) {
                        httpPost.setHeader(key, headerMap.get(key));
                    }
                }
                if (!body.isEmpty()){
                    UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(body, "UTF-8");
                    httpPost.setEntity(encodedFormEntity);
                }
                response = client.execute(httpPost);
            }
            //判断结果不等于null  说明访问成功
            if (response == null) {
                return null;
            }
            Map<String, Object> responseMap = new HashMap();
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(responseEntity.getContent(), ParamUtils.CODE_UTF_8))) {
                    String s = null;
                    StringBuilder sb = new StringBuilder();
                    while ((s = br.readLine()) != null) {
                        sb.append(s);
                    }
                    br.close();
                    String responsebody = sb.toString();
                    responseMap.put(ResponseParameter.RESPONSE_BODY, responsebody);
                }
            }

            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("状态码:" + statusCode);
            responseMap.put(ResponseParameter.RESPONSE_CODE, Integer.valueOf(statusCode));
            //response head with Location for Vm createsnapshot function
            Header[] headerstemp = response.getHeaders("Location");
            if ((headerstemp != null) && (headerstemp.length > 0)) {
                String location = headerstemp[0].getValue();
                responseMap.put(ParamUtils.LOCATION, location);
            }
            //返回数据
            return responseMap;
        }
        return null;
    }

    public static CloseableHttpClient createSSLGetClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1", "TLSv1.1", "TLSv1.2"}, null, new NoopHostnameVerifier());
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
}

