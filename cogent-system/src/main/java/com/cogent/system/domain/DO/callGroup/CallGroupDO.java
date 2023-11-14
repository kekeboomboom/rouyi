package com.cogent.system.domain.DO.callGroup;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author keboom
 * @Date 2023-06-26 14:16
 */
@Data
@TableName("call_group")
public class CallGroupDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer callGroupNumber;
    private String callGroupName;
    private String callGroupCreater;
    private String callGroupSpeaker;
    private Date callGroupSpeakerStartTime;
    private Date createTime;
    private Date updateTime;

}
