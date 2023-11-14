package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 17:49
 * @Description:
 */
@Data
public class BagListItem implements Serializable {
    private static final long serialVersionUID = 2304943545907270219L;

    /**
     * 设备名称
     */
    private String alias;
    /**
     * 背包编号：数据库中的编号
     */
    private long backpackId;
    /**
     * 国标编号
     */
    private String gb;
    /**
     * 序列号
     */
    private String sn;
    /**
     * 设备类型：T60/T80
     */
    private String type;
}
