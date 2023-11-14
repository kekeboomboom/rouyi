package com.cogent.system.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.core.redis.RedisCache;
import com.cogent.common.utils.HttpUtil;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.domain.DO.callGroup.CallGroupDO;
import com.cogent.system.domain.DO.callGroup.CallGroupMemberDO;
import com.cogent.system.domain.DO.callGroup.MemberStatus;
import com.cogent.system.domain.vo.callGroup.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/3
 * {@code @description:}
 */
@Component
public class CallGroupCommon {

    @Resource
    private RedisCache redisCache;

    public String generateStreamId() {
        String streamId = RandomStringUtils.randomAlphanumeric(8);
        while (checkStreamIdExists(streamId)) {
            streamId = RandomStringUtils.randomAlphanumeric(8);
        }
        return streamId;
    }

    private boolean checkStreamIdExists(String streamId) {
        return redisCache.checkSetHasValue(RedisConstant.STREAM_ID_CALL_GROUP_SET.getValue(), streamId);
    }

    public void reportDeviceMemberEnter(Integer groupNumber, String memberNumber, String memberName, List<CallGroupMemberDO> filteredSelfList) {
        List<String> snList = filteredSelfList.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList());
        JSONArray reportOtherMemberNumbers = new JSONArray();
        reportOtherMemberNumbers.addAll(snList);
        JSONObject reportReq = new JSONObject();
        reportReq.put("callGroupNumber", groupNumber);
        reportReq.put("memberNumber", memberNumber);
        reportReq.put("memberName", memberName);
        reportReq.put("reportOtherMemberNumbers", reportOtherMemberNumbers);
        HttpUtil.postGateWayRequest(HttpUtil.GATEWAY_BASE_URL + "/api/callGroup/enterReport", reportReq);
    }

    public void reportDeviceDeleteGroup(Integer groupNumber, List<String> snList) {
        JSONObject del = new JSONObject();
        JSONArray snListArray = new JSONArray();
        snListArray.addAll(snList);
        del.put("snList", snListArray);
        del.put("callGroupNumber", groupNumber);
        HttpUtil.postGateWayRequest(HttpUtil.GATEWAY_BASE_URL + "/api/callGroup/bagDevice/delete", del);
    }

    public void reportDeviceAddGroup(int callGroupNumber, String groupName, List<String> snList) {
        JSONObject addGroup = new JSONObject();
        JSONArray snListArray = new JSONArray();
        snListArray.addAll(snList);
        addGroup.put("snList", snListArray);
        addGroup.put("callGroupNumber", callGroupNumber);
        addGroup.put("callGroupName", groupName);
        // 现在默认创建者都是admin
        addGroup.put("callGroupCreater", "admin");
        HttpUtil.postGateWayRequest(HttpUtil.GATEWAY_BASE_URL + "/api/callGroup/bagDevice", addGroup);
    }

    public void reportDeviceExitGroup(Integer groupNumber, String memberNumber, List<CallGroupMemberDO> filteredSelfList) {
        JSONObject req = new JSONObject();
        req.put("callGroupNumber", groupNumber);
        req.put("exitMemberNumber", memberNumber);
        JSONArray reportOtherMemberNumbers = new JSONArray();
        reportOtherMemberNumbers.addAll(filteredSelfList.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList()));
        req.put("reportOtherMemberNumbers", reportOtherMemberNumbers);
        HttpUtil.postGateWayRequest(HttpUtil.GATEWAY_BASE_URL + "/api/callGroup/exitReport", req);
    }

    public void reportDeviceEndSpeak(Integer groupNumber, String memberNumber, List<CallGroupMemberDO> filteredSelfList) {
        JSONObject req = new JSONObject();
        req.put("callGroupNumber", groupNumber);
        req.put("callGroupSpeakerNumber", memberNumber);
        JSONArray reportMemberNumberList = new JSONArray();
        reportMemberNumberList.addAll(filteredSelfList.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList()));
        req.put("reportMemberNumberList", reportMemberNumberList);
        HttpUtil.postGateWayRequest(HttpUtil.GATEWAY_BASE_URL + "/api/callGroup/endTheSpeakReport", req);
    }

    public void reportDeviceApplyForSpeak(CallGroupDO callGroupDO, CallGroupMemberDO applier, List<CallGroupMemberDO> filteredSelfList) {
        JSONObject req = new JSONObject();
        req.put("callGroupNumber", callGroupDO.getCallGroupNumber());
        req.put("callGroupName", callGroupDO.getCallGroupName());
        req.put("callGroupSpeaker", applier.getMemberNumber());
        req.put("callGroupSpeakerName", callGroupDO.getCallGroupSpeaker());
        req.put("callGroupSpeakerStartTime", callGroupDO.getCallGroupSpeakerStartTime());
        req.put("applierStreamId", applier.getMemberStreamId());
        req.put("applierAppName", "call");
        JSONArray reportMemberNumberList = new JSONArray();
        reportMemberNumberList.addAll(filteredSelfList.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList()));
        req.put("reportMemberNumberList", reportMemberNumberList);
        HttpUtil.postGateWayRequest(HttpUtil.GATEWAY_BASE_URL + "/api/callGroup/applySpeakReport", req);
    }

    public void reportGroupInfo(PaasGroupInfoVO groupInfo) {
        HttpUtil.postGateWayRequest(HttpUtil.GATEWAY_BASE_URL + "/api/callGroup/groupInfoReport", JSONObject.from(groupInfo));
    }

    public GroupInfoVO convertToGroupInfoVO(CallGroupDO callGroupDO, List<CallGroupMemberDO> callGroupMemberDOS) {
        String speaker = callGroupDO.getCallGroupSpeaker();
        GroupInfoVO vo = new GroupInfoVO();
        vo.setGroupNumber(callGroupDO.getCallGroupNumber());
        vo.setGroupName(callGroupDO.getCallGroupName());
        vo.setGroupSpeaker(speaker);
        vo.setGroupSpeakerStartTime(callGroupDO.getCallGroupSpeakerStartTime());

        ArrayList<MemberInfoVO> members = new ArrayList<>();
        if (StringUtils.isNotEmpty(speaker)) {
            for (CallGroupMemberDO callGroupMemberDO : callGroupMemberDOS) {
                MemberInfoVO memberInfoVO = new MemberInfoVO();
                if (speaker.equals(callGroupMemberDO.getMemberName())) {
                    vo.setApp("call");
                    vo.setStreamId(callGroupMemberDO.getMemberStreamId());
                }
                memberInfoVO.setMemberNumber(callGroupMemberDO.getMemberNumber());
                memberInfoVO.setMemberName(callGroupMemberDO.getMemberName());
                memberInfoVO.setMemberStatus(callGroupMemberDO.getMemberStatus().name());
                memberInfoVO.setMemberType(callGroupMemberDO.getMemberType().name());
                members.add(memberInfoVO);
            }
        } else {
            for (CallGroupMemberDO callGroupMemberDO : callGroupMemberDOS) {
                MemberInfoVO memberInfoVO = new MemberInfoVO();
                memberInfoVO.setMemberNumber(callGroupMemberDO.getMemberNumber());
                memberInfoVO.setMemberName(callGroupMemberDO.getMemberName());
                memberInfoVO.setMemberStatus(callGroupMemberDO.getMemberStatus().name());
                memberInfoVO.setMemberType(callGroupMemberDO.getMemberType().name());
                members.add(memberInfoVO);
            }
        }
        vo.setMembers(members);
        return vo;
    }

    public PaasGroupInfoVO convertToPaasGroupInfoVO(CallGroupDO callGroupDO, List<CallGroupMemberDO> callGroupMemberDOS) {
        String speaker = callGroupDO.getCallGroupSpeaker();
        PaasGroupInfoVO vo = new PaasGroupInfoVO();
        vo.setGroupNumber(callGroupDO.getCallGroupNumber());
        vo.setGroupName(callGroupDO.getCallGroupName());
        vo.setGroupSpeaker(speaker);
        vo.setGroupSpeakerStartTime(callGroupDO.getCallGroupSpeakerStartTime());

        ArrayList<PaasMemberInfoVO> members = new ArrayList<>();
        if (StringUtils.isNotEmpty(speaker)) {
            for (CallGroupMemberDO callGroupMemberDO : callGroupMemberDOS) {
                PaasMemberInfoVO memberInfoVO = new PaasMemberInfoVO();
                if (speaker.equals(callGroupMemberDO.getMemberName())) {
                    vo.setApp("call");
                    vo.setStreamId(callGroupMemberDO.getMemberStreamId());
                }
                memberInfoVO.setMemberNumber(callGroupMemberDO.getMemberNumber());
                memberInfoVO.setMemberName(callGroupMemberDO.getMemberName());
                if (callGroupMemberDO.getMemberStatus().equals(MemberStatus.onlineIn)) {
                    memberInfoVO.setOnline(1);
                } else {
                    memberInfoVO.setOnline(0);
                }
                if (StringUtils.equals(speaker, callGroupMemberDO.getMemberName())) {
                    memberInfoVO.setSpeak(1);
                } else {
                    memberInfoVO.setSpeak(0);
                }
                members.add(memberInfoVO);
            }
        } else {
            for (CallGroupMemberDO callGroupMemberDO : callGroupMemberDOS) {
                PaasMemberInfoVO memberInfoVO = new PaasMemberInfoVO();
                memberInfoVO.setMemberNumber(callGroupMemberDO.getMemberNumber());
                memberInfoVO.setMemberName(callGroupMemberDO.getMemberName());
                if (callGroupMemberDO.getMemberStatus().equals(MemberStatus.onlineIn)) {
                    memberInfoVO.setOnline(1);
                } else {
                    memberInfoVO.setOnline(0);
                }
                if (StringUtils.equals(speaker, callGroupMemberDO.getMemberName())) {
                    memberInfoVO.setSpeak(1);
                } else {
                    memberInfoVO.setSpeak(0);
                }
                members.add(memberInfoVO);
            }
        }
        vo.setMembers(members);
        return vo;
    }

    public JoinedGroupVO convertToJoinedGroupVO(CallGroupDO callGroupDO) {
        JoinedGroupVO vo = new JoinedGroupVO();
        vo.setGroupCreater(callGroupDO.getCallGroupCreater());
        vo.setGroupName(callGroupDO.getCallGroupName());
        vo.setGroupNumber(callGroupDO.getCallGroupNumber());
        vo.setGroupSpeaker(callGroupDO.getCallGroupSpeaker());
        vo.setGroupSpeakerStartTime(callGroupDO.getCallGroupSpeakerStartTime());
        // 根据群组编号，查询群组成员数量
        return vo;
    }
}
