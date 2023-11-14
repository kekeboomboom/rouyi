package com.cogent.system.domain.vo;/*
    @Auther:yinzh
    @Date:2023/5/6/006
    @Description:com.cogent.web.controller.system.vo
    @param 
    @return
*/

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author:wangke
 * @Date:2023/5/6/00610:40
 */
@Data
public class RouteParam {

    private Integer routeId;
    @NotBlank
    private String routeName;
    @NotNull
    private Long sourceId;

    private List<Long> dests;
}
