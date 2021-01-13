package com.dcits.zabbixagentapiservice.Rest;

import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;
import com.dcits.zabbixagentapiservice.ZabbixAgentApiServiceApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={ZabbixAgentApiServiceApplication.class})
class ZabbixAgentRestServiceTest {
    @org.junit.jupiter.api.Test
    void getDiscoverInfo() throws IOException {
        ZabbixAgentQueryInfo zabbixAgentQueryInfo = new ZabbixAgentQueryInfo();
        zabbixAgentQueryInfo.setServerIP("http://api.ctyun.cn");
        zabbixAgentQueryInfo.setAccessKey("36065bacf0b94c57a85dc72c16cb029f");
        zabbixAgentQueryInfo.setSecretKey("0a2eba050000491ba84b22c5d2a63d25");
        zabbixAgentQueryInfo.setManagedCloudType("CT");
        zabbixAgentQueryInfo.setAction("discover_vm");
        zabbixAgentQueryInfo.setRegionId("100054c0416811e9a6690242ac110002");
        ZabbixAgentRestService zabbixAgentRestService = new ZabbixAgentRestService();
        zabbixAgentRestService.getDiscoverInfo(zabbixAgentQueryInfo);
    }

    @org.junit.jupiter.api.Test
    void getMonitorInfo() throws IOException {
        ZabbixAgentQueryInfo zabbixAgentQueryInfo = new ZabbixAgentQueryInfo();
        zabbixAgentQueryInfo.setServerIP("http://api.ctyun.cn");
        zabbixAgentQueryInfo.setAccessKey("36065bacf0b94c57a85dc72c16cb029f");
        zabbixAgentQueryInfo.setSecretKey("0a2eba050000491ba84b22c5d2a63d25");
        zabbixAgentQueryInfo.setManagedCloudType("CT");
        zabbixAgentQueryInfo.setAction("monitor_vm");
        zabbixAgentQueryInfo.setRegionId("100054c0416811e9a6690242ac110002");
        zabbixAgentQueryInfo.setInstanceName("test_VmOnly");
        ZabbixAgentRestService zabbixAgentRestService = new ZabbixAgentRestService();
        zabbixAgentRestService.getMonitorInfo(zabbixAgentQueryInfo);
    }
}