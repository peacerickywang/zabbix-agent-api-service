package com.dcits.zabbixagentapiservice.Model.CTCloud.Monitor;

import lombok.Data;

@Data
public class CTCloudMetric {
    /**
     * 监控最新数据
     */
    String lastvalue = "0";
    /**
     * 监控最新时间戳
     */
    String lastclock;
    /**
     * 监控状态，3分钟内是否有监控，
     * true/false
     */
    String agent_status;
}
