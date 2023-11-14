package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.route.RouteDestRelDO;
import com.cogent.system.mapper.RouteDestRelMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/11
 * {@code @description:}
 */
@Repository
public class RouteDestRelDao extends ServiceImpl<RouteDestRelMapper, RouteDestRelDO> {
    @Resource
    private RouteDestRelMapper routeDestRelMapper;

    public RouteDestRelDO selectByDestId(Long id) {
        LambdaQueryWrapper<RouteDestRelDO> query = Wrappers.lambdaQuery();
        query.eq(RouteDestRelDO::getDestId, id.intValue());
        return routeDestRelMapper.selectOne(query);
    }

    public void removeByDestId(Long longDest) {
        LambdaQueryWrapper<RouteDestRelDO> query = Wrappers.lambdaQuery();
        query.eq(RouteDestRelDO::getDestId, longDest.intValue());
        routeDestRelMapper.delete(query);
    }

    public void removeByDestIds(List<Long> destIds) {
        if (CollectionUtils.isEmpty(destIds)) {
            return;
        }
        LambdaQueryWrapper<RouteDestRelDO> query = Wrappers.lambdaQuery();
        query.in(RouteDestRelDO::getDestId, destIds.stream().map(Long::intValue).collect(Collectors.toList()));
        routeDestRelMapper.delete(query);
    }
}
