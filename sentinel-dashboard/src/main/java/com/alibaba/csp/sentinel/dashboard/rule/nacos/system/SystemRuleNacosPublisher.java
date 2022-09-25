package com.alibaba.csp.sentinel.dashboard.rule.nacos.system;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosProperties;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.csp.sentinel.datasource.Converter;

import java.util.List;

/**
 * @author gblfy
 * @date 2021-08-05
 */
@Component("systemRuleNacosPublisher")
public class SystemRuleNacosPublisher implements DynamicRulePublisher<List<SystemRuleEntity>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemRuleNacosPublisher.class);
    @Autowired
    private NacosProperties nacosProperties;
    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<List<SystemRuleEntity>, String> converter;

    @Override
    public void publish(String app, List<SystemRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        String convertedRule = converter.convert(rules);
        LOGGER.info("sentinel dashboard publisher system rule : {}", convertedRule);
        configService.publishConfig(app + NacosConfigUtil.SYS_DATA_ID_POSTFIX,
                nacosProperties.getGroupId(), convertedRule);
    }
}
