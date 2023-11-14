package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.callGroup.CallGroupMemberDO;
import com.cogent.system.domain.DO.callGroup.MemberStatus;
import com.cogent.system.domain.DO.callGroup.MemberType;
import com.cogent.system.mapper.CallGroupMemberMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author keboom
 * @Date 2023-06-26 14:53
 */
@Repository
public class CallGroupMemberDao extends ServiceImpl<CallGroupMemberMapper, CallGroupMemberDO> {

    @Resource
    private CallGroupMemberMapper callGroupMemberMapper;

    public List<CallGroupMemberDO> selectByGroupDeviceNumber(Integer groupNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getCallGroupNumber, groupNumber)
                .eq(CallGroupMemberDO::getMemberType, MemberType.backpack);
        return callGroupMemberMapper.selectList(query);
    }

    public List<CallGroupMemberDO> selectByGroupNumber(Integer groupNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getCallGroupNumber, groupNumber);
        return callGroupMemberMapper.selectList(query);
    }

    public void deleteByMemberNumber(Integer groupNumber, List<String> memberNumberList) {
        if (CollectionUtils.isEmpty(memberNumberList)) {
            return;
        }
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getCallGroupNumber, groupNumber)
                .in(CallGroupMemberDO::getMemberNumber, memberNumberList);
        callGroupMemberMapper.delete(query);
    }

    public void deleteCallGroup(Integer groupNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getCallGroupNumber, groupNumber);
        callGroupMemberMapper.delete(query);
    }

    public List<CallGroupMemberDO> selectJoinedGroup(String memberNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getMemberNumber, memberNumber);
        return callGroupMemberMapper.selectList(query);
    }

    public void updateCallGroupMember(CallGroupMemberDO callGroupMemberDO) {
        LambdaUpdateWrapper<CallGroupMemberDO> update = Wrappers.lambdaUpdate();
        update.eq(CallGroupMemberDO::getMemberNumber, callGroupMemberDO.getMemberNumber())
                .eq(CallGroupMemberDO::getCallGroupNumber, callGroupMemberDO.getCallGroupNumber());
        callGroupMemberMapper.update(callGroupMemberDO, update);
    }

    /**
     * 查询当前在线且在当前群组、且是设备的成员
     *
     * @param groupNumber
     * @return
     */
    public List<CallGroupMemberDO> selectGroupOnlineInDeviceMember(Integer groupNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getCallGroupNumber, groupNumber)
                .eq(CallGroupMemberDO::getMemberStatus, MemberStatus.onlineIn)
                .eq(CallGroupMemberDO::getMemberType, MemberType.backpack);
        return callGroupMemberMapper.selectList(query);
    }

    public CallGroupMemberDO selectMemberByNumber(String memberNumber, Integer groupNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getMemberNumber, memberNumber)
                .eq(CallGroupMemberDO::getCallGroupNumber, groupNumber);
        return callGroupMemberMapper.selectOne(query);
    }

    public CallGroupMemberDO selectMemberByName(String speaker, Integer groupNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getMemberName, speaker)
                .eq(CallGroupMemberDO::getCallGroupNumber, groupNumber);
        return callGroupMemberMapper.selectOne(query);
    }

    public void updateByGroupNumberAndMemberNumber(CallGroupMemberDO callGroupMemberDO) {
        LambdaUpdateWrapper<CallGroupMemberDO> update = Wrappers.lambdaUpdate();
        update.eq(CallGroupMemberDO::getCallGroupNumber, callGroupMemberDO.getCallGroupNumber())
                .eq(CallGroupMemberDO::getMemberNumber, callGroupMemberDO.getMemberNumber());
        callGroupMemberMapper.update(callGroupMemberDO, update);
    }

    public List<CallGroupMemberDO> selectWebUsersByGroupNumber(Integer groupNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getMemberType, MemberType.webuser)
                .eq(CallGroupMemberDO::getCallGroupNumber, groupNumber);
        return callGroupMemberMapper.selectList(query);
    }

    /**
     * 查询当前成员，进入的群组
     * @param memberNumber
     * @return
     */
    public CallGroupMemberDO selectMemberEnteredGroup(String memberNumber) {
        LambdaQueryWrapper<CallGroupMemberDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupMemberDO::getMemberNumber, memberNumber)
                .eq(CallGroupMemberDO::getMemberStatus, MemberStatus.onlineIn);
        return callGroupMemberMapper.selectOne(query);
    }
}
