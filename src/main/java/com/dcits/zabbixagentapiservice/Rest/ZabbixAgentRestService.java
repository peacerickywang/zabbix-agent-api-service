package com.dcits.zabbixagentapiservice.Rest;

import com.alibaba.fastjson.JSON;
import com.dcits.zabbixagentapiservice.ManagedCloud;
import com.dcits.zabbixagentapiservice.ManagedCloudFactory;
import com.dcits.zabbixagentapiservice.Model.ZabbixAgentQueryInfo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Service("ZabbixAgentRestService")
@RestController
public class ZabbixAgentRestService {
    private static Logger logger = LoggerFactory.getLogger(ZabbixAgentRestService.class);
    @ApiOperation("获取zabbix自发现数据")
    @RequestMapping(value = "/discover", method = RequestMethod.PUT)
    @ResponseBody
    public String getDiscoverInfo(@RequestBody ZabbixAgentQueryInfo zabbixAgentQueryInfo) throws IOException {
        logger.info("ZabbixAgentQueryInfo:");
        logger.info(JSON.toJSONString(zabbixAgentQueryInfo));
        ManagedCloud managedCloud = ManagedCloudFactory.getManagedCloud(zabbixAgentQueryInfo.getManagedCloudType()).orElseThrow(() -> new IllegalArgumentException("Invalid ManagedCloud"));
        return managedCloud.doAction(zabbixAgentQueryInfo);
    }

    @ApiOperation("获取zabbix监控数据")
    @RequestMapping(value = "/monitor", method = RequestMethod.PUT)
    @ResponseBody
    public float getMonitorInfo(@RequestBody ZabbixAgentQueryInfo zabbixAgentQueryInfo) throws IOException {
        logger.info("ZabbixAgentQueryInfo:");
        logger.info(JSON.toJSONString(zabbixAgentQueryInfo));
        ManagedCloud managedCloud = ManagedCloudFactory.getManagedCloud(zabbixAgentQueryInfo.getManagedCloudType()).orElseThrow(() -> new IllegalArgumentException("Invalid ManagedCloud"));
        return Float.parseFloat(managedCloud.doAction(zabbixAgentQueryInfo));
    }
}