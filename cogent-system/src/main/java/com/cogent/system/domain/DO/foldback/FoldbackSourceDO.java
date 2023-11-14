package com.cogent.system.domain.DO.foldback;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/20
 * {@code @description:}
 */
@Data
@TableName("foldback_source")
public class FoldbackSourceDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String streamId;
    private String app;
    private Integer delay;
}
