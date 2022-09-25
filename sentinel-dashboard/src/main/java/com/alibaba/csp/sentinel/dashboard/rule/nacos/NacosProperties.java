package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import org.apache.commons.lang.StringUtils;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gblfy
 * @date 2021-08-05
 */
@ConfigurationProperties("sentinel.nacos")
public class NacosProperties {

    private static final String NACOS_SERVER_ADDR = "NACOS_SERVER_ADDR";
    private static final String NACOS_GROUP_ID = "NACOS_GROUP_ID";
    private static final String NACOS_NAMESPACE = "NACOS_NAMESPACE";

    private String serverAddr;
    private String dataId;
    private String groupId;
    private String namespace;

    public String getServerAddr() {
        String nacosServerAddr = getEnv(NACOS_SERVER_ADDR);
        if (StringUtils.isNotBlank(nacosServerAddr)) {
            return nacosServerAddr;
        }
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroupId() {
        String nacosGroupId = getEnv(NACOS_GROUP_ID);
        if (StringUtils.isNotBlank(nacosGroupId)) {
            return nacosGroupId;
        }
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getNamespace() {
        String nacosNamespace = getEnv(NACOS_NAMESPACE);
        if (StringUtils.isNotBlank(nacosNamespace)) {
            return nacosNamespace;
        }
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    private static String getEnv(String key) {
        return System.getenv(key);
    }
}
