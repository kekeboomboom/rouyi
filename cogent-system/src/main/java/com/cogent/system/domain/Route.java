package com.cogent.system.domain;/*
    @Auther:yinzh
    @Date:2023/5/5/005
    @Description:com.cogent.system.domain
    @param 
    @return
*/

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
*@Author:wangke
*@Date:2023/5/5/00514:56
*/
@Data
@TableName("route")
public class Route implements Serializable {

    private static final long serialVersionUID = 183758095517795554L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String routeName;
    private Boolean startStop;
    private Long sourceId;
    // 以逗号分割，例如 1,2,3,4
    private String destIds;
    // 去掉这个，放到目的地
    private Long runTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String uuid;

}
