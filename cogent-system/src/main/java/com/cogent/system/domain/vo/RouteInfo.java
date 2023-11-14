package com.cogent.system.domain.vo;/*
    @Auther:yinzh
    @Date:2023/5/5/005
    @Description:com.cogent.web.controller.system.vo
    @param 
    @return
*/

import lombok.Data;

import java.util.List;

/**
 * @Author:wangke
 * @Date:2023/5/5/00516:49
 */
@Data
public class RouteInfo {

    private Integer routeId;
    private String routeName;
    private Integer status;
    private String sourceName;
    private Integer destCount;
    private Long runTime;
    private Boolean startStop;
    private SourceInfo source;

    private List<DestInfo> dests;
}
