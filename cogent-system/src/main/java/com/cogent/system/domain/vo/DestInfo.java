package com.cogent.system.domain.vo;/*
    @Auther:yinzh
    @Date:2023/5/5/005
    @Description:com.cogent.web.controller.system.vo
    @param 
    @return
*/

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author:wangke
 * @Date:2023/5/5/00514:12
 */
@Data
public class DestInfo {

    private Long destId;
    private String name;
    private String protocol;
    private String protocolType;
    @NotNull
    private String addr;
    private int port;
    private SRTMore srtMore;// 如果协议为SRT类型，则以json格式存入数据库，其他类型的协议，此字段为空。

    private Boolean startStop;
    private Integer state;
    private Integer currentRate;

    private Integer status;
    private Boolean isSdi;
}
