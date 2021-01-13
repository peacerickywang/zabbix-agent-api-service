package com.dcits.zabbixagentapiservice.Actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dcits.zabbixagentapiservice.Action;
import com.dcits.zabbixagentapiservice.Model.CTCloud.Monitor.CTCloudMetricResponse;
import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;
import com.dcits.zabbixagentapiservice.Util.CTCloudApiUtils;
import com.google.gson.JsonObject;
import com.huawei.esdk.fusioncompute.local.ServiceFactory;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.FCSDKResponse;
import com.huawei.esdk.fusioncompute.local.model.PageList;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricReq;
import com.huawei.esdk.fusioncompute.local.model.common.QueryObjectmetricResp;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import com.huawei.esdk.fusioncompute.local.model.vm.QueryVmsReq;
import com.huawei.esdk.fusioncompute.local.model.vm.VmInfo;
import com.huawei.esdk.fusioncompute.local.resources.common.MonitorResource;
import com.huawei.esdk.fusioncompute.local.resources.vm.VmResource;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonitorVM implements Action {
    /**
     * 错误码
     */
    public static String SUCCESS_CODE = "800";
    private static Logger logger = LoggerFactory.getLogger(MonitorVM.class);

    @Override
    public String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
        String result = null;
        String siteUri = siteBasicInfoList.get(0).getUri();
        QueryVmsReq queryVmsReq = new QueryVmsReq();
        queryVmsReq.setLimit(1);
        queryVmsReq.setOffset(0);
        queryVmsReq.setUuid(instanceName);
        // 获取VmResource接口的实现
        VmResource instance = ServiceFactory.getService(VmResource.class, clientProvider);
        FCSDKResponse<PageList<VmInfo>> response = instance.queryVMs(queryVmsReq, siteUri);
        if (!response.getErrorCode().equals(SUCCESS_CODE)) {
            logger.error("QUERY VM info FAILED");
        } else {
            if (response.getResult().getList().size()==0){
                return String.valueOf(0);
            }
            logger.debug("VM INFO: "+ JSON.toJSONString(response));
            List<String> metricId = new ArrayList<String>();
            metricId.add(metric);
            QueryObjectmetricReq queryObjectmetricReq = new QueryObjectmetricReq();
            queryObjectmetricReq.setUrn(response.getResult().getList().get(0).getUrn());
            queryObjectmetricReq.setMetricId(metricId);
            List<QueryObjectmetricReq> reqs = new ArrayList<QueryObjectmetricReq>();
            reqs.add(queryObjectmetricReq);
            MonitorResource monitorResource = ServiceFactory.getService(MonitorResource.class, clientProvider);
            FCSDKResponse<QueryObjectmetricResp> queryObjectmetricResp = monitorResource.queryObjectmetricRealtimedata(siteUri, reqs);
            String value = queryObjectmetricResp.getResult().getItems().get(0).getValue().get(0).getMetricValue();
            if (value.isEmpty()) {
                result = String.valueOf(Float.parseFloat("0"));
                logger.debug(String.valueOf(Float.parseFloat("0")));
            } else {
                result = String.valueOf(Float.parseFloat(value));
                logger.debug(String.valueOf(Float.parseFloat(value)));
            }
        }
        return result;
    }

    @Override
    public String doAction(ZabbixAgentQueryInfo zabbixAgentQueryInfo, String instanceName, String metric) throws IOException {
        CTCloudMetricResponse metricResponse = new CTCloudMetricResponse();
        String url = "/apiproxy/v4/os/queryMetricLast";
        String contentMD5Source = "";
        Map<String, String> headerParam = CTCloudApiUtils.setHeaderParam(zabbixAgentQueryInfo.getAccessKey(), zabbixAgentQueryInfo.getSecretKey(), url, contentMD5Source);
        List<String> stringList = new ArrayList<>();
        stringList.add(instanceName);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("regionId",zabbixAgentQueryInfo.getRegionId());
        jsonObject.put("zabbix_name_list",stringList);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("jsonStr", jsonObject.toJSONString()));
        url = zabbixAgentQueryInfo.getServerIP() + url;
        //响应参数
        Map<String, Object> responseMap = null;
        responseMap = CTCloudApiUtils.getCTYunHttpBody(formParams, url, headerParam, "post");
        logger.info("responseMap is " + JSON.toJSONString(responseMap));
        if (responseMap.get("statusCode").equals(SUCCESS_CODE)){
            JSONObject metricObject = jsonObject.parseObject(responseMap.get("data").toString());
            metricResponse = metricObject.getObject(instanceName,CTCloudMetricResponse.class);
            logger.info(instanceName);
            logger.info(JSON.toJSONString(metricResponse));
        }
        return JSON.toJSONString(metricResponse);
    }
}
