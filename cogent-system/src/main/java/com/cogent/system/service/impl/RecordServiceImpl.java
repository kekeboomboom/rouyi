package com.cogent.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.HttpUtil;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.common.LiveState;
import com.cogent.system.common.RecordType;
import com.cogent.system.dao.MediaCurInfoDao;
import com.cogent.system.dao.RecordDao;
import com.cogent.system.domain.DO.bag.BagDO;
import com.cogent.system.domain.DO.bag.MediaCurrentInfoDO;
import com.cogent.system.domain.DO.record.RecordDO;
import com.cogent.system.domain.vo.mediaHook.OnRecordMp4Req;
import com.cogent.system.domain.vo.mediaHook.StreamChangedReq;
import com.cogent.system.domain.vo.record.RecordSwitchReq;
import com.cogent.system.domain.vo.record.RecordVO;
import com.cogent.system.domain.vo.record.UpdateRecordReq;
import com.cogent.system.mapper.BagMapper;
import com.cogent.system.service.IRecordService;
import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/23
 * {@code @description:}
 */
@Slf4j
@Service
public class RecordServiceImpl implements IRecordService {

    @Resource
    private BagMapper bagMapper;
    @Resource
    private MediaCurInfoDao mediaCurInfoDao;
    @Resource
    private RecordDao recordDao;

    /**
     * 判断此设备是否开启录像，如果开启录像，判断mediakit是否已经录像，如果已经录像，那么我们就不再去开了，如果没有那么就开启。
     *
     * @param req
     */
    @Override
    public void startRecord(StreamChangedReq req) {
        // 先判断数据库中是否开启录像
        BagDO bagBySN = bagMapper.getBagBySN(req.getStream());
        if (!bagBySN.getRecordSwitch()) {
            log.info("设备 {} 未开启录像", req.getStream());
            return;
        }
        startMediakitRecord(req);
    }

