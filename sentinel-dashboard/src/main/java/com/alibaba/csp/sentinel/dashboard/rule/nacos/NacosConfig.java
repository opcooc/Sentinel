package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import org.apache.commons.lang.StringUtils;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

/**
 * @author gblfy
 * @date 2021-08-05
 */
@EnableConfigurationProperties(NacosProperties.class)
@Configuration
public class NacosConfig {

    //====================流控规则 Converter
    @Bean
    public Converter<List<FlowRuleEntity>, String> flowRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<FlowRuleEntity>> flowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, FlowRuleEntity.class);
    }

    //====================降级规则 Converter
    @Bean
    public Converter<List<DegradeRuleEntity>, String> degradeRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<DegradeRuleEntity>> degradeRuleEntityDecoder() {
        return s -> JSON.parseArray(s, DegradeRuleEntity.class);
    }

    //====================热点规则 Converter
    @Bean
    public Converter<List<ParamFlowRuleEntity>, String> paramsRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<ParamFlowRuleEntity>> paramsRuleEntityDecoder() {
        return s -> JSON.parseArray(s, ParamFlowRuleEntity.class);
    }

    //====================系统规则 Converter
    @Bean
    public Converter<List<SystemRuleEntity>, String> systemRuleEntityEncoder() {
        return JSON::toJSONString;
    }

    @Bean
    public Converter<String, List<SystemRuleEntity>> systemRuleEntityDecoder() {
        return s -> JSON.parseArray(s, SystemRuleEntity.class);
    }

    //====================授权规则 Converter
    @Bean
    public Converter<List<AuthorityRuleEntity>, String> authRuleEntityEncoder() {
        return JSON::toJSONString;
    }


    @Bean
    Converter<String, List<AuthorityRuleEntity>> authRuleEntityDecoder() {
        return s -> JSON.parseArray(s, AuthorityRuleEntity.class);
    }

    //====================网关限流规则 Converter
    @Bean
    public Converter<List<GatewayFlowRuleEntity>, String> gatewayFlowRuleEntityEncoder() {
        return JSON::toJSONString;
    }


    @Bean
    Converter<String, List<GatewayFlowRuleEntity>> gatewayFlowRuleEntityDecoder() {
        return s -> JSON.parseArray(s, GatewayFlowRuleEntity.class);
    }

    //====================网关API限流规则 Converter
    @Bean
    public Converter<List<ApiDefinitionEntity>, String> gatewayApiRuleEntityEncoder() {
        return JSON::toJSONString;
    }


    @Bean
    Converter<String, List<ApiDefinitionEntity>> gatewayApiRuleEntityDecoder() {
        return s -> JSON.parseArray(s, ApiDefinitionEntity.class);
    }


    @Bean
    public ConfigService nacosConfigService(NacosProperties nacosProperties) throws Exception {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosProperties.getServerAddr());
        if(StringUtils.isNotBlank(nacosProperties.getNamespace())){
            properties.put(PropertyKeyConst.NAMESPACE, nacosProperties.getNamespace());
        }
        return ConfigFactory.createConfigService(properties);
    }
}
