package com.cogent.system.domain.vo.route;

import com.cogent.system.domain.vo.SRTMore;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/9
 * {@code @description:}
 */
@Data
public class AddDestReq {

    @NotEmpty
    private String name;
    @NotEmpty
    private String protocol;
    private String protocolType;
    @NotEmpty
    private String addr;
    @NotNull
    private Integer port;

    private SRTMore srtMore;// 如果协议为SRT类型，则以json格式存入数据库，其他类型的协议，此字段为空。
    @NotNull
    private Boolean isSdi;

}
