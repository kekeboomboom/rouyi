package com.cogent.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cogent.common.core.redis.RedisCache;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.common.CallGroupCommon;
import com.cogent.system.common.RedisConstant;
import com.cogent.system.dao.CallGroupDao;
import com.cogent.system.dao.CallGroupMemberDao;
import com.cogent.system.domain.DO.callGroup.CallGroupDO;
import com.cogent.system.domain.DO.callGroup.CallGroupMemberDO;
import com.cogent.system.domain.DO.callGroup.MemberStatus;
import com.cogent.system.domain.vo.callGroup.*;
import com.cogent.system.service.ICallGroupPaasService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/6/30
 * {@code @description:}
 */
@Service
@Slf4j
public class CallGroupPaasServiceImpl implements ICallGroupPaasService {

    @Resource
    private CallGroupDao callGroupDao;
    @Resource
    private CallGroupMemberDao callGroupMemberDao;
    @Resource
    private RedisCache redisCache;
    @Resource
    private CallGroupCommon callGroupCommon;

    // key 为 groupNumber，value为此群组相关的所有sse，当群组中的web和设备有所改动后，都推送给此群组下的sse
    private final ConcurrentHashMap<Integer, HashSet<SseEmitter>> SSEEmitterGroupInfoMap = new ConcurrentHashMap<>();

    @Override
    public List<JoinedGroupVO> getJoinedGroup(String memberNumber) {
        // 获得此成员加入的群组的编号
        List<CallGroupMemberDO> joinedGroup = callGroupMemberDao.selectJoinedGroup(memberNumber);
        if (CollectionUtils.isEmpty(joinedGroup)) {
            return Collections.emptyList();
        }
        // 根据编号列表查询，群组更详细的信息
        List<CallGroupDO> groupInfoList = callGroupDao.selectByGroupNumberList(joinedGroup.stream().map(CallGroupMemberDO::getCallGroupNumber).collect(Collectors.toList()));
        return groupInfoList.stream().map(callGroupCommon::convertToJoinedGroupVO).collect(Collectors.toList());
    }

    @Override
    public PaasGroupInfoVO getPaasGroupInfo(Integer groupNumber) {
        // 查询群组信息
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        // 查询群组成员信息
        List<CallGroupMemberDO> callGroupMemberDOS = callGroupMemberDao.selectByGroupNumber(groupNumber);
        return callGroupCommon.convertToPaasGroupInfoVO(callGroupDO, callGroupMemberDOS);
    }

    public GroupInfoVO getGroupInfo(Integer groupNumber) {
        // 查询群组信息
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        // 查询群组成员信息
        List<CallGroupMemberDO> callGroupMemberDOS = callGroupMemberDao.selectByGroupNumber(groupNumber);
        return callGroupCommon.convertToGroupInfoVO(callGroupDO, callGroupMemberDOS);
    }

