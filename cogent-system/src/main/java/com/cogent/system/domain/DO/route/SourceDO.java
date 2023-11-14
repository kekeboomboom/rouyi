package com.cogent.system.domain.DO.route;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cogent.common.core.domain.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@TableName("source_device")
@Data
public class SourceDO implements Serializable {

    private static final long serialVersionUID = -3809701377330661202L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sourceName;//别名
    private String protocol;//协议
    private String protocolType;//协议类型 caller/listener
    private String addr;//字符串ip
    private int port;//端口号
    private String srtMore;// 如果协议为SRT类型，则以json格式存入数据库，其他类型的协议，此字段为空。
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer sourceState;
}
