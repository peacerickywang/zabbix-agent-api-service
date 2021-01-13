package com.dcits.zabbixagentapiservice;

import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;
import com.huawei.esdk.fusioncompute.local.model.ClientProviderBean;
import com.huawei.esdk.fusioncompute.local.model.site.SiteBasicInfo;

import java.io.IOException;
import java.util.List;

public interface Action {
    String doAction(ClientProviderBean clientProvider, List<SiteBasicInfo> siteBasicInfoList, String instanceName, String metric);
    String doAction(ZabbixAgentQueryInfo zabbixAgentQueryInfo, String instanceName, String metric) throws IOException;
}