package com.dcits.zabbixagentapiservice.ManagedClouds;

import com.alibaba.fastjson.JSON;
import com.dcits.zabbixagentapiservice.Action;
import com.dcits.zabbixagentapiservice.ActionFactory;
import com.dcits.zabbixagentapiservice.ManagedCloud;
import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;
import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.common.LoginResp;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.resources.common.AuthenticateResource;
import com.huawei.esdk.fusioncompute.local.resources.site.SiteResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FusionCompute implements ManagedCloud {
    /**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";
    /**
     * 服务器端口号
     */
    public static String serverPort = "7443";
    private static Logger logger = LoggerFactory.getLogger(FusionCompute.class);
    private Map<String, ClientProviderBean> clientProviderBeanMap = new HashMap();
    @Override
    public String doAction(ZabbixAgentQueryInfo zabbixAgentQueryInfo) {
        // 设定服务器配置
        ClientProviderBean clientProvider = new ClientProviderBean();
        // 设定服务器配置_设定服务器IP
        clientProvider.setServerIp(zabbixAgentQueryInfo.getServerIP());
        // 设定服务器配置_设定服务器端口号
        clientProvider.setServerPort(serverPort);
        clientProvider.setUserName(zabbixAgentQueryInfo.getUsername());
        clientProvider.setVersion(Float.parseFloat(zabbixAgentQueryInfo.getVersion()));
        // 初始化用户资源实例
        AuthenticateResource auth = ServiceFactory.getService(AuthenticateResource.class, clientProvider);
        // 以用户名，用户密码作为传入参数，调用AuthenticateResource提供的login方法，完成用户的登录
        FCSDKResponse<LoginResp> resp = new FCSDKResponse<LoginResp>();
        try {
            resp = auth.login(zabbixAgentQueryInfo.getUsername(), new String(Base64.getDecoder().decode(zabbixAgentQueryInfo.getPassword()), "utf-8"));
            if (!resp.getErrorCode().equals(ERROR_CODE)) {
                logger.error("登录失败，服务器连接认证失败！登陆信息：" + JSON.toJSONString(clientProvider));
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "登录失败，服务器连接认证失败！登陆信息：" + JSON.toJSONString(zabbixAgentQueryInfo) + "password: " + new String(Base64.getDecoder().decode(zabbixAgentQueryInfo.getPassword())) + " 错误信息：" + JSON.toJSONString(resp);
        }
        // 获取SiteResource接口的实现
        SiteResource site = ServiceFactory.getService(SiteResource.class, clientProvider);
        FCSDKResponse<List<SiteBasicInfo>> resps = site.querySites();
        if (!resps.getErrorCode().equals(ERROR_CODE)) {
            logger.error("获取站点信息失败！登陆信息：" + JSON.toJSONString(clientProvider));
            return null;
        }
        List<SiteBasicInfo> siteBasicInfoList = resps.getResult();
        Action targetAction = ActionFactory.getAction(zabbixAgentQueryInfo.getAction()).orElseThrow(() -> new IllegalArgumentException("Invalid Action"));
        return targetAction.doAction(clientProvider, siteBasicInfoList, zabbixAgentQueryInfo.getInstanceName(), zabbixAgentQueryInfo.getMetric());
    }
}
