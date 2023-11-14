package com.cogent.system.domain.vo;/*
    @Auther:yinzh
    @Date:2023/5/4/004
    @Description:com.cogent.web.controller.system.vo
    @param 
    @return
*/

import lombok.Data;

/**
 * @Author:wangke
 * @Date:2023/5/4/00416:26
 */
@Data
public class SourceInfo {


    private Long sourceId;
    private String name;
    private String protocol;
    private String protocolType;
    private String addr;
    private int port;
    private SRTMore srtMore;// 如果协议为SRT类型，则以json格式存入数据库，其他类型的协议，此字段为空。

    private Integer status;
    private Integer currentRate;

    private Integer state;

}
