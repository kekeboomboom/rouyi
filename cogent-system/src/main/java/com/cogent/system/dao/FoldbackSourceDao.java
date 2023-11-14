package com.cogent.system.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cogent.system.domain.DO.foldback.FoldbackSourceDO;
import com.cogent.system.mapper.FoldbackSourceMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/20
 * {@code @description:}
 */
@Repository
public class FoldbackSourceDao extends ServiceImpl<FoldbackSourceMapper, FoldbackSourceDO> {

    @Resource
    private FoldbackSourceMapper foldbackSourceMapper;


}
