package com.cogent.system.domain.DO.route;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/11
 * {@code @description:}
 */
@TableName("route_dest_rel")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteDestRelDO implements Serializable {

    private static final long serialVersionUID = -3624544903484526402L;

    private Integer routeId;
    private Integer destId;
}
