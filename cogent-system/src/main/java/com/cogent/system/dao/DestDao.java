package com.cogent.system.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.route.DestDO;
import com.cogent.system.mapper.DestMapper;
import com.cogent.system.mapper.RouteMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DestDao extends ServiceImpl<DestMapper, DestDO> {

    @Resource
    private RouteMapper routeMapper;
    @Resource
    private DestMapper destMapper;

    public DestDO selectByName(String name) {
        LambdaQueryWrapper<DestDO> query = Wrappers.lambdaQuery();
        query.eq(DestDO::getDestName, name);
        return destMapper.selectOne(query);
    }

    public List<DestDO> selectByIds(String destIds) {
        List<String> destList = Arrays.stream(destIds.split(",")).collect(Collectors.toList());
        LambdaQueryWrapper<DestDO> query = Wrappers.lambdaQuery();
        query.in(DestDO::getId, destList);
        return destMapper.selectList(query);
    }
}
