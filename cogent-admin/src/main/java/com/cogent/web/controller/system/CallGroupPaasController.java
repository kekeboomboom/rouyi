package com.cogent.web.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.annotation.Anonymous;
import com.cogent.common.core.controller.BaseController;
import com.cogent.common.core.domain.AjaxResult;
import com.cogent.common.core.page.TableDataInfo;
import com.cogent.system.domain.vo.callGroup.*;
import com.cogent.system.service.ICallGroupPaasService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * {@code @Author} keboom
 * {@code @Date} 2023-06-27 17:19
 * 这些解耦都是给paas去调用的
 */
@Validated
@Anonymous
@RestController
@RequestMapping("/callGroup")
public class CallGroupPaasController extends BaseController {

    @Resource
    private ICallGroupPaasService callGroupPaasService;

    @GetMapping("/joined")
    public TableDataInfo getCallGroupJoined(@RequestParam String memberNumber) {
        long count = callGroupPaasService.getJoinedGroupCount(memberNumber);
        startPage();
        List<JoinedGroupVO> joinedGroup = callGroupPaasService.getJoinedGroup(memberNumber);
        return new TableDataInfo(joinedGroup, (int) count);
    }

    @GetMapping("/info")
    public AjaxResult getCallGroupInfo(@RequestParam Integer groupNumber) {
        PaasGroupInfoVO groupInfo = callGroupPaasService.getPaasGroupInfo(groupNumber);
        return success(groupInfo);
    }

    @PostMapping("/enter")
    public AjaxResult enterGroup(@RequestBody @Valid EnterGroupPaasReq req) {
        callGroupPaasService.enterGroup(req);
        return success();
    }

    @PostMapping("/applySpeak")
    public AjaxResult applySpeak(@RequestBody @Valid ApplySpeakReq req) {
        boolean res = callGroupPaasService.applyForSpeak(req);
        JSONObject data = new JSONObject();
        data.put("applyResult", res);
        return success(data);
    }

    @PostMapping("/endTheSpeak")
    public AjaxResult endTheSpeak(@RequestBody @Valid EndTheSpeakReq req) {
        callGroupPaasService.endTheSpeak(req);
        return success();
    }

    @PostMapping("/exit")
    public AjaxResult exitGroup(@RequestBody @Valid ExitGroupPaasReq req) {
        callGroupPaasService.exitGroup(req);
        return success();
    }
}