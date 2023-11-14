package com.cogent.system.domain.DO.bag;

import lombok.Data;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 14:56
 * @Description:
 */
@Data
public class BagDO {

    private Integer id;
    private String sn;
    private String devName;
    private String devType;
    private String gbId;
    private Boolean foldbackState;

    private Boolean isDelete;
    private Boolean isBlacklist;

    private String foldbackStreamId;
    private String state;
    private String androidVersion;
    private Boolean recordSwitch;
}
