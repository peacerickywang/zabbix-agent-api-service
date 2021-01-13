package com.dcits.zabbixagentapiservice.Model.CTCloud;

/**
 * 主机信息
 * 涉及接口：http://ctyun-api-url/apiproxy/v3/queryVMs 根据资源池id查询主机列表（融合）
 * http://ctyun-api-url/apiproxy/v3/queryVMsByOrderId 根据订单ID查询主机信息（融合）
 * 用途：出参
 * @auth ChenHaoQi
 * @package com.chq.springcloud.bo
 * @date 2020/12/17
 */
public class VmInfo_CTCloud {

    /**
     * 账户 id
     */
    private String accountId;
    /**
     * 主机对应的虚拟资源 id
     */
    private String workOrderResourceId;
    /**
     * 资源池可用区 id
     */
    private String zoneId;
    /**
     * uuid
     */
    private String id;
    /**
     * 资源池 id
     */
    private String regionId;
    /**
     * 真实主机 id
     */
    private String resVmId;
    /**
     * 主订单 id
     */
    private String orderId;
    /**
     * 用户 id
     */
    private String userId;
    /**
     * 子网 id
     */
    private String vlanId;
    /**
     * 主机核数
     */
    private int cpuNum;
    /**
     * 创建时间
     */
    private Long createDate;
    /**
     * 是否冻结 （1-冻结，0-未冻结）
     */
    private int isFreeze;
    /**
     * 主机内存大小
     */
    private int memSize;

    /**
     * 主机的系统类型
     */
    private String osStyle;
    /**
     * 主机状态（主机对应的虚拟资源状态）
     * -1	未知
     * 0	错误
     * 1	正在启用
     * 2	启用
     * 3	续订
     * 4	升级
     * 5	退订
     * 6	过期
     * 7	销毁
     * 8	转按量
     * 9	后付费降级
     * 10	已自动转按量
     * 11	按量转包周期
     * 999	逻辑删除
     */
    private Integer status;
    /**
     * 主机名称
     */
    private String vmName;
    /**
     * 主机实际状态：
     * RESTARTING   重启中
     * RUNNING      运行中
     * STOPPING	   关机中
     * STOPPED	   关机
     * STARTING	   开机中
     * DUEING	   销毁中
     * DELETE	   删除
     * FREEZING	   过期
     * OPENING	   开通中
     * UPDATING	   变更规格中
     */
    private String vmStatus;
    /**
     * 资源池名称
     */
    private String zoneName;
    /**
     * 到期时间
     */
    private Long dueDate;

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getWorkOrderResourceId() {
        return workOrderResourceId;
    }

    public void setWorkOrderResourceId(String workOrderResourceId) {
        this.workOrderResourceId = workOrderResourceId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getResVmId() {
        return resVmId;
    }

    public void setResVmId(String resVmId) {
        this.resVmId = resVmId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVlanId() {
        return vlanId;
    }

    public void setVlanId(String vlanId) {
        this.vlanId = vlanId;
    }

    public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public int getIsFreeze() {
        return isFreeze;
    }

    public void setIsFreeze(int isFreeze) {
        this.isFreeze = isFreeze;
    }

    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int memSize) {
        this.memSize = memSize;
    }

    public String getOsStyle() {
        return osStyle;
    }

    public void setOsStyle(String osStyle) {
        this.osStyle = osStyle;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public String getVmStatus() {
        return vmStatus;
    }

    public void setVmStatus(String vmStatus) {
        this.vmStatus = vmStatus;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    @Override
    public String toString() {
        return "VmInfo{" +
                "accountId='" + accountId + '\'' +
                ", workOrderResourceId='" + workOrderResourceId + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", id='" + id + '\'' +
                ", regionId='" + regionId + '\'' +
                ", resVmId='" + resVmId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", vlanId='" + vlanId + '\'' +
                ", cpuNum=" + cpuNum +
                ", createDate=" + createDate +
                ", isFreeze=" + isFreeze +
                ", memSize=" + memSize +
                ", osStyle='" + osStyle + '\'' +
                ", status=" + status +
                ", vmName='" + vmName + '\'' +
                ", vmStatus='" + vmStatus + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", dueDate='" + dueDate + '\'' +
                '}';
    }
}

