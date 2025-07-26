package com.enn.iot.dtu.common.properties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Component
@ConfigurationProperties(prefix = "enn")
@Slf4j
@Validated
@Data
@ToString
public class IotProperties {

    private static final Duration DEFAULT_READER_IDLE_TIMEOUT = Duration.ofMinutes(5);
    private static final Duration DEFAULT_WRITER_IDLE_TIMEOUT = Duration.ofMinutes(10);
    private static final Duration DEFAULT_ALL_IDLE_TIMEOUT = Duration.ofMinutes(0);
    private static final Duration DEFAULT_RESPONSE_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration DEFAULT_COLLECT_POLLING_INTERVAL = Duration.ofSeconds(40);
    private static final Duration DEFAULT_TRAFFIC_CHECK_INTERVAL = Duration.ofMinutes(10);
    private static final int DEFAULT_RETRY_COUNT = 3;
    /**
     * 读空闲最大时长，0为禁用<br/>
     * 主机长时间没有读取到数据。 <br/>
     * <code>
     * 场景：
     * 1、从机连接后，主机长时间没有读取到认证报文。
     * 2、主机发送多次请求后，从机都没有发送应答报文。
     * 要求：
     * 1、{读空闲最大时长} 必须大于 {采集周期}，避免在两轮采集的间隔时间中触发"读空闲事件"
     * 2、{读空闲最大时长} 必须大于 {应答超时时间}，确保"应答超时事件"先于"读空闲事件"触发
     * 3、{读空闲最大时长} 必须大于 {采集周期}+{应答超时时间}，确保"应答超时事件"先于"读空闲事件"触发，否则设置为 2 * ({采集周期}+{应答超时时间})
     * </code>
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration readerIdleTimeout = DEFAULT_READER_IDLE_TIMEOUT;
    /**
     * 写空闲最大时长，0为禁用<br/>
     * 主机长时间没有发送数据。<br/>
     * 配置为 0 时，禁用<br/>
     * <code>
     * 场景：
     * 1、从机连接后，主机长时间没有读取到认证报文，导致没有执行首个指令。
     * 2、DTU下无设备、或有设备没有测点。每个间隔{写空闲最大时长}就会断开与DTU的连接。
     * 3、主机有bug，没有正确的轮询采集数据。超过{写空闲最大时长}后就会断开与DTU的连接。
     * 要求：
     * 1、{写空闲最大时长}必须大于{读空闲最大时长}，避免场景1时提前触发"写空闲事件"
     * </code>
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration writerIdleTimeout = DEFAULT_WRITER_IDLE_TIMEOUT;
    /**
     * 读写空闲最大时长，默认禁用<br/>
     * 主机长时间没有读取到数据，也没有发送数据。<br/>
     * <code>
     * 场景：
     * 1、从机连接后，从机长时间没有发送认证报文。
     * 2、DTU下无设备、或有设备没有测点。
     * 3、主机有bug，没有正确的轮询采集数据
     * 因为以上所有场景已经被{读空闲最大时长}和{写空闲最大时长}的场景覆盖了，所以禁用。
     * </code>
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration allIdleTimeout = DEFAULT_ALL_IDLE_TIMEOUT;
    /**
     * 应答超时时间，0为禁用 <br/>
     * 主机发送指令请求后，长时间没有读取到指令应答数据。<br/>
     * <code>
     * 推荐：
     * 1、{应答超时时间}应该小于{读空闲最大时长}，确保"应答超时事件"先于"读空闲事件"触发
     * 默认：3秒
     * </code>
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration responseTimeout = DEFAULT_RESPONSE_TIMEOUT;

    /**
     * 采集周期，采集轮询间隔。
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration collectPollingInterval = DEFAULT_COLLECT_POLLING_INTERVAL;

    /**
     * 流量检查间隔，0为禁用
     */
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration trafficCheckInterval = DEFAULT_TRAFFIC_CHECK_INTERVAL;

    private Integer retryCount = DEFAULT_RETRY_COUNT;

    @PostConstruct
    private void defaultValue() {
        if (readerIdleTimeout == null || readerIdleTimeout.isNegative()) {
            readerIdleTimeout = DEFAULT_READER_IDLE_TIMEOUT;
        }
        if (writerIdleTimeout == null || writerIdleTimeout.isNegative()) {
            writerIdleTimeout = DEFAULT_WRITER_IDLE_TIMEOUT;
        }
        if (allIdleTimeout == null || allIdleTimeout.isNegative()) {
            allIdleTimeout = DEFAULT_ALL_IDLE_TIMEOUT;
        }
        if (responseTimeout == null || responseTimeout.isNegative()) {
            responseTimeout = DEFAULT_RESPONSE_TIMEOUT;
        }
        if (collectPollingInterval == null || collectPollingInterval.isZero() || collectPollingInterval.isNegative()) {
            collectPollingInterval = DEFAULT_COLLECT_POLLING_INTERVAL;
        }
        // {读空闲最大时长} 必须大于 {采集周期}+{应答超时时间}, 否则设置为 2 * ({采集周期}+{应答超时时间})
        if (!readerIdleTimeout.isZero() && !responseTimeout.isZero()
                && readerIdleTimeout.compareTo(collectPollingInterval.plus(responseTimeout)) <= 0) {
            readerIdleTimeout = collectPollingInterval.plus(responseTimeout).multipliedBy(2);
        }
        if (!writerIdleTimeout.isZero() && !readerIdleTimeout.isZero()
                && writerIdleTimeout.compareTo(readerIdleTimeout) <= 0) {
            writerIdleTimeout = readerIdleTimeout.multipliedBy(2);
        }
        if (trafficCheckInterval == null || trafficCheckInterval.isNegative()) {
            trafficCheckInterval = DEFAULT_TRAFFIC_CHECK_INTERVAL;
        }

        if (retryCount == null) {
            retryCount = DEFAULT_RETRY_COUNT;
        }

        if (log.isInfoEnabled()) {
            log.info(this.toString());
        }
    }
}
