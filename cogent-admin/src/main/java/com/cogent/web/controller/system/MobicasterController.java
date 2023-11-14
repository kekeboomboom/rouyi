package com.cogent.web.controller.system;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.annotation.Anonymous;
import com.cogent.common.constant.HttpStatus;
import com.cogent.common.core.controller.BaseController;
import com.cogent.common.core.domain.AjaxResult;
import com.cogent.common.utils.HttpUtil;
import com.cogent.system.domain.vo.mobicaster.*;
import com.cogent.system.service.IMobicasterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/22
 * {@code @description:}
 */
@Slf4j
@Validated
@Anonymous
@RestController
@RequestMapping("/mobicaster")
public class MobicasterController extends BaseController {

    @Autowired
    private IMobicasterService mobicasterService;

    @PostMapping("/deviceInfo")
    public AjaxResult deviceInfo(@RequestBody @Valid DeviceInfoReq req) {
        mobicasterService.deviceInfo(req);
        return success();
    }

    @PostMapping("/live")
    public AjaxResult live(@RequestBody @Valid LiveReq req) {
        int port = mobicasterService.live(req);
        LiveRespVO liveRespVO = new LiveRespVO();
        if (port == -1) {
            liveRespVO.setLiveAction(false);
        } else {
            liveRespVO.setLiveAction(true);
            liveRespVO.setIp(HttpUtil.HOST_IP);
            liveRespVO.setPort(port);
        }
        return success(liveRespVO);
    }

    @PostMapping("/statusReport")
    public AjaxResult statusReport(@RequestBody AndroidStatusReq req) {
        log.info("statusReport: " + req.toString());
        mobicasterService.statusReport(req);
        return success();
    }

    @PostMapping("/offlineReport")
    public AjaxResult offlineReport(@RequestBody @Valid OfflineReportReq req) {
        mobicasterService.offlineReport(req);
        return success();
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        AppInfo deviceInfo = mobicasterService.getDeviceInfo(id);
        return success(deviceInfo);
    }

    @PutMapping("/settings")
    public AjaxResult updateApp(@RequestBody @Valid UpdateAppReq req) {
        mobicasterService.updateApp(req);
        return success();
    }

    @PostMapping("/appLive")
    public AjaxResult appLive(@RequestBody @Valid AppLiveReq req) {
        boolean liveState = mobicasterService.getLiveState(req.getAndroidID());
        if (req.getLive() && liveState) {
            return new AjaxResult(HttpStatus.ACCEPTED, "直播已开启");
        }
        mobicasterService.appLive(req);
        return success();
    }

    @PostMapping("/preview")
    public AjaxResult preview(@RequestBody @Valid PreviewReq req) {
        boolean liveState = mobicasterService.getLiveState(req.getAndroidID());
        if (liveState) {
            mobicasterService.preview(req);
            return success();
        } else {
            return error("直播未开启");
        }
    }
}
