package com.alibaba.csp.sentinel.dashboard.rule.nacos;

/**
 * @author gblfy
 * @date 2021-08-05
 */
public final class NacosConfigUtil {

//    public static final String GROUP_ID = "SENTINEL_GROUP";

    public static final String FLOW_DATA_ID_POSTFIX = "-flow-rules";
    public static final String DEGRADE_DATA_ID_POSTFIX = "-degrade-rules";
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "-param-rules";
    public static final String SYS_DATA_ID_POSTFIX = "-system-rules";
    public static final String AUTH_DATA_ID_POSTFIX = "-auth-rules";

    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "-gateway-flow";
    public static final String GATEWAY_API_DATA_ID_POSTFIX = "-gateway-api";

    public static final String CLUSTER_MAP_DATA_ID_POSTFIX = "-cluster-map";

    /**
     * cc for `cluster-client`
     */
    public static final String CLIENT_CONFIG_DATA_ID_POSTFIX = "-cc-config";
    /**
     * cs for `cluster-server`
     */
    public static final String SERVER_TRANSPORT_CONFIG_DATA_ID_POSTFIX = "-cs-transport-config";
    public static final String SERVER_FLOW_CONFIG_DATA_ID_POSTFIX = "-cs-flow-config";
    public static final String SERVER_NAMESPACE_SET_DATA_ID_POSTFIX = "-cs-namespace-set";

    private NacosConfigUtil() {}
}
