package com.cogent.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cogent.system.domain.DO.bag.MediaHistoryInfoDO;
import org.apache.ibatis.annotations.Select;

/**
 * @Author keboom
 * @Date 2023-06-06 17:07
 */
public interface MediaHistoryInfoMapper extends BaseMapper<MediaHistoryInfoDO> {

    @Select("SELECT * FROM media_history_info where sn = #{sn} ORDER BY id DESC limit 1")
    MediaHistoryInfoDO getLatestInfoBySN(String sn);
}
