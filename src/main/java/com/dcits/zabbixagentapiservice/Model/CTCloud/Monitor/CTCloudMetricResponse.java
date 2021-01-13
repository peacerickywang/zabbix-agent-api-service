package com.dcits.zabbixagentapiservice.Model.CTCloud.Monitor;

import lombok.Data;

@Data
public class CTCloudMetricResponse {
    /**
     * 监控粒度开启状态，无（-1）|开启（0）|关闭（1）
     */
    String extend_status;
    /**
     * CPU使用率
     */
    CTCloudMetric cpu_util;
    /**
     * 内存使用率
     */
    CTCloudMetric mem_util;
    /**
     * 磁盘分配率
     */
    CTCloudMetric disk_util_inband;
    /**
     * 磁盘读速率
     */
    CTCloudMetric disk_read_bytes_rate;
    /**
     * 磁盘写速率
     */
    CTCloudMetric disk_write_bytes_rate;
    /**
     * 网络流入速率
     */
    CTCloudMetric network_incoming_bytes_rate_inband;
    /**
     * 网络流出速率
     */
    CTCloudMetric network_outing_bytes_rate_inband;
    /**
     * 磁盘读请求速率
     */
    CTCloudMetric disk_read_requests_rate;
    /**
     * 磁盘写请求速率
     */
    CTCloudMetric disk_write_requests_rate;
}
