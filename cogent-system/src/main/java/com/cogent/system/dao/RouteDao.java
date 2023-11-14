package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.Route;
import com.cogent.system.mapper.RouteMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/7
 * {@code @description:}
 */
@Repository
public class RouteDao extends ServiceImpl<RouteMapper, Route> {

    @Resource
    private RouteMapper routeMapper;

    public Route selectByRouteName(String routeName) {
        LambdaQueryWrapper<Route> query = Wrappers.lambdaQuery();
        query.eq(Route::getRouteName, routeName);
        return routeMapper.selectOne(query);
    }

    public List<Route> selectByRouteNames(List<String> routeNames) {
        LambdaQueryWrapper<Route> query = Wrappers.lambdaQuery();
        query.in(Route::getRouteName, routeNames);
        return routeMapper.selectList(query);
    }

    public void deleteByName(String sn) {
        LambdaQueryWrapper<Route> query = Wrappers.lambdaQuery();
        query.eq(Route::getRouteName, sn);
        routeMapper.delete(query);
    }

    public Route selectByUuid(String srcUuid) {
        LambdaQueryWrapper<Route> query = Wrappers.lambdaQuery();
        query.eq(Route::getUuid, srcUuid);
        return routeMapper.selectOne(query);
    }
}
