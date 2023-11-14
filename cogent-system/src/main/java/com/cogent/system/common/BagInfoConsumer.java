package com.cogent.system.common;

import com.cogent.system.dao.MediaHistoryInfoDao;
import com.cogent.system.domain.DO.bag.MediaHistoryInfoDO;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @Author keboom
 * @Date 2023-06-06 14:15
 */
@Slf4j
@Order(2)
@Component
public class BagInfoConsumer {

    @Resource
    MediaHistoryInfoDao mediaHistoryInfoDao;

    List<MediaHistoryInfoDO> mediaLinkInfo;

    void consumeMediaLinkInfo() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    mediaLinkInfo = new ArrayList<>(100);
                    Queues.drain(BagInfoReport.mediaLinkInfoQueue, mediaLinkInfo, 50, 30, TimeUnit.SECONDS);
                    mediaHistoryInfoDao.saveBatch(mediaLinkInfo);

                }
            }
        });
    }
}
