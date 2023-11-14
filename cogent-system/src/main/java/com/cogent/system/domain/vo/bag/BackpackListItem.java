package com.cogent.system.domain.vo.bag;

import lombok.Data;

/**
 * @Author keboom
 * @Date 2023-06-07 11:45
 */
@Data
public class BackpackListItem {

    private Integer id;
    private String sn;
    private String devName;
    private String devType;
    private String gbId;
    private String state;
    private Integer liveState;
    private Integer foldbackState;
    private Integer foldbackStateSwitch;
    private Boolean recordSwitch;

}
