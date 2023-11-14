package com.cogent.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.dao.MinioObjectDao;
import com.cogent.system.domain.DO.bag.MediaCurrentInfoDO;
import com.cogent.system.domain.DO.bag.MediaHistoryInfoDO;
import com.cogent.system.domain.DO.fileUpload.MinioObjectDO;
import com.cogent.system.mapper.MediaCurrentInfoMapper;
import com.cogent.system.mapper.MediaHistoryInfoMapper;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/6
 * {@code @description:}
 */
@Slf4j
@Component
public class SystemTask {
    @Resource
    private MediaCurrentInfoMapper mediaCurrentInfoMapper;
    @Resource
    private MediaHistoryInfoMapper statusMapper;
    @Resource
    private MinioObjectDao minioObjectDao;
    @Value("${minio.addr}")
    private String minioAddress;
    @Value("${minio.accessKey}")
    private String minioAccessKey;
    @Value("${minio.secretKey}")
    private String minioSecretKey;

    /**
     * 每15秒检查设备上报的状态，如果上报的状态已经是15秒之前了，那么说明设备离线了，更新设备状态
     */
    @Scheduled(cron = "*/15 * * ? * *")
    public void checkDeviceStatus() {
        // 只看那些正在返送和正在直播的，如果他们的更新超过15秒，那么就是离线了, 2 是直播中，返送中
        LambdaQueryWrapper<MediaCurrentInfoDO> query = Wrappers.lambdaQuery();
        query.eq(MediaCurrentInfoDO::getStaLiveState, 2)
                .or()
                .eq(MediaCurrentInfoDO::getStaFeedbackState, 2);
        List<MediaCurrentInfoDO> livingDevice = mediaCurrentInfoMapper.selectList(query);
        long currentTimeMillis = System.currentTimeMillis();
        for (MediaCurrentInfoDO aDo : livingDevice) {
            long lastUpdateTime = aDo.getUpdateTime().getTime();
            long delayTime = currentTimeMillis - lastUpdateTime;
            if (delayTime > (15 * 1000)) {
                log.error("{} status report timed out 15s", aDo.getSn());
            }
        }

    }



    /**
     * java启动一分钟后执行，接着是每个天执行一次
     * 如果minio在运行过程中，java程序停止了，此时有人向minio上传了文件，那么java数据库中的object与minio就不一致
     * 因此此定时任务进行数据同步的操作。
     * 并且只同步了cogent这个bucket
     */
    @SneakyThrows
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000, initialDelay = 60 * 1000)
    public void checkMinioObject() {
        log.info("Check Minio object whether it is consistent");
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://" + minioAddress)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket("cogent").recursive(true).build());

        // listObject将放到map
        // 数据库中的数据也放到map
        // 遍历listObject，如果数据库中没有，那么就插入数据库。如果数据库有，那么就删除数据库map中的数据
        // 最后，数据库中map剩下的数据，就是要删除的数据
        HashMap<String, MinioObjectDO> listObjects = new HashMap<>();
        for (Result<Item> result : results) {
            Item item = result.get();
            MinioObjectDO aDo = new MinioObjectDO();
            String objectName = item.objectName();
            // 我们的bucket都是cogent
            String keyName = "cogent/" + objectName;
            aDo.setKeyName(keyName);
            aDo.setUploadTime(Date.from(item.lastModified().toInstant()));
            aDo.setSize((int) item.size());
            String[] split = StringUtils.split(objectName, "/");
            aDo.setObjectName(split[split.length - 1]);
            listObjects.put(keyName, aDo);
        }

        List<MinioObjectDO> dataBaseObjects = minioObjectDao.list();
        HashMap<String, MinioObjectDO> dataBaseMap = new HashMap<>();
        for (MinioObjectDO dataBaseObject : dataBaseObjects) {
            dataBaseMap.put(dataBaseObject.getKeyName(), dataBaseObject);
        }

        listObjects.forEach((key, value) -> {
            if (dataBaseMap.containsKey(key)) {
                dataBaseMap.remove(key);
            } else {
                minioObjectDao.save(value);
            }
        });

        dataBaseMap.forEach((key, value) -> minioObjectDao.removeByKeyName(key));
    }
}
