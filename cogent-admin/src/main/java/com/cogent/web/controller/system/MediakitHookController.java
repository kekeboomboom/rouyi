package com.cogent.web.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.annotation.Anonymous;
import com.cogent.common.core.controller.BaseController;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.domain.vo.mediaHook.NoReadReq;
import com.cogent.system.domain.vo.mediaHook.OnRecordMp4Req;
import com.cogent.system.domain.vo.mediaHook.StreamChangedReq;
import com.cogent.system.service.IRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author keboom
 * @Date 2023-06-25 9:23
 */
@Slf4j
@Anonymous
@RestController
@Validated
@RequestMapping("")
public class MediakitHookController extends BaseController {

    @Resource
    private IRecordService recordService;

    /**
     * mediakit会在无人观看时有一个hook请求。通过这个我可以关闭无人观看的预览。
     * <a href="https://github.com/ZLMediaKit/ZLMediaKit/wiki/ZLMediakit%E7%8B%AC%E5%AE%B6%E7%89%B9%E6%80%A7%E4%BB%8B%E7%BB%8D#2%E6%97%A0%E4%BA%BA%E8%A7%82%E7%9C%8B%E4%BA%8B%E4%BB%B6">...</a>
     *
     * @param noReadReq
     * @return
     */
    @PostMapping("/index/hook/on_stream_none_reader")
    public JSONObject noReader(@RequestBody @Valid NoReadReq noReadReq) {
        JSONObject res = new JSONObject();
        res.put("code", 0);
        if (StringUtils.equals(noReadReq.getApp(), "transcoding") && StringUtils.equals(noReadReq.getSchema(), "rtsp")) {

            res.put("close", true);
        } else {
            res.put("close", false);
        }
        return res;
    }

    /**
     * 一条流打到mediakit，会转出5条流。因此会被调用多次
     *
     * @param req
     * @return
     */
    @PostMapping("/index/hook/on_stream_changed")
    public JSONObject onStreamChanged(@RequestBody @Valid StreamChangedReq req) {
        // 判断此设备是否开启录像，如果开启录像，判断mediakit是否已经录像，如果已经录像，那么我们就不再去开了，如果没有那么就开启。
        // 有多个流，这里我们就值判断一个rtsp的流就可以了
        if (req.getRegist() && req.getSchema().equals("rtsp") && req.getApp().equals("live")) {
            recordService.startRecord(req);
        }
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("msg", "success");
        return resp;
    }

    /**
     * 当此次录像结束后，会调用此接口，我们此时记录录像文件的信息。并获取截图，存到服务器上。
     * {
     * "app": "live",
     * "file_name": "11-26-52-0.mp4",
     * "file_path": "/opt/java-server/mediakit/www/record/live/C10821A0212/2023-10-24/11-26-52-0.mp4",
     * "file_size": 14832817,
     * "folder": "/opt/java-server/mediakit/www/record/live/C10821A0212/",
     * "mediaServerId": "your_server_id",
     * "start_time": 1698118012,
     * "stream": "C10821A0212",
     * "time_len": 15,
     * "url": "record/live/C10821A0212/2023-10-24/11-26-52-0.mp4",
     * "vhost": "__defaultVhost__"
     * }
     * @param req
     * @return
     */
    @PostMapping("/index/hook/on_record_mp4")
    public JSONObject onRecordMp4(@RequestBody @Valid OnRecordMp4Req req) {
        recordService.onRecordMp4(req);
        JSONObject resp = new JSONObject();
        resp.put("code", 0);
        resp.put("msg", "success");
        return resp;
    }
}
