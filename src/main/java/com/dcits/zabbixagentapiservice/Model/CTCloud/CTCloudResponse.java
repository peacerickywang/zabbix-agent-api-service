package com.dcits.zabbixagentapiservice.Model.CTCloud;

import lombok.Data;

/**
 * 天翼云API通用返回值（顶级）
 * @auth ChenHaoQi
 * @package com.chq.springcloud.model
 * @date 2020/12/7
 */
@Data
public class CTCloudResponse {

    /**
     * 状态码：800-成功、900-失败、400-客户端失败
     */
    private String statusCode;

    /**
     * response结果，可能是map也可能是 list<map>
     */
    private Object returnObj;

    /**
     * 错误信息（900时才有）
     */
    private String message;
}
