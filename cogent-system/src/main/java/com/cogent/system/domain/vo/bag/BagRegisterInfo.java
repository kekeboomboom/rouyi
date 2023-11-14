package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: keboom
 * @Date: 2023/5/12/012 16:21
 * @Description:
 */
@Data
public class BagRegisterInfo implements Serializable {

    private static final long serialVersionUID = -942254487663763425L;

    // 背包序列号
    private String sn;
    private String type;
    private String fulltype;
}
