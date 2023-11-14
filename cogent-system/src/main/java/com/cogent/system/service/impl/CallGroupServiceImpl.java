package com.cogent.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cogent.common.core.domain.entity.SysUser;
import com.cogent.common.core.redis.RedisCache;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.HttpUtil;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.common.CallGroupCommon;
import com.cogent.system.common.RedisConstant;
import com.cogent.system.dao.CallGroupDao;
import com.cogent.system.dao.CallGroupMemberDao;
import com.cogent.system.domain.DO.bag.BagDO;
import com.cogent.system.domain.DO.callGroup.CallGroupDO;
import com.cogent.system.domain.DO.callGroup.CallGroupMemberDO;
import com.cogent.system.domain.DO.callGroup.MemberStatus;
import com.cogent.system.domain.DO.callGroup.MemberType;
import com.cogent.system.domain.vo.callGroup.*;
import com.cogent.system.mapper.BagMapper;
import com.cogent.system.mapper.SysUserMapper;
import com.cogent.system.service.ICallGroupService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * {@code @Author} keboom
 * {@code @Date} 2023-06-26 14:14
 */
@Slf4j
@Service
public class CallGroupServiceImpl implements ICallGroupService {

    @Resource
    private CallGroupDao callGroupDao;
    @Resource
    private CallGroupMemberDao callGroupMemberDao;
    @Resource
    private BagMapper bagMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private RedisCache redisCache;
    @Resource
    private CallGroupCommon callGroupCommon;
    // key为 memberNumber ，value是此成员的sse，因为web成员可以在不同浏览器上登录同一账号，所以同一web用户有多个sse
    private final ConcurrentHashMap<String, HashSet<SseEmitter>> SSEEmitterJoinedGroupMap = new ConcurrentHashMap<>();
    // key 为 groupNumber，value为此群组相关的所有sse，当群组中的web和设备有所改动后，都推送给此群组下的sse
    private final ConcurrentHashMap<Integer, HashSet<SseEmitter>> SSEEmitterGroupInfoMap = new ConcurrentHashMap<>();

    /**
     * 此方法用来维持sse心跳
     */
    @PostConstruct
    private void sseHeart() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        executorService.scheduleAtFixedRate(() -> {
            try {
                SSEEmitterJoinedGroupMap.forEach((memberNumber, SSESet) -> {
                    for (SseEmitter item : SSESet) {
                        JSONObject SSEData = new JSONObject();
                        SSEData.put("code", 200);
                        SSEData.put("msg", "Heartbeat");
                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                .data(SSEData.toString())
                                .id(memberNumber)
                                .name("heartBeat");
                        try {
                            log.info("{} joined group sse set size:{}", memberNumber, SSESet.size());
                            item.send(event);
                        } catch (Throwable throwable) {
                            item.complete();
                            log.error("Error sending heartbeat", throwable);
                        }
                    }
                });
            } catch (Throwable throwable) {
                log.error("Error sending heartbeat", throwable);
            }

        }, 1, 10, TimeUnit.SECONDS);


