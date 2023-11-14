package com.cogent.web.controller.system;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.annotation.Anonymous;
import com.cogent.common.core.controller.BaseController;
import com.cogent.common.core.domain.AjaxResult;
import com.cogent.system.domain.vo.callGroup.*;
import com.cogent.system.service.ICallGroupPaasService;
import com.cogent.system.service.ICallGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * {@code @Author} keboom
 * {@code @Date} 2023-06-26 14:05
 * 这些接口是给web网页调用的
 */
@Validated
@Anonymous
@RestController
@RequestMapping("/voice-call/group")
public class CallGroupController extends BaseController {

    @Resource
    private ICallGroupService callGroupService;
    @Resource
    private ICallGroupPaasService callGroupPaasService;

    @PostMapping("")
    public AjaxResult callGroup(@RequestBody @Valid CreateCallGroupReq req) {
        callGroupService.createCallGroup(req);
        return success();
    }

    /**
     * 现在只支持设备成员增删
     *
     * @param req req
     * @return response
     */
    @PutMapping("")
    public AjaxResult updateCallGroup(@RequestBody @Valid UpdateCallGroupReq req) {
        callGroupService.updateCallGroup(req);
        return success();
    }

    @DeleteMapping("")
    public AjaxResult deleteCallGroup(@RequestBody @Valid DeleteCallGroupReq req) {
        if (invalidGroupNumber(req.getGroupNumber())) {
            return error("Invalid group number");
        }
        callGroupService.deleteCallGroup(req.getGroupNumber());
        return success();
    }

    /**
     * groupNumber groupName 都是前缀匹配
     *
     * @param memberNumber
     * @param groupNumber
     * @param groupName
     * @return
     */
    @GetMapping("/joined")
    public AjaxResult getJoinedGroup(@RequestParam String memberNumber,
                                     @RequestParam(required = false) Integer groupNumber,
                                     @RequestParam(required = false) String groupName) {
        List<JoinedGroupVO> joinedGroup = callGroupService.getJoinedGroup(memberNumber, groupNumber, groupName);
        return success(joinedGroup);
    }

    @GetMapping("/{groupNumber}")
    public AjaxResult getGroupInfo(@PathVariable("groupNumber") Integer groupNumber) {
        if (invalidGroupNumber(groupNumber)) {
            return error("Invalid group number");
        }
        return success(callGroupService.getGroupInfo(groupNumber));
    }

    @PostMapping("/enter")
    public AjaxResult enterGroup(@RequestBody @Valid EnterGroupReq req) {
        String streamId = callGroupService.enterGroup(req);
        JSONObject data = new JSONObject();
        data.put("streamId", streamId);
        data.put("app", "call");
        return success(data);
    }

    @PostMapping("/applyForSpeak")
    public AjaxResult applyForSpeak(@RequestBody @Valid ApplySpeakReq req) {
        boolean res = callGroupService.applyForSpeak(req);
        JSONObject data = new JSONObject();
        data.put("applyResult", res);
        return success(data);
    }

    @PostMapping("/endSpeak")
    public AjaxResult endSpeak(@RequestBody @Valid EndSpeakReq req) {
        callGroupService.endSpeak(req);
        return success();
    }

    @PostMapping("exit")
    public AjaxResult exitGroup(@RequestBody @Valid ExitGroupReq req) {
        callGroupService.exitGroup(req);
        return success();
    }

    private boolean invalidGroupNumber(Integer groupNumber) {
        return groupNumber == null || groupNumber < 10000 || groupNumber > 99999;
    }

    /**
     * @param memberNumber web 用户名
     * @return
     */
    @GetMapping("/sse/joinedGroup")
    public SseEmitter joinedGroup(@RequestParam String memberNumber) {
        SseEmitter emitter = new SseEmitter();
        // 对于群组的管理只有web端可以操作，对于设备不需要这个sse
        callGroupService.putSseInJoinedGroupMap(memberNumber, emitter);
        return emitter;
    }

    @GetMapping("/sse/groupInfo")
    public SseEmitter groupInfo(@RequestParam Integer groupNumber) {
        SseEmitter emitter = new SseEmitter();
        callGroupService.putSseInGroupInfoMap(groupNumber, emitter);
        callGroupPaasService.putSseInGroupInfoMap(groupNumber, emitter);
        return emitter;
    }

    @GetMapping("/memberList")
    public AjaxResult getMemberList() {
        List<MemberItemVO> memberList = callGroupService.getMemberList();
        return success(memberList);
    }

    @PostMapping("/interruptCall")
    public AjaxResult interruptCall(@RequestBody @Valid InterruptCallReq req) {
        callGroupService.interruptCall(req);
        return success();
    }

    @PostMapping("/specifySpeak")
    public AjaxResult specifySpeak(@RequestBody @Valid SpecifySpeakReq req) {
        callGroupService.specifySpeak(req);
        return success();
    }
}
