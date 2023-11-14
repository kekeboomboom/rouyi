package com.cogent.system.common;

import com.cogent.system.domain.DO.bag.MediaHistoryInfoDO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author keboom
 * @Date 2023-06-06 10:20
 * 背包配置 状态信息上报，需要进行批量插入，采用消费者生产者模型
 */
@Slf4j
@Component
@Order(1)
public class BagInfoReport {

    public static BlockingQueue<MediaHistoryInfoDO> mediaLinkInfoQueue = new LinkedBlockingQueue<>(500);

    @Resource
    BagInfoConsumer consumer;


    @PostConstruct
    public void startConsume() {
        consumer.consumeMediaLinkInfo();
    }

    @SneakyThrows
    public void produceMediaLinkInfo(MediaHistoryInfoDO mediaHistoryInfoDO) {
        mediaLinkInfoQueue.add(mediaHistoryInfoDO);
    }
}
