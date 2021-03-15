package com.dcits.zabbixagentapiservice;


import com.dcits.zabbixagentapiservice.Actions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ActionFactory {
    static Map<String, Action> actionMap = new HashMap<>();

    static {
        actionMap.put("discover_host", new DiscoverHosts());
        actionMap.put("discover_vm", new DiscoverVMs());
        actionMap.put("monitor_vm", new MonitorVM());
        actionMap.put("monitor_host", new MonitorHost());
        actionMap.put("query_monitor_vm", new QueryMonitorVM());
    }

    public static Optional<Action> getAction (String action){
        return Optional.ofNullable(actionMap.get(action));
    }
}
