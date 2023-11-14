package com.cogent.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.core.redis.RedisCache;
import com.cogent.common.utils.HttpUtil;
import com.cogent.common.utils.bean.BeanUtils;
import com.cogent.system.common.RedisConstant;
import com.cogent.system.dao.FoldbackSourceDao;
import com.cogent.system.domain.DO.foldback.FoldbackSourceDO;
import com.cogent.system.domain.vo.foldback.FoldbackSourceVO;
import com.cogent.system.domain.vo.foldback.FoldbackStreamInfoVO;
import com.cogent.system.service.IFoldbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/20
 * {@code @description:}
 */
@Slf4j
@Service
public class FoldbackServiceImpl implements IFoldbackService {

    @Resource
    private FoldbackSourceDao foldbackSourceDao;

    @Resource
    private RedisCache redisCache;

    @Override
    public void createSource(String name, Integer delay) {
        FoldbackSourceDO aDo = new FoldbackSourceDO();
        aDo.setApp("feedback");
        aDo.setName(name);
        aDo.setDelay(delay);
        String streamId = generateStreamId();
        redisCache.setCacheSet(RedisConstant.STREAM_ID_FOLDBACK_SET.getValue(), Collections.singleton(streamId));
        aDo.setStreamId(streamId);
        foldbackSourceDao.save(aDo);
    }

    @Override
    public void updateSource(Integer id, String name, Integer delay) {
        FoldbackSourceDO aDo = new FoldbackSourceDO();
        aDo.setId(id);
        aDo.setName(name);
        aDo.setDelay(delay);
        foldbackSourceDao.updateById(aDo);
    }

    @Override
    public void deleteSource(Integer id) {
        FoldbackSourceDO sourceDO = foldbackSourceDao.getById(id);
        redisCache.deleteSetValue(RedisConstant.STREAM_ID_FOLDBACK_SET.getValue(), sourceDO.getStreamId());
        foldbackSourceDao.removeById(id);
    }

    @Override
    public List<FoldbackSourceVO> getSourceList() {
        List<FoldbackSourceDO> foldbackSourceDOS = foldbackSourceDao.list();
        ArrayList<FoldbackSourceVO> result = new ArrayList<>(foldbackSourceDOS.size());
        for (FoldbackSourceDO aDo : foldbackSourceDOS) {
            FoldbackSourceVO vo = new FoldbackSourceVO();
            BeanUtils.copyProperties(aDo, vo);
            HashMap<String, String> param = new HashMap<>();
            // 现在通过判断是否有rtmp的流，来判断是否有返送流
            param.put("schema", "rtmp");
            param.put("vhost", "__defaultVhost__");
            param.put("app", "feedback");
            param.put("stream", aDo.getStreamId());
            JSONObject mediaResp = HttpUtil.getMediaKitRequest(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/getMediaInfo", param);
            vo.setState(mediaResp.getInteger("code") == 0);
            result.add(vo);
        }
        return result;
    }

    @Override
    public FoldbackSourceDO getSource(Integer id) {
        return foldbackSourceDao.getById(id);
    }

    /**
     * track结构：
     * "tracks": [
     * {
     * "channels": 2,
     * "codec_id": 2,
     * "codec_id_name": "mpeg4-generic",
     * "codec_type": 1,
     * "frames": 71072,
     * "loss": -1.0,
     * "ready": true,
     * "sample_bit": 16,
     * "sample_rate": 48000
     * },
     * {
     * "codec_id": 0,
     * "codec_id_name": "H264",
     * "codec_type": 0,
     * "fps": 50.0,
     * "frames": 37900,
     * "gop_interval_ms": 1222,
     * "gop_size": 60,
     * "height": 1080,
     * "key_frames": 632,
     * "loss": -1.0,
     * "ready": true,
     * "width": 1920
     * }
     * ],
     * <p>
     * 根据codec_type判断是音频还是视频。codec_type为1是音频，codec_type为0是视频
     *
     * @param streamId
     * @return
     */
    @Override
    public FoldbackStreamInfoVO getSourceStreamInfo(String streamId) {
        // 查询mediakit中，这个流的信息
        HashMap<String, String> param = new HashMap<>();
        param.put("schema", "rtsp");
        param.put("vhost", "__defaultVhost__");
        param.put("app", "feedback");
        param.put("stream", streamId);
        JSONObject mediaResp = HttpUtil.getMediaKitRequest(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/getMediaInfo", param);
        // 根据track获得详情
        FoldbackStreamInfoVO vo = new FoldbackStreamInfoVO();
        JSONArray tracks = mediaResp.getJSONArray("tracks");
        for (int i = 0; i < tracks.size(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            Integer codecType = track.getInteger("codec_type");
            if (codecType == 0) {
                // 为0是视频
                FoldbackStreamInfoVO.Video video = new FoldbackStreamInfoVO.Video();
                video.setLoss(track.getInteger("loss"));
                video.setFps(track.getFloat("fps"));
                video.setGop(track.getInteger("gop_interval_ms"));
                video.setEncode(track.getString("codec_id_name"));
                video.setResolution(track.getInteger("width") + "x" + track.getInteger("height"));
                vo.setVideo(video);
            }
            if (codecType == 1) {
                FoldbackStreamInfoVO.Audio audio = new FoldbackStreamInfoVO.Audio();
                audio.setSampleRate(track.getInteger("sample_rate"));
                audio.setSampleBit(track.getInteger("sample_bit"));
                audio.setEncode(track.getString("codec_id_name"));
                vo.setAudio(audio);
            }
        }
        return vo;
    }

    /**
     * 生成全数字的streamId
     *
     * @return
     */
    public String generateStreamId() {
        String streamId = RandomStringUtils.randomNumeric(8);
        while (checkStreamIdExists(streamId)) {
            streamId = RandomStringUtils.randomNumeric(8);
        }
        return streamId;
    }

    private boolean checkStreamIdExists(String streamId) {
        return redisCache.checkSetHasValue(RedisConstant.STREAM_ID_CALL_GROUP_SET.getValue(), streamId);
    }
}