    @Override
    public long getJoinedGroupCount(String memberNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getMemberNumber, memberNumber);
        return callGroupMemberDao.count(query);
    }

    @Override
    public void enterGroup(EnterGroupPaasReq req) {
        Integer groupNumber = req.getCallGroupNumber();
        String memberNumber = req.getMemberNumber();
        //check request
        CallGroupMemberDO memberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        if (ObjectUtils.isEmpty(memberDO)) {
            throw new ServiceException("This CallGroup does not have this member");
        }
        // qt 那边，退出群组是直接退出的，不会管请求是否成功，因为他觉得用户退出群组不应该转圈等待。
        // 因此可能会设备进入了群组A，然后他退出了群组A，但是退出的请求没发给我，然后他有进了群组A或者群组B，
        // 因此我允许设备可以重复进入同一个群组，他重复进，想当于没退出，那就什么也不需要做
        // 如果他进入了其他群组，那么我需要让他先退出群组A，然后再让他进入群组B
        CallGroupMemberDO enteredGroup = callGroupMemberDao.selectMemberEnteredGroup(memberNumber);
        if (ObjectUtils.isNotEmpty(enteredGroup)) {
            // 如果相等，则说明重复进同一个，什么也不做即可
            if (Objects.equals(enteredGroup.getCallGroupNumber(), groupNumber)) {
                return;
            } else {
                // 不相等，则需要先退出原来的，在进入当前的
                ExitGroupPaasReq exitGroupPaasReq = new ExitGroupPaasReq();
                exitGroupPaasReq.setGroupNumber(enteredGroup.getCallGroupNumber());
                exitGroupPaasReq.setMemberNumber(memberNumber);
                exitGroup(exitGroupPaasReq);
            }
        }

        String memberName = memberDO.getMemberName();

        String streamId = callGroupCommon.generateStreamId();
        redisCache.setCacheSet(RedisConstant.STREAM_ID_CALL_GROUP_SET.getValue(), Collections.singleton(streamId));

        CallGroupMemberDO callGroupMemberDO = new CallGroupMemberDO();
        callGroupMemberDO.setCallGroupNumber(groupNumber);
        callGroupMemberDO.setMemberNumber(memberNumber);
        callGroupMemberDO.setMemberStreamId(streamId);
        callGroupMemberDO.setMemberStatus(MemberStatus.onlineIn);
        callGroupMemberDao.updateCallGroupMember(callGroupMemberDO);

        // report web
        HashSet<SseEmitter> emitterSet = SSEEmitterGroupInfoMap.get(groupNumber);
        if (CollectionUtils.isNotEmpty(emitterSet)) {
            for (SseEmitter sseEmitter : emitterSet) {
                GroupInfoVO groupInfo = getGroupInfo(groupNumber);
                JSONObject SSEData = new JSONObject();
                SSEData.put("code", 200);
                SSEData.put("msg", "group info");
                SSEData.put("data", groupInfo);
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(SSEData.toString())
                        .id(String.valueOf(groupNumber))
                        .name("groupInfo");
                try {
                    log.info("SSE Event id{} name{} data{}", groupNumber, "groupInfo", SSEData);
                    sseEmitter.send(event);
                } catch (Exception e) {
                    log.error("Error sending joinedGroup", e);
                }
            }
        }

        // report device
        try {
            List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
//            List<CallGroupMemberDO> filteredSelfList = groupOnlineInDeviceMember.stream().filter(groupOnlineIn -> !StringUtils.equals(groupOnlineIn.getMemberNumber(), memberNumber)).collect(Collectors.toList());
            callGroupCommon.reportDeviceMemberEnter(groupNumber, memberNumber, memberName, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("enter group report error", e);
        }
    }

    @Override
    public boolean applyForSpeak(ApplySpeakReq req) {
        Integer groupNumber = req.getGroupNumber();
        String memberNumber = req.getMemberNumber();
        // check request
        CallGroupMemberDO memberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        if (ObjectUtils.isEmpty(memberDO)) {
            throw new ServiceException("This CallGroup does not have this member");
        }

        CallGroupMemberDO applier = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        String speaker = callGroupDO.getCallGroupSpeaker();

        if (StringUtils.isNotEmpty(speaker)) {
            CallGroupMemberDO speakerDO = callGroupMemberDao.selectMemberByName(speaker, groupNumber);
            if (speakerDO.getMemberLevel() >= applier.getMemberLevel()) {
                return false;
            }
        }
        callGroupDO.setCallGroupSpeaker(applier.getMemberName());
        callGroupDO.setCallGroupSpeakerStartTime(new Date());
        callGroupDao.updateByGroupNumber(callGroupDO);


        HashSet<SseEmitter> emitterSet = SSEEmitterGroupInfoMap.get(groupNumber);
        if (CollectionUtils.isNotEmpty(emitterSet)) {
            for (SseEmitter sseEmitter : emitterSet) {
                GroupInfoVO groupInfo = getGroupInfo(groupNumber);
                JSONObject SSEData = new JSONObject();
                SSEData.put("code", 200);
                SSEData.put("msg", "group info");
                SSEData.put("data", groupInfo);
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(SSEData.toString())
                        .id(String.valueOf(groupNumber))
                        .name("groupInfo");
                try {
                    log.info("SSE Event id{} name{} data{}", groupNumber, "groupInfo", SSEData);
                    sseEmitter.send(event);
                } catch (Exception e) {
                    log.error("Error sending joinedGroup", e);
                }
            }
        }


        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
        try {
            callGroupCommon.reportDeviceApplyForSpeak(callGroupDO, applier, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("apply speaker failed", e);
        }
        return true;
    }

    @Override
    public void endTheSpeak(EndTheSpeakReq req) {
        Integer groupNumber = req.getCallGroupNumber();
        String memberNumber = req.getCallGroupSpeaker();

        CallGroupMemberDO callGroupMemberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        CallGroupDO groupDO = callGroupDao.selectByGroupNumber(groupNumber);
        if (ObjectUtils.isEmpty(groupDO)) {
            throw new ServiceException("Cannot find this call group");
        }
        if (ObjectUtils.isEmpty(callGroupMemberDO)) {
            throw new ServiceException("This CallGroup does not have this member");
        }
        if (!StringUtils.equals(groupDO.getCallGroupSpeaker(), callGroupMemberDO.getMemberName())) {
            throw new ServiceException("Speaker name does not match");
        }

        CallGroupDO callGroupDO = new CallGroupDO();
        callGroupDO.setCallGroupNumber(groupNumber);
        callGroupDO.setCallGroupSpeaker("");
        callGroupDO.setCallGroupSpeakerStartTime(null);
        callGroupDao.updateByGroupNumber(callGroupDO);

        HashSet<SseEmitter> emitterSet = SSEEmitterGroupInfoMap.get(groupNumber);
        if (CollectionUtils.isNotEmpty(emitterSet)) {
            for (SseEmitter sseEmitter : emitterSet) {
                GroupInfoVO groupInfo = getGroupInfo(groupNumber);
                JSONObject SSEData = new JSONObject();
                SSEData.put("code", 200);
                SSEData.put("msg", "group info");
                SSEData.put("data", groupInfo);
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(SSEData.toString())
                        .id(String.valueOf(groupNumber))
                        .name("groupInfo");
                try {
                    log.info("SSE Event id{} name{} data{}", groupNumber, "groupInfo", SSEData);
                    sseEmitter.send(event);
                } catch (Exception e) {
                    log.error("Error sending joinedGroup", e);
                }
            }
        }


        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
        try {
            callGroupCommon.reportDeviceEndSpeak(groupNumber, memberNumber, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("end speak report error", e);
        }
    }

    @Override
    public void exitGroup(ExitGroupPaasReq req) {
        Integer groupNumber = req.getGroupNumber();
        String memberNumber = req.getMemberNumber();
        // check request
        CallGroupMemberDO groupMemberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        if (ObjectUtils.isEmpty(groupMemberDO)) {
            throw new ServiceException("This group cannot find this member");
        }

        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        String speaker = callGroupDO.getCallGroupSpeaker();
        if (StringUtils.isNotEmpty(speaker) && speaker.equals(groupMemberDO.getMemberName())) {
            EndTheSpeakReq endSpeakReq = new EndTheSpeakReq();
            endSpeakReq.setCallGroupNumber(groupNumber);
            endSpeakReq.setCallGroupSpeaker(memberNumber);
            endTheSpeak(endSpeakReq);
        }

        CallGroupMemberDO callGroupMemberDO = new CallGroupMemberDO();
        callGroupMemberDO.setCallGroupNumber(groupNumber);
        callGroupMemberDO.setMemberNumber(memberNumber);
        callGroupMemberDO.setMemberStatus(MemberStatus.onlineNotIn);
        callGroupMemberDO.setMemberStreamId("");
        callGroupMemberDao.updateByGroupNumberAndMemberNumber(callGroupMemberDO);

        redisCache.deleteSetValue(RedisConstant.STREAM_ID_CALL_GROUP_SET.getValue(), callGroupMemberDO.getMemberStreamId());
        HashSet<SseEmitter> emitterSet = SSEEmitterGroupInfoMap.get(groupNumber);
        if (CollectionUtils.isNotEmpty(emitterSet)) {
            for (SseEmitter sseEmitter : emitterSet) {
                GroupInfoVO groupInfo = getGroupInfo(groupNumber);
                JSONObject SSEData = new JSONObject();
                SSEData.put("code", 200);
                SSEData.put("msg", "group info");
                SSEData.put("data", groupInfo);
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .data(SSEData.toString())
                        .id(String.valueOf(groupNumber))
                        .name("groupInfo");
                try {
                    log.info("SSE Event id{} name{} data{}", groupNumber, "groupInfo", SSEData);
                    sseEmitter.send(event);
                } catch (Exception e) {
                    log.error("Error sending joinedGroup", e);
                }
            }
        }

        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
        try {
            callGroupCommon.reportDeviceExitGroup(groupNumber, memberNumber, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("end speak report error", e);
        }
    }

    /**
     * 这边的sse，是当设备有任何动作时，可以通知web用户
     *
     * @param groupNumber
     * @param emitter
     */
    @Override
    public void putSseInGroupInfoMap(Integer groupNumber, SseEmitter emitter) {
        HashSet<SseEmitter> set = SSEEmitterGroupInfoMap.get(groupNumber);
        if (ObjectUtils.isEmpty(set)) {
            HashSet<SseEmitter> emitterHashSet = new HashSet<>();
            emitterHashSet.add(emitter);
            SSEEmitterGroupInfoMap.put(groupNumber, emitterHashSet);

            emitter.onCompletion(() -> {
                log.info("sse onCompletion: id:{} ", groupNumber);
                emitterHashSet.remove(emitter);
            });
            emitter.onTimeout(() -> {
                log.info("sse onTimeout: id:{} ", groupNumber);
                emitterHashSet.remove(emitter);
            });
            emitter.onError((throwable) -> {
                log.error("sse onError: id:{} ", groupNumber);
                emitterHashSet.remove(emitter);
            });
        } else {
            set.add(emitter);
            emitter.onCompletion(() -> {
                log.info("sse onCompletion: id:{} ", groupNumber);
                set.remove(emitter);
            });
            emitter.onTimeout(() -> {
                log.info("sse onTimeout: id:{} ", groupNumber);
                set.remove(emitter);
            });
            emitter.onError((throwable) -> {
                log.error("sse onError: id:{} ", groupNumber, throwable);
                set.remove(emitter);
            });
        }
    }
}