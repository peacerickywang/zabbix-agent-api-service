package com.dcits.zabbixagentapiservice.ManagedClouds;

import com.dcits.zabbixagentapiservice.Action;
import com.dcits.zabbixagentapiservice.ActionFactory;
import com.dcits.zabbixagentapiservice.ManagedCloud;
import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;

import java.io.IOException;

public class CTCloud implements ManagedCloud {
    @Override
    public String doAction(ZabbixAgentQueryInfo zabbixAgentQueryInfo) throws IOException {
        Action targetAction = ActionFactory.getAction(zabbixAgentQueryInfo.getAction()).orElseThrow(() -> new IllegalArgumentException("Invalid Action"));
        return targetAction.doAction(zabbixAgentQueryInfo, zabbixAgentQueryInfo.getInstanceName(), zabbixAgentQueryInfo.getMetric());
    }
}
