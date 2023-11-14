package com.cogent.system.domain.vo.route;

import com.cogent.system.domain.vo.DestInfo;
import com.cogent.system.domain.vo.SourceInfo;
import lombok.Data;

import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/10
 * {@code @description:}
 */
@Data
public class RouteListVO {

    private Integer routeId;
    private String routeName;
    private String bagName;
    private Integer status;
    private String sourceName;
    private Integer destCount;
    private Long runTime;
    private Boolean startStop;
    private SourceInfo source;

    private List<DestInfo> dests;
}
