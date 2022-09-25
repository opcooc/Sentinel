/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.csp.sentinel.dashboard.repository.metric;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.JdbcMetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.mapper.MetricMapper;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * Caches metrics data in a period of time to jdbc.
 *
 * @author Carpenter Lee
 * @author Eric Zhao
 */
@Component
public class JdbcMetricsRepository implements MetricsRepository<MetricEntity> {

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Resource
    private MetricMapper metricMapper;

    @Override
    public void save(MetricEntity entity) {
        if (entity == null || StringUtil.isBlank(entity.getApp())) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            metricMapper.insert(toPo(entity));
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            List<JdbcMetricEntity> metricList = new ArrayList<>();
            metrics.forEach(metric -> metricList.add(toPo(metric)));
            metricMapper.insertBatch(metricList);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource,
                                                           long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();

        if (StringUtil.isBlank(app)) {
            return results;
        }

        readWriteLock.readLock().lock();
        try {
            List<JdbcMetricEntity> metricList = metricMapper.selectList(app, resource, startTime, endTime);

            if (CollectionUtils.isEmpty(metricList)) {
                return results;
            }

            metricList.forEach(e -> results.add(toPo(e)));
            return results;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }

        final long minTimeMs = System.currentTimeMillis() - 1000 * 60;
        Map<String, MetricEntity> resourceCount = new ConcurrentHashMap<>(32);

        readWriteLock.readLock().lock();
        try {
            List<JdbcMetricEntity> metricList = metricMapper.selectList(app, minTimeMs);

            List<MetricEntity> metricEntityList = new ArrayList<>();
            metricList.forEach(e -> metricEntityList.add(toPo(e)));

            if (CollectionUtils.isEmpty(metricEntityList)) {
                return results;
            }

            for (MetricEntity newEntity : metricEntityList) {
                String resource = newEntity.getResource();
                if (resourceCount.containsKey(resource)) {
                    MetricEntity oldEntity = resourceCount.get(resource);
                    oldEntity.addPassQps(newEntity.getPassQps());
                    oldEntity.addRtAndSuccessQps(newEntity.getRt(), newEntity.getSuccessQps());
                    oldEntity.addBlockQps(newEntity.getBlockQps());
                    oldEntity.addExceptionQps(newEntity.getExceptionQps());
                    oldEntity.addCount(1);
                } else {
                    resourceCount.put(resource, MetricEntity.copyOf(newEntity));
                }
            }
            // Order by last minute b_qps DESC.
            return resourceCount.entrySet()
                    .stream()
                    .sorted((o1, o2) -> {
                        MetricEntity e1 = o1.getValue();
                        MetricEntity e2 = o2.getValue();
                        int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                        if (t != 0) {
                            return t;
                        }
                        return e2.getPassQps().compareTo(e1.getPassQps());
                    })
                    .map(Entry::getKey)
                    .collect(Collectors.toList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private JdbcMetricEntity toPo(MetricEntity metricEntity) {
        JdbcMetricEntity metric = new JdbcMetricEntity();
        BeanUtils.copyProperties(metricEntity, metric, JdbcMetricEntity.class);
        return metric;
    }

    private MetricEntity toPo(JdbcMetricEntity metric) {
        MetricEntity metricEntity = new MetricEntity();
        BeanUtils.copyProperties(metric, metricEntity, MetricEntity.class);
        return metricEntity;
    }
}
