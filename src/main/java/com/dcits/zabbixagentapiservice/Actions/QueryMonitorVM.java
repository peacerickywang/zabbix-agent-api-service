package com.dcits.zabbixagentapiservice.Actions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dcits.zabbixagentapiservice.Action;
import com.dcits.zabbixagentapiservice.Model.CTCloud.Monitor.CTCloudMetricResponse;
import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;
import com.dcits.zabbixagentapiservice.Util.CTCloudApiUtils;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryMonitorVM implements Action {
    /**
     * 错误码
     */
    public static String SUCCESS_CODE = "800";
    private static Logger logger = LoggerFactory.getLogger(QueryMonitorVM.class);

    @Override
    public String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric) {
        return null;
    }

    @Override
    public String doAction(ZabbixAgentQueryInfo zabbixAgentQueryInfo, String instanceName, String metric) throws IOException {
        CTCloudMetricResponse metricResponse = new CTCloudMetricResponse();
        String url = "/apiproxy/v4/os/queryMetricDetails";
        String contentMD5Source = "";
        Map<String, String> headerParam = CTCloudApiUtils.setHeaderParam(zabbixAgentQueryInfo.getAccessKey(), zabbixAgentQueryInfo.getSecretKey(), url, contentMD5Source);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("regionId",zabbixAgentQueryInfo.getRegionId());
        jsonObject.put("uuid",instanceName);
        jsonObject.put("ser","ecs");
        jsonObject.put("dim","ecs");
        jsonObject.put("metric_name","[\"cpu_util\",\"mem_util\"]");
        jsonObject.put("period",1200);
        jsonObject.put("filter","avg");
        jsonObject.put("time_range",1440);
        jsonObject.put("time_from",1612156176);
        jsonObject.put("time_till",1612328976);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("regionId", zabbixAgentQueryInfo.getRegionId()));
        formParams.add(new BasicNameValuePair("uuid", instanceName));
        formParams.add(new BasicNameValuePair("ser", "ser"));
        formParams.add(new BasicNameValuePair("dim", "dim"));
        formParams.add(new BasicNameValuePair("metric_name", "[\"cpu_util\",\"mem_util\"]"));
        formParams.add(new BasicNameValuePair("period", "1200"));
        formParams.add(new BasicNameValuePair("filter", "avg"));
        formParams.add(new BasicNameValuePair("time_range", "1440"));
        formParams.add(new BasicNameValuePair("time_from", "1612156176"));
        formParams.add(new BasicNameValuePair("time_till", "1612156176"));
        url = zabbixAgentQueryInfo.getServerIP() + url;
        //响应参数
        Map<String, Object> responseMap = null;
        responseMap = CTCloudApiUtils.getCTYunHttpBody(formParams, url, headerParam, "GET");
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
