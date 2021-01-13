package com.dcits.zabbixagentapiservice;

import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;

import java.io.IOException;

public interface ManagedCloud {
    String doAction(ZabbixAgentQueryInfo zabbixAgentQueryInfo) throws IOException;
}
