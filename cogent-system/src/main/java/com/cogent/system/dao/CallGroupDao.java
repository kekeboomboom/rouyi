package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.callGroup.CallGroupDO;
import com.cogent.system.mapper.CallGroupMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @Author keboom
 * @Date 2023-06-26 14:21
 */
@Repository
public class CallGroupDao extends ServiceImpl<CallGroupMapper, CallGroupDO> {

    @Resource
    private CallGroupMapper callGroupMapper;

    public CallGroupDO selectByGroupNumber(Integer groupNumber) {
        LambdaQueryWrapper<CallGroupDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupDO::getCallGroupNumber, groupNumber);
        return callGroupMapper.selectOne(query);
    }

    public void updateGroupName(Integer groupNumber, String callGroupName) {
        callGroupMapper.update(null, Wrappers.<CallGroupDO>lambdaUpdate()
                .setSql("call_group_name = " + "\"" + callGroupName + "\"")
                .eq(CallGroupDO::getCallGroupNumber, groupNumber));
    }

    public void deleteCallGroup(Integer groupNumber) {
        LambdaQueryWrapper<CallGroupDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupDO::getCallGroupNumber, groupNumber);
        callGroupMapper.delete(query);
    }

    public List<CallGroupDO> selectByGroupNumberList(List<Integer> groupNumberList) {
        if (CollectionUtils.isEmpty(groupNumberList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<CallGroupDO> query = Wrappers.lambdaQuery();
        query.in(CallGroupDO::getCallGroupNumber, groupNumberList);
        return callGroupMapper.selectList(query);
    }

    public CallGroupDO selectByGroupName(String groupName) {
        LambdaQueryWrapper<CallGroupDO> query = Wrappers.lambdaQuery();
        query.eq(CallGroupDO::getCallGroupName, groupName);
        return callGroupMapper.selectOne(query);
    }

    public void updateByGroupNumber(CallGroupDO callGroupDO) {
        LambdaUpdateWrapper<CallGroupDO> update = Wrappers.lambdaUpdate();
        update.eq(CallGroupDO::getCallGroupNumber, callGroupDO.getCallGroupNumber());
        callGroupMapper.update(callGroupDO, update);
    }
}
