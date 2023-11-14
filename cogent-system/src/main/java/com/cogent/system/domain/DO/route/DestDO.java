package com.cogent.system.domain.DO.route;/*
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
 * @Author:wangke
 * @Date:2023/5/5/00513:52
 */
@TableName("dest_device")
@Data
public class DestDO implements Serializable {
    private static final long serialVersionUID = -2709380663726584304L;
    @TableId(type = IdType.AUTO)
    private Long id;
    private String destName;//别名
    private String protocol;//协议
    private String protocolType;//协议类型 caller/listener
    private String addr;//字符串ip
    private Integer port;//端口号
    private String srtMore;// 如果协议为SRT类型，则以json格式存入数据库，其他类型的协议，此字段为空。
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String destUuid;
    private String srcUuid;
    private Boolean startStop;
    private Integer destState;
    private Boolean isSdi;

}
