package com.dcits.zabbixagentapiservice;

import com.dcits.zabbixagentapiservice.ManagedClouds.CTCloud;
import com.dcits.zabbixagentapiservice.ManagedClouds.FusionCompute;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ManagedCloudFactory {
    static Map<String, ManagedCloud> managedCloudMap = new HashMap<>();

    static {
        managedCloudMap.put("FC", new FusionCompute());
        managedCloudMap.put("CT", new CTCloud());
    }

    public static Optional<ManagedCloud> getManagedCloud (String managedCloudType){
        return Optional.ofNullable(managedCloudMap.get(managedCloudType));
    }
}