    private void startMediakitRecord(StreamChangedReq req) {
        // 如果开启录像，那么就去mediakit中判断是否正在录像，如果正在录像，那么就不去开启录像，如果没有录像，那么就去开启录像
        HashMap<String, String> param = new HashMap<>();
        // 现在通过判断是否有rtmp的流，来判断是否有返送流
        param.put("vhost", "__defaultVhost__");
        param.put("app", req.getApp());
        param.put("stream", req.getStream());
        param.put("type", String.valueOf(RecordType.MP4.getCode()));
        JSONObject isRecording = HttpUtil.getMediaKitRequest(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/isRecording", param);
        if (isRecording.getInteger("code") != 0) {
            throw new ServiceException("请求mediakit接口失败: " + isRecording.toString());
        }
        if (isRecording.getBoolean("status")) {
            log.info("设备 {} 已经在录像", req.getStream());
            return;
        }
        // 如果没有录像，那么就去开启录像
        JSONObject startRecord = HttpUtil.getMediaKitRequest(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/startRecord", param);
        if (startRecord.getInteger("code") != 0) {
            throw new ServiceException("请求mediakit接口 /index/api/startRecord 失败: " + startRecord.toString());
        }
        log.info("设备 {} 开启录像成功", req.getStream());
    }

    @Override
    public void openRecord(RecordSwitchReq req) {
        BagDO bagDO = bagMapper.getBagBySN(req.getSn());
        if (bagDO == null) {
            throw new ServiceException("设备 " + req.getSn() + " 不存在");
        }
        bagDO.setRecordSwitch(req.getRecordSwitch());
        bagMapper.updateBag(bagDO);
        MediaCurrentInfoDO mediaCurrentInfoDO = mediaCurInfoDao.selectBySn(req.getSn());
        if (mediaCurrentInfoDO == null) {
            throw new ServiceException("设备 " + req.getSn() + " 状态没有上报");
        }
        // 如果正在直播，那么我就去开启录像
        if (mediaCurrentInfoDO.getStaLiveState().equals(LiveState.LIVE.getCode())) {
            StreamChangedReq streamChangedReq = new StreamChangedReq();
            streamChangedReq.setApp("live");
            streamChangedReq.setRegist(true);
            streamChangedReq.setSchema("rtsp");
            streamChangedReq.setStream(req.getSn());
            startMediakitRecord(streamChangedReq);
        }
        log.info("device:{} open record success", req.getSn());
    }

    /**
     * 修改数据库中的录像开关
     * 调用关闭录像接口，如果直播状态未关，调用此接口会报错，说找不到流，我们不处理这个异常
     *
     * @param req
     */
    @Override
    public void closeRecord(RecordSwitchReq req) {
        BagDO bagDO = bagMapper.getBagBySN(req.getSn());
        bagDO.setRecordSwitch(req.getRecordSwitch());
        bagMapper.updateBag(bagDO);
        closeMediakitRecord(req.getSn());
    }

    private void closeMediakitRecord(String sn) {
        HashMap<String, String> param = new HashMap<>();
        // 现在通过判断是否有rtmp的流，来判断是否有返送流
        param.put("vhost", "__defaultVhost__");
        param.put("app", "live");
        param.put("stream", sn);
        param.put("type", String.valueOf(RecordType.MP4.getCode()));
        HttpUtil.getMediaKitRequest(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/stopRecord", param);
    }

    /**
     * 1. 获取截图，存到服务器上，路径规律：/snap/设备sn/年-月-日/录像文件名.jpeg
     * 2. 获取截图存储路径，和录像文件相关信息，整合起来存到数据库。
     * <p>
     * 问题：当关闭录像时，会发送两次/index/hook/on_record_mp4 ，两次的请求体一模一样
     *
     * @param req
     * @return
     */
    @SneakyThrows
    @Override
    public void onRecordMp4(OnRecordMp4Req req) {
        // 先查询数据库中是否已经有此录像了，如果有了，则直接return
        RecordDO selectByFilePath = recordDao.selectByFilePath(req.getFilePath());
        if (selectByFilePath != null) {
            log.info("record {} already exist", req.getFilePath());
            return;
        }
        // 获取截图
        HashMap<String, String> param = new HashMap<>();
        param.put("url", req.getFilePath());
        param.put("timeout_sec", "10");
        param.put("expire_sec", "1");
        Stopwatch started = Stopwatch.createStarted();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/getSnap")).newBuilder();
        param.forEach(urlBuilder::addQueryParameter);
        urlBuilder.addQueryParameter("secret", HttpUtil.MEDIAKIT_SECRET);
        String urlWithParam = urlBuilder.build().toString();

        OkHttpClient httpClient = HttpUtil.getHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(urlWithParam)
                .build();

        // 录像截图路径
        String snapUrlPath = getSnapPath(req.getUrl());
        String snapAbsolutePath = getSnapAbsolutePath(req.getFilePath(), req.getUrl(), snapUrlPath);
        createSnapFolder(snapAbsolutePath, req);
        try (Response response = httpClient.newCall(request).execute()) {
            InputStream inputStream = response.body().byteStream();
            BufferedOutputStream outputStream = FileUtil.getOutputStream(new File(snapAbsolutePath));

            IoUtil.copy(inputStream, outputStream);
        }
        log.info("get snap {} cost: {} ", req.getFilePath(), started.elapsed(TimeUnit.MILLISECONDS));
        // 整合起来存到数据库
        RecordDO recordDO = new RecordDO();
        recordDO.setFileAlias(req.getFileName());
        recordDO.setFileName(req.getFileName());
        recordDO.setFilePath(req.getFilePath());
        recordDO.setFileSize(req.getFileSize());
        recordDO.setFolder(req.getFolder());
        recordDO.setMediaServerId(req.getMediaServerId());
        recordDO.setStartTime(new Date(req.getStartTime() * 1000));
        recordDO.setStream(req.getStream());
        recordDO.setApp(req.getApp());
        recordDO.setUrl(req.getUrl());
        recordDO.setVhost(req.getVhost());
        recordDO.setSnapPath(snapUrlPath);
        recordDao.save(recordDO);
    }

    /**
     * snapPath = snap/C10821A0212/2023-10-24/11-26-52-0.jpeg
     * 我们需要先创建文件夹 /opt/java-server/mediakit/www/snap/C10821A0212/2023-10-24/
     *
     * @param snapAbsolutePath
     * @param req
     */
    private void createSnapFolder(String snapAbsolutePath, OnRecordMp4Req req) {
        String snapFolder = new File(snapAbsolutePath).getParent();
        FileUtil.mkdir(snapFolder);
        log.info("create snap folder: {}", snapFolder);
    }

    /**
     * fileUrl = record/live/C10821A0212/2023-10-24/11-26-52-0.mp4
     * result = snap/C10821A0212/2023-10-24/11-26-52-0.jpeg
     *
     * @param fileUrl
     */
    private String getSnapPath(String fileUrl) {
        String[] split = StringUtils.split(fileUrl, "/");
        split[0] = "snap";
        split[split.length - 1] = split[split.length - 1].replace("mp4", "jpeg");
        return StringUtils.join(split, "/");
    }

    @Override
    public List<RecordVO> list(String sn, String name, Long startTime, Long endTime) {
        List<RecordDO> recordDOS = recordDao.list(sn, name,startTime, endTime);
        return convertToVO(recordDOS);
    }

    private List<RecordVO> convertToVO(List<RecordDO> recordDOS) {
        ArrayList<RecordVO> vos = new ArrayList<>(recordDOS.size());
        for (RecordDO recordDO : recordDOS) {
            RecordVO recordVO = new RecordVO();
            recordVO.setId(recordDO.getId());
            recordVO.setName(getRecordDate(recordDO.getUrl())+"_"+recordDO.getFileAlias());
            recordVO.setSize(recordDO.getFileSize());
            recordVO.setOrigin(recordDO.getStream());
            recordVO.setUrl(recordDO.getUrl());
            recordVO.setImg(recordDO.getSnapPath());
            recordVO.setDate(recordDO.getStartTime().getTime());
            vos.add(recordVO);
        }
        return vos;
    }

    /**
     * url = record/live/C10821A0212/2023-10-24/11-26-52-0.mp4
     * @param url
     * @return
     */
    private String getRecordDate(String url) {
        String[] split = StringUtils.split(url, "/");
        return split[split.length - 2];
    }

    @Override
    public void delete(List<Integer> ids) {
        for (Integer id : ids) {
            RecordDO recordDO = recordDao.getById(id);
            if (recordDO == null) {
                throw new ServiceException("record not exist");
            }
            // /opt/java-server/mediakit/www/snap/C10821A0212/2023-10-24/11-26-52-0.jpeg
            String snapAbsolutePath = getSnapAbsolutePath(recordDO.getFilePath(), recordDO.getUrl(), recordDO.getSnapPath());
            // delete snap file
            FileUtil.del(snapAbsolutePath);
            // delete record file
            FileUtil.del(recordDO.getFilePath());

            recordDao.removeById(id);
        }

    }

    /**
     * filePath /opt/java-server/mediakit/www/record/live/C10821A0212/2023-10-24/11-26-52-0.mp4
     * fileUrl record/live/C10821A0212/2023-10-24/11-26-52-0.mp4
     * snapPath snap/C10821A0212/2023-10-24/11-26-52-0.jpeg
     *
     * @param filePath
     * @param fileUrl
     * @param snapPath
     * @return
     */
    private static String getSnapAbsolutePath(String filePath, String fileUrl, String snapPath) {
        return filePath.replace(fileUrl, snapPath);
    }

    @Override
    public long countRecord() {
        return recordDao.count();
    }

    @Override
    public void update(UpdateRecordReq req) {
        RecordDO recordDO = recordDao.getById(req.getId());
        if (recordDO == null) {
            throw new ServiceException("record not exist");
        }
        recordDO.setFileAlias(req.getName());
        recordDao.updateById(recordDO);
    }
}
