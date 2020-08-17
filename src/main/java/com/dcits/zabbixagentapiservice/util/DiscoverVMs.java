package com.dcits.zabbixagentapiservice.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.PageList;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.vm.QueryVmsReq;
import com.huawei.esdk.fusioncompute.local.model.vm.VmInfo;
import com.huawei.esdk.fusioncompute.local.resources.vm.VmResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoverVMs implements Action{
    /**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";
    private static Logger logger = LoggerFactory.getLogger(DiscoverVMs.class);

    @Override
    public String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
        JsonObject result = new JsonObject();
        QueryVmsReq req = new QueryVmsReq();
        req.setLimit(100);
        req.setOffset(0);
        // 获取VmResource接口的实现
        VmResource instance = ServiceFactory.getService(VmResource.class, clientProvider);
        for (SiteBasicInfo siteBasicInfo : siteBasicInfoList) {
            FCSDKResponse<PageList<VmInfo>> response = instance.queryVMs(null, siteBasicInfo.getUri());
            if (!response.getErrorCode().equals(ERROR_CODE)) {
                logger.error("QUERY VMs FAILED");
            } else {
                JsonArray jsonArray = new JsonArray();
                for (VmInfo vmInfo : response.getResult().getList()) {
                    //忽略镜像
                    if (!vmInfo.getIsTemplate()) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("{#VMNAME}", vmInfo.getName());
                        //因uri和urn都含有特殊字符zabbix不支持，进行加密
//						jsonObject.addProperty("{#VMURN}", Base64.getEncoder().encodeToString(vmInfo.getUrn().getBytes()));
//						jsonObject.addProperty("{#VMURN}", vmInfo.getUrn().replace(":", "?"));
                        jsonArray.add(jsonObject);
                    }
                }
                result.add("data", jsonArray);
                logger.info(JSON.toJSONString(result));
            }
        }
        return JSON.toJSONString(result);
    }
}
