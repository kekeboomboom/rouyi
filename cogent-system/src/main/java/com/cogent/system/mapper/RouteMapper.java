package com.cogent.system.mapper;/*
    @Auther:yinzh
    @Date:2023/5/6/006
    @Description:com.cogent.system.mapper
    @param 
    @return
*/

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cogent.system.domain.Route;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RouteMapper extends BaseMapper<Route> {

    int insertRoute(Route routeDO);

    int updateRoute(Route route);

    int deleteRouteByIds(List<Integer> ids);

    List<Route> selectRouteList(String routeName);

    Route selectRouteById(int id);

    List<Route> selectRouteByIds(List<Integer> ids);

    int switchRoute(@Param("routeId") int routeId,@Param("startStop") boolean startStop);

    int selectCount();

    Route selectRouteBySourceId(Long id);

    Route selectRouteByUuid(String uuid);
}
