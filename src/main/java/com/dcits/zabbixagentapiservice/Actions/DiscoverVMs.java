package com.dcits.zabbixagentapiservice.Actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dcits.zabbixagentapiservice.Action;
import com.dcits.zabbixagentapiservice.Model.CTCloud.VmInfo_CTCloud;
import com.dcits.zabbixagentapiservice.Util.CTCloudApiUtils;
import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;
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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoverVMs implements Action {
    /**
     * 错误码
     */
    public static String ERROR_CODE = "00000000";
    private static Logger logger = LoggerFactory.getLogger(DiscoverVMs.class);

    @Override
    public String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
        JsonObject result = new JsonObject();
        QueryVmsReq req = new QueryVmsReq();
        req.setDetail(0);
        req.setLimit(100);
        int current = 0;
        int offset = 0;
        boolean errorFlag = false;
        // 获取VmResource接口的实现
        VmResource instance = ServiceFactory.getService(VmResource.class, clientProvider);
        List<VmInfo> vmInfosAll = new ArrayList<>();
        for (SiteBasicInfo siteBasicInfo : siteBasicInfoList) {
            while (true) {
                req.setOffset(offset);
                FCSDKResponse<PageList<VmInfo>> response = instance.queryVMs(null, siteBasicInfo.getUri());
                if (!response.getErrorCode().equals(ERROR_CODE)) {
                    errorFlag = true;
                    logger.error("QUERY VMs FAILED. Query info: "+JSON.toJSONString(clientProvider));
                }
                if (response.getResult().getList().size() > 0) {
                    vmInfosAll.addAll(response.getResult().getList());
                    current = current + response.getResult().getList().size();
                    if (response.getResult().getTotal() <= current) {
                        break;
                    } else {
                        offset = offset + 100;
                    }
                } else {
                    break;
                }
            }
            if (errorFlag) {
                logger.error("QUERY VMs FAILED. Query info: "+JSON.toJSONString(clientProvider));
            } else {
                JsonArray jsonArray = new JsonArray();
                for (VmInfo vmInfo : vmInfosAll) {
                    //忽略镜像
                    if (!vmInfo.getIsTemplate()) {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("{#VMNAME}", vmInfo.getUuid());
                        //因uri和urn都含有特殊字符zabbix不支持，进行加密
//						jsonObject.addProperty("{#VMURN}", Base64.getEncoder().encodeToString(vmInfo.getUrn().getBytes()));
//						jsonObject.addProperty("{#VMURN}", vmInfo.getUrn().replace(":", "?"));
                        jsonArray.add(jsonObject);
                    }
                }
                result.add("data", jsonArray);
                logger.debug(result.toString());
            }
        }
        return result.toString();
    }

    @Override
    public String doAction(ZabbixAgentQueryInfo zabbixAgentQueryInfo, String instanceName, String metric) throws IOException {
        JsonObject result = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        int page_no = 1;
        int page_size = 10;
        int row_count = 0;
        boolean continue_ = true;
        List<VmInfo_CTCloud> vmInfos = new ArrayList<>();
        String url = "/apiproxy/v3/queryVMs";
        String contentMD5Source = "";
        Map<String, String> headerParam = CTCloudApiUtils.setHeaderParam(zabbixAgentQueryInfo.getAccessKey(), zabbixAgentQueryInfo.getSecretKey(), url, contentMD5Source);
        headerParam.put("regionId", zabbixAgentQueryInfo.getRegionId());
        while (continue_) {
            headerParam.put("pageNo",String.valueOf(page_no));
            headerParam.put("pageSize",String.valueOf(page_size));
            url = zabbixAgentQueryInfo.getServerIP() + url;
            logger.info("url is " + url);
            //响应参数
            Map<String, Object> responseMap = null;
            responseMap = CTCloudApiUtils.getCTYunHttpBody(null, url, headerParam, "get");
            logger.info("responseMap is " + JSON.toJSONString(responseMap));
            String responseBody = (String) responseMap.get("body");
            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            if ((Integer) responseMap.get("code") == 200) {
                if (jsonObject.getInteger("statusCode") == 800) {
                    JSONObject returnObj = jsonObject.getJSONObject("returnObj");
                    row_count = row_count + returnObj.getInteger("rowCount");
                    JSONArray objJSONArray = returnObj.getJSONArray("result");
                    vmInfos.addAll(objJSONArray.toJavaList(VmInfo_CTCloud.class));
                    if (row_count >= returnObj.getInteger("totalCount")) {
                        continue_ = false;
                    } else {
                        page_no++;
                    }
                } else {
                    continue;
                }
            } else {
                continue;
            }
        }
        for (VmInfo_CTCloud vmInfo : vmInfos) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("{#VMNAME}", vmInfo.getVmName());
            jsonArray.add(jsonObject);
        }
        result.add("data", jsonArray);
        logger.info(result.toString());
        return result.toString();
    }
}
