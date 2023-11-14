package com.cogent.system.domain.DO.bag;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cogent.system.domain.DO.BaseDO;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("backpack_blacklist")
public class BlackListDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    private String sn;
    private String devName;
    private String devType;
}