        ScheduledExecutorService executorService2 = Executors.newScheduledThreadPool(10);
        executorService2.scheduleAtFixedRate(() -> {
            try {
                SSEEmitterGroupInfoMap.forEach((groupMember, SSESet) -> {
                    for (SseEmitter item : SSESet) {
                        JSONObject SSEData = new JSONObject();
                        SSEData.put("code", 200);
                        SSEData.put("msg", "Heartbeat");
                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                .data(SSEData.toString())
                                .id(String.valueOf(groupMember))
                                .name("heartBeat");
                        try {
                            log.info("{} joined group sse set size:{}", groupMember, SSESet.size());
                            item.send(event);
                        } catch (Throwable throwable) {
                            item.complete();
                            log.error("Error sending heartbeat", throwable);
                        }
                    }
                });
            } catch (Throwable throwable) {
                log.error("Error sending heartbeat", throwable);
            }
        }, 1, 10, TimeUnit.SECONDS);


    }

    @Override
    public void createCallGroup(CreateCallGroupReq req) {
        // 查询群组名字，相同的不允许创建
        if (checkGroupNameAlreadyExist(req.getGroupName())) {
            throw new ServiceException("Group name already exists");
        }
        String groupName = req.getGroupName();
        String groupCreater = req.getGroupCreater();
        List<String> webMember = req.getWebMember();
        List<String> deviceMember = req.getDeviceMember();

        List<BagDO> bySNList = bagMapper.getBagListBySNList(deviceMember);
        if (bySNList.size() != deviceMember.size()) {
            throw new ServiceException("Device member list has non existing sn");
        }

        CallGroupDO callGroupDO = new CallGroupDO();
        // 左闭右开，数字范围 10000 ~ 99999  五位数字
        int callGroupNumber = getCallGroupNumber();
        callGroupDO.setCallGroupNumber(callGroupNumber);
        callGroupDO.setCallGroupName(groupName);
        callGroupDO.setCallGroupCreater(groupCreater);

        List<SysUser> sysUsers = sysUserMapper.selectListByUserName(webMember);
        if (sysUsers.size() != webMember.size()) {
            throw new ServiceException("Web member list have invalid username");
        }
        ArrayList<CallGroupMemberDO> callGroupMemberDOS = new ArrayList<>();
        for (SysUser sysUser : sysUsers) {
            CallGroupMemberDO aDo = new CallGroupMemberDO();
            aDo.setMemberType(MemberType.webuser);
            aDo.setMemberNumber(sysUser.getUserName());
            aDo.setMemberName(sysUser.getNickName());
            aDo.setCallGroupNumber(callGroupNumber);
            aDo.setMemberLevel(10);
            aDo.setMemberStatus(MemberStatus.onlineNotIn);
            callGroupMemberDOS.add(aDo);
        }

        // 从网关获得设备状态，并封装设备类型群组成员列表
        List<CallGroupMemberDO> deviceGroupMember = getDeviceGroupMember(deviceMember, callGroupNumber);
        callGroupMemberDOS.addAll(deviceGroupMember);

        callGroupMemberDao.saveBatch(callGroupMemberDOS);
        callGroupDao.save(callGroupDO);

        // todo通知web, 通过SSE
        SSEEmitterJoinedGroupMap.forEach((username, emitterSet) -> {
            if (webMember.contains(username)) {
                if (CollectionUtils.isNotEmpty(emitterSet)) {
                    List<JoinedGroupVO> joinedGroup = getJoinedGroup(username, null, null);
                    for (SseEmitter sseEmitter : emitterSet) {
                        JSONObject SSEData = new JSONObject();
                        SSEData.put("code", 200);
                        SSEData.put("msg", "joined group");
                        JSONArray data = new JSONArray();
                        data.addAll(joinedGroup);
                        SSEData.put("data", data);
                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                .data(SSEData.toString())
                                .id(username)
                                .name("joinedGroup");
                        try {
                            log.info("SSE Event id{} name{} data{}", username, "joinedGroup", data);
                            sseEmitter.send(event);
                        } catch (Exception e) {
                            log.error("Error sending joinedGroup", e);
                        }
                    }
                }
            }
        });

        // 通知设备
        try {
            callGroupCommon.reportDeviceAddGroup(callGroupNumber, groupName, deviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(callGroupNumber));
        } catch (Exception e) {
            log.error("create device group report error", e);
        }
    }

    /**
     * 现在只支持对设备成员修改，后面如果支持web用户的话，需要
     *
     * @param req req
     */
    @Override
    public void updateCallGroup(UpdateCallGroupReq req) {
        // 查询当前群主中有哪些成员，跟去req中给的，判断哪些要删除，哪些要添加, 更新群组名
        Integer groupNumber = req.getGroupNumber();
        String callGroupName = req.getGroupName();
        List<String> snList = req.getDeviceMember();
        List<String> webMember = req.getWebMember();

        List<BagDO> bySNList = bagMapper.getBagListBySNList(snList);
        if (bySNList.size() != snList.size()) {
            throw new ServiceException("Device member list has non existing sn");
        }
        List<SysUser> dataWebUser = sysUserMapper.selectListByUserName(webMember);
        if (dataWebUser.size() != webMember.size()) {
            throw new ServiceException("Web member list has non existing username");
        }
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        if (ObjectUtils.isEmpty(callGroupDO)) {
            throw new ServiceException("valid call group number.cannot find this group");
        }
        List<CallGroupMemberDO> callGroupMemberDOS = callGroupMemberDao.selectByGroupDeviceNumber(groupNumber);

        // 现在认为群组中成员不能为空
        if (CollectionUtils.isEmpty(callGroupMemberDOS)) {
            throw new ServiceException("Call group members cannot be empty");
        }
        List<CallGroupMemberDO> webMembers = callGroupMemberDao.selectWebUsersByGroupNumber(groupNumber);

        // 设备相关，获得那些需要增加，那些需要删除
        // 遍历数据库中现有的，如果req中不存在的，那就是要删除的
        List<CallGroupMemberDO> delMember = callGroupMemberDOS.stream().filter(member -> !snList.contains(member.getMemberNumber())).collect(Collectors.toList());
        // 遍历req，数据库中不存在的，那就是要添加的
        List<String> dataBaseMember = callGroupMemberDOS.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList());
        List<String> addMember = snList.stream().filter(member -> !dataBaseMember.contains(member)).collect(Collectors.toList());

        // 人员相关，获得需要哪些新增人员，哪些是删除人员
        List<CallGroupMemberDO> delWebMember = webMembers.stream().filter(memberDO -> !webMember.contains(memberDO.getMemberNumber())).collect(Collectors.toList());
        List<String> dataBaseWebMember = webMembers.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList());
        List<String> addWebMember = webMember.stream().filter(member -> !dataBaseWebMember.contains(member)).collect(Collectors.toList());

        // 获得要添加的
        if (CollectionUtils.isNotEmpty(addMember)) {
            List<CallGroupMemberDO> addDOS = getDeviceGroupMember(addMember, groupNumber);
            callGroupMemberDao.saveBatch(addDOS);
        }
        if (CollectionUtils.isNotEmpty(addWebMember)) {
//            List<SysUser> sysUsers = sysUserMapper.selectListByUserName(addWebMember);
            List<CallGroupMemberDO> webMemberList = getWebMemberList(addWebMember, dataWebUser, groupNumber);
            callGroupMemberDao.saveBatch(webMemberList);
        }
        // 删除成员
        if (CollectionUtils.isNotEmpty(delMember)) {
            callGroupMemberDao.deleteByMemberNumber(groupNumber, delMember.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(delWebMember)) {
            callGroupMemberDao.deleteByMemberNumber(groupNumber, delWebMember.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList()));
        }
        // 更新群组名
        if (!callGroupName.equals(callGroupDO.getCallGroupName())) {
            callGroupDao.updateGroupName(groupNumber, callGroupName);
        }

        // 通知web用户
        SSEEmitterJoinedGroupMap.forEach((username, emitterSet) -> {
            if (webMember.contains(username)) {
                if (CollectionUtils.isNotEmpty(emitterSet)) {
                    List<JoinedGroupVO> joinedGroup = getJoinedGroup(username, null, null);
                    for (SseEmitter sseEmitter : emitterSet) {
                        JSONObject SSEData = new JSONObject();
                        SSEData.put("code", 200);
                        SSEData.put("msg", "joined group");
                        JSONArray data = new JSONArray();
                        data.addAll(joinedGroup);
                        SSEData.put("data", data);
                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                .data(SSEData.toString())
                                .id(username)
                                .name("joinedGroup");
                        try {
                            log.info("SSE Event id{} name{} data{}", username, "joinedGroup", data);
                            sseEmitter.send(event);
                        } catch (Exception e) {
                            log.error("Error sending joinedGroup", e);
                        }
                    }
                }
            }
        });

        // 通知设备
        try {
            // 通知背包设备,被添加到的群组
            if (CollectionUtils.isNotEmpty(addMember)) {
                callGroupCommon.reportDeviceAddGroup(groupNumber, callGroupName, addMember);
            }
            // 通知背包设备，被删除的群组
            if (CollectionUtils.isNotEmpty(delMember)) {
                callGroupCommon.reportDeviceDeleteGroup(groupNumber, delMember.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList()));
            }
            // 通知群组中没有被改动的设备，当前群组的详情。我只需要查询当前群组详情，然后调用/callGroup/groupInfoReport接口即可
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));

        } catch (Exception e) {
            log.error("report device update group call error", e);
        }
    }

    @Override
    public void deleteCallGroup(Integer groupNumber) {
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        if (ObjectUtils.isEmpty(callGroupDO)) {
            throw new ServiceException("Valid call group number");
        }
        List<CallGroupMemberDO> callGroupMemberDOS = callGroupMemberDao.selectByGroupDeviceNumber(groupNumber);
        List<String> webMember = callGroupMemberDOS.stream().filter(member -> member.getMemberType().equals(MemberType.webuser)).map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList());

        // 删除群组，删除群组成员表
        callGroupDao.deleteCallGroup(groupNumber);
        callGroupMemberDao.deleteCallGroup(groupNumber);

        // 通知web用户
        SSEEmitterJoinedGroupMap.forEach((username, emitterSet) -> {
            if (webMember.contains(username)) {
                if (CollectionUtils.isNotEmpty(emitterSet)) {
                    List<JoinedGroupVO> joinedGroup = getJoinedGroup(username, null, null);
                    for (SseEmitter sseEmitter : emitterSet) {
                        JSONObject SSEData = new JSONObject();
                        SSEData.put("code", 200);
                        SSEData.put("msg", "joined group");
                        JSONArray data = new JSONArray();
                        data.addAll(joinedGroup);
                        SSEData.put("data", data);
                        SseEmitter.SseEventBuilder event = SseEmitter.event()
                                .data(SSEData.toString())
                                .id(username)
                                .name("joinedGroup");
                        try {
                            log.info("SSE Event id{} name{} data{}", username, "joinedGroup", data);
                            sseEmitter.send(event);
                        } catch (Exception e) {
                            log.error("Error sending joinedGroup", e);
                        }
                    }
                }
            }
        });

        // report device
        try {
            callGroupCommon.reportDeviceDeleteGroup(groupNumber, callGroupMemberDOS.stream().map(CallGroupMemberDO::getMemberNumber).collect(Collectors.toList()));
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("delete group call error", e);
        }
    }

    @Override
    public List<JoinedGroupVO> getJoinedGroup(String memberNumber, Integer groupNumber, String groupName) {
        // 获得此成员加入的群组的编号
        List<CallGroupMemberDO> joinedGroup = callGroupMemberDao.selectJoinedGroup(memberNumber);
        if (CollectionUtils.isEmpty(joinedGroup)) {
            return Collections.emptyList();
        }
        if (ObjectUtils.isNotEmpty(groupNumber)) {
            // 匹配
            joinedGroup = joinedGroup.stream()
                    .filter(callGroupMemberDO -> String.valueOf(callGroupMemberDO.getCallGroupNumber()).contains(String.valueOf(groupNumber)))
                    .collect(Collectors.toList());
        }
        // 根据编号列表查询，群组更详细的信息
        List<CallGroupDO> groupInfoList = callGroupDao.selectByGroupNumberList(joinedGroup.stream().map(CallGroupMemberDO::getCallGroupNumber).collect(Collectors.toList()));
        if (StringUtils.isNotEmpty(groupName)) {
            // 匹配
            return groupInfoList.stream().filter(callGroupDO -> callGroupDO.getCallGroupName().contains(groupName)).map(callGroupCommon::convertToJoinedGroupVO).collect(Collectors.toList());
        }
        List<JoinedGroupVO> result = groupInfoList.stream().map(callGroupCommon::convertToJoinedGroupVO).collect(Collectors.toList());
        for (JoinedGroupVO vo : result) {
            LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
            query.eq(CallGroupMemberDO::getCallGroupNumber, vo.getGroupNumber());
            vo.setMemberCount((int) callGroupMemberDao.count(query));
        }
        return result;
    }

    @Override
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

    /**
     * redis中放streamid主要用来做判断重复。要获取某个成员的streamid还是去mysql
     * 生成streamid，放到redis。更新数据库。streamid返回给web，并通知其他成员。
     *
     * @param req req
     */
    @Override
    public String enterGroup(EnterGroupReq req) {
        String memberNumber = req.getMemberNumber();
        Integer groupNumber = req.getGroupNumber();
        //check request
        CallGroupMemberDO memberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        if (ObjectUtils.isEmpty(memberDO)) {
            throw new ServiceException("This CallGroup does not have this member");
        }
        if (StringUtils.isNotEmpty(memberDO.getMemberStreamId())) {
            throw new ServiceException("This web user is already enter this group.Maybe at other web Browser");
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

        // 通知web成员
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

        // 通知其他设备此成员进入
        try {
            List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
//            List<CallGroupMemberDO> filteredSelfList = groupOnlineInDeviceMember.stream().filter(groupOnlineIn -> !StringUtils.equals(groupOnlineIn.getMemberNumber(), memberNumber)).collect(Collectors.toList());
            callGroupCommon.reportDeviceMemberEnter(groupNumber, memberNumber, memberName, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("enter group report error", e);
        }

        return streamId;
    }

    @Override
    public boolean applyForSpeak(ApplySpeakReq req) {
        String memberNumber = req.getMemberNumber();
        Integer groupNumber = req.getGroupNumber();
        // check request
        CallGroupMemberDO memberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        if (ObjectUtils.isEmpty(memberDO)) {
            throw new ServiceException("This CallGroup does not have this member");
        }
        // 如果讲话人为空，那么直接申请成功，如果讲话人有，那么需要对比权限才行，权限大于当前讲话人才申请成功
        CallGroupMemberDO applier = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        String speaker = callGroupDO.getCallGroupSpeaker();

        // 当前讲话人不可以重复申请讲话
        if (StringUtils.equals(speaker, applier.getMemberName())) {
            throw new ServiceException("Applier is speaker.Cannot repeatedly apply");
        }

        // 查询当前讲话者权限，进行对比，判断是否申请成功，如果失败则直接return
        if (StringUtils.isNotEmpty(speaker)) {
            CallGroupMemberDO speakerDO = callGroupMemberDao.selectMemberByName(speaker, groupNumber);
            if (speakerDO.getMemberLevel() >= applier.getMemberLevel()) {
                return false;
            }
        }
        // 如果成功，更新群组表，发通知
        callGroupDO.setCallGroupSpeaker(applier.getMemberName());
        callGroupDO.setCallGroupSpeakerStartTime(new Date());
        callGroupDao.updateByGroupNumber(callGroupDO);

        // 网页sse
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

        // 设备通知
        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
//        List<CallGroupMemberDO> filteredSelfList = groupOnlineInDeviceMember.stream().filter(groupOnlineIn -> !StringUtils.equals(groupOnlineIn.getMemberNumber(), memberNumber)).collect(Collectors.toList());
        try {
            callGroupCommon.reportDeviceApplyForSpeak(callGroupDO, applier, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("apply speaker failed", e);
        }
        return true;
    }

    @Override
    public void endSpeak(EndSpeakReq req) {
        Integer groupNumber = req.getGroupNumber();
        String memberNumber = req.getMemberNumber();
        // check group speaker
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
        // 更新数据库speak
        CallGroupDO callGroupDO = new CallGroupDO();
        callGroupDO.setCallGroupNumber(groupNumber);
        callGroupDO.setCallGroupSpeaker("");
        callGroupDO.setCallGroupSpeakerStartTime(null);
        callGroupDao.updateByGroupNumber(callGroupDO);
        // 通知web
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

        // 通知设备
        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
//        List<CallGroupMemberDO> filteredSelfList = groupOnlineInDeviceMember.stream().filter(groupOnlineIn -> !StringUtils.equals(groupOnlineIn.getMemberNumber(), memberNumber)).collect(Collectors.toList());
        try {
            callGroupCommon.reportDeviceEndSpeak(groupNumber, memberNumber, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("end speak report error", e);
        }
    }

    @Override
    public void exitGroup(ExitGroupReq req) {
        Integer groupNumber = req.getGroupNumber();
        String memberNumber = req.getMemberNumber();
        // check request
        CallGroupMemberDO groupMemberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        if (ObjectUtils.isEmpty(groupMemberDO)) {
            throw new ServiceException("This group cannot find this member");
        }
        // 如果当前成员正在讲话，现在他退出了群组，那么应该先执行结束讲话的逻辑
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        String speaker = callGroupDO.getCallGroupSpeaker();
        if (StringUtils.isNotEmpty(speaker) && speaker.equals(groupMemberDO.getMemberName())) {
            EndSpeakReq endSpeakReq = new EndSpeakReq();
            endSpeakReq.setGroupNumber(groupNumber);
            endSpeakReq.setMemberNumber(memberNumber);
            endSpeak(endSpeakReq);
        }

        // update database member status
        CallGroupMemberDO callGroupMemberDO = new CallGroupMemberDO();
        callGroupMemberDO.setCallGroupNumber(groupNumber);
        callGroupMemberDO.setMemberNumber(memberNumber);
        callGroupMemberDO.setMemberStatus(MemberStatus.onlineNotIn);
        callGroupMemberDO.setMemberStreamId("");
        callGroupMemberDao.updateByGroupNumberAndMemberNumber(callGroupMemberDO);

        // delete streamId in redis's STREAM_ID_SET
        redisCache.deleteSetValue(RedisConstant.STREAM_ID_CALL_GROUP_SET.getValue(), callGroupMemberDO.getMemberStreamId());
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
        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
//        List<CallGroupMemberDO> filteredSelfList = groupOnlineInDeviceMember.stream().filter(groupOnlineIn -> !StringUtils.equals(groupOnlineIn.getMemberNumber(), memberNumber)).collect(Collectors.toList());
        try {
            callGroupCommon.reportDeviceExitGroup(groupNumber, memberNumber, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("end speak report error", e);
        }
    }

    @Override
    public List<MemberItemVO> getMemberList() {
        List<MemberItemVO> res = new ArrayList<>();
        // 先获取web用户, 当前只有admin
        SysUser sysUser = new SysUser();
        sysUser.setStatus("0");
        List<SysUser> sysUsers = sysUserMapper.selectUserList(sysUser);
        for (SysUser user : sysUsers) {
            res.add(new MemberItemVO(user.getUserName(), user.getNickName(), MemberType.webuser.name()));
        }
        // 获取设备
        List<BagDO> bagList = bagMapper.getBagList(null);
        bagList.forEach(bagDO -> res.add(new MemberItemVO(bagDO.getSn(), bagDO.getDevName(), MemberType.backpack.name())));
        return res;
    }

    @Override
    public void interruptCall(InterruptCallReq req) {
        Integer groupNumber = req.getGroupNumber();
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        // 当前是否有讲话人
        String callGroupSpeaker = callGroupDO.getCallGroupSpeaker();
        if (StringUtils.isEmpty(callGroupSpeaker)) {
            throw new ServiceException("There is no speaker at present");
        }

        callGroupDO.setCallGroupSpeaker("");
        callGroupDO.setCallGroupSpeakerStartTime(null);
        callGroupDao.updateByGroupNumber(callGroupDO);

        // report web user
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
        CallGroupMemberDO callGroupMemberDO = callGroupMemberDao.selectMemberByName(callGroupSpeaker, groupNumber);
        String memberNumber = callGroupMemberDO.getMemberNumber();
        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
//        List<CallGroupMemberDO> filteredSelfList = groupOnlineInDeviceMember.stream().filter(groupOnlineIn -> !StringUtils.equals(groupOnlineIn.getMemberNumber(), memberNumber)).collect(Collectors.toList());
        try {
            callGroupCommon.reportDeviceEndSpeak(groupNumber, memberNumber, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("end speak report error", e);
        }
    }

    @Override
    public void specifySpeak(SpecifySpeakReq req) {
        Integer groupNumber = req.getGroupNumber();
        String memberNumber = req.getMemberNumber();
        // check 有没有这个成员
        CallGroupMemberDO callGroupMemberDO = callGroupMemberDao.selectMemberByNumber(memberNumber, groupNumber);
        if (ObjectUtils.isEmpty(callGroupMemberDO)) {
            throw new ServiceException("Cannot find this group member");
        }
        // check 这个人是不是讲话者
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        if (StringUtils.equals(callGroupDO.getCallGroupSpeaker(), callGroupMemberDO.getMemberName())) {
            throw new ServiceException("Specify member is already a speaker");
        }
        // 不需要对比权限，直接让这个人作为讲话者
        callGroupDO.setCallGroupSpeaker(callGroupMemberDO.getMemberName());
        callGroupDO.setCallGroupSpeakerStartTime(new Date());
        callGroupDao.updateByGroupNumber(callGroupDO);

        // 网页sse
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

        // 设备通知
        List<CallGroupMemberDO> groupOnlineInDeviceMember = callGroupMemberDao.selectGroupOnlineInDeviceMember(groupNumber);
//        List<CallGroupMemberDO> filteredSelfList = groupOnlineInDeviceMember.stream().filter(groupOnlineIn -> !StringUtils.equals(groupOnlineIn.getMemberNumber(), memberNumber)).collect(Collectors.toList());
        try {
            callGroupCommon.reportDeviceApplyForSpeak(callGroupDO, callGroupMemberDO, groupOnlineInDeviceMember);
            callGroupCommon.reportGroupInfo(getPaasGroupInfo(groupNumber));
        } catch (Exception e) {
            log.error("apply speaker failed", e);
        }
    }

    @Override
    public void putSseInJoinedGroupMap(String memberNumber, SseEmitter emitter) {
        HashSet<SseEmitter> set = SSEEmitterJoinedGroupMap.get(memberNumber);
        if (ObjectUtils.isEmpty(set)) {
            HashSet<SseEmitter> emitterHashSet = new HashSet<>();
            emitterHashSet.add(emitter);
            SSEEmitterJoinedGroupMap.put(memberNumber, emitterHashSet);

            emitter.onCompletion(() -> {
                log.info("sse onCompletion: id:{} ", memberNumber);
                emitterHashSet.remove(emitter);
            });
            emitter.onTimeout(() -> {
                log.info("sse onTimeout: id:{} ", memberNumber);
                emitterHashSet.remove(emitter);
            });
            emitter.onError((throwable) -> {
                log.error("sse onError: id:{} ", memberNumber);
                emitterHashSet.remove(emitter);
            });
        } else {
            set.add(emitter);
            emitter.onCompletion(() -> {
                log.info("sse onCompletion: id:{} ", memberNumber);
                set.remove(emitter);
            });
            emitter.onTimeout(() -> {
                log.warn("sse onTimeout: id:{} ", memberNumber);
                set.remove(emitter);
            });
            emitter.onError((throwable) -> {
                log.error("sse onError: id:{} ", memberNumber, throwable);
                set.remove(emitter);
            });
        }
    }

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

    private List<CallGroupMemberDO> getWebMemberList(List<String> addMember, List<SysUser> sysUsers, Integer groupNumber) {
        ArrayList<CallGroupMemberDO> addWebMemberList = new ArrayList<>();
        for (SysUser sysUser : sysUsers) {
            if (addMember.contains(sysUser.getUserName())) {
                CallGroupMemberDO aDo = new CallGroupMemberDO();
                aDo.setMemberType(MemberType.webuser);
                aDo.setMemberNumber(sysUser.getUserName());
                aDo.setMemberName(sysUser.getNickName());
                aDo.setCallGroupNumber(groupNumber);
                aDo.setMemberLevel(10);
                aDo.setMemberStatus(MemberStatus.onlineNotIn);
                addWebMemberList.add(aDo);
            }
        }
        return addWebMemberList;
    }

    private List<CallGroupMemberDO> getDeviceGroupMember(List<String> deviceMember, int callGroupNumber) {
        ArrayList<CallGroupMemberDO> addDOS = new ArrayList<>();
        List<BagDO> bagListBySNList = bagMapper.getBagListBySNList(deviceMember);
        Map<String, String> deviceStatus = new HashMap<>();
        for (BagDO item : bagListBySNList) {
            CallGroupMemberDO aDo = new CallGroupMemberDO();
            aDo.setMemberType(MemberType.backpack);
            aDo.setMemberNumber(item.getSn());
            aDo.setMemberName(item.getDevName());
            aDo.setCallGroupNumber(callGroupNumber);
            aDo.setMemberLevel(5);
            if (StringUtils.equals(deviceStatus.get(item.getSn()), "online")) {
                aDo.setMemberStatus(MemberStatus.onlineNotIn);
            } else {
                aDo.setMemberStatus(MemberStatus.offline);
            }
            addDOS.add(aDo);
        }
        return addDOS;
    }


    private int getCallGroupNumber() {
        int callGroupNumber = RandomUtils.nextInt(10000, 100000);
        while (ObjectUtils.isNotEmpty(callGroupDao.selectByGroupNumber(callGroupNumber))) {
            callGroupNumber = RandomUtils.nextInt(10000, 100000);
        }
        return callGroupNumber;
    }

    private boolean checkGroupNameAlreadyExist(String groupName) {
        CallGroupDO callGroupDO = callGroupDao.selectByGroupName(groupName);
        return callGroupDO != null;
    }

    public PaasGroupInfoVO getPaasGroupInfo(Integer groupNumber) {
        // 查询群组信息
        CallGroupDO callGroupDO = callGroupDao.selectByGroupNumber(groupNumber);
        // 查询群组成员信息
        List<CallGroupMemberDO> callGroupMemberDOS = callGroupMemberDao.selectByGroupNumber(groupNumber);
        return callGroupCommon.convertToPaasGroupInfoVO(callGroupDO, callGroupMemberDOS);
    }
}
