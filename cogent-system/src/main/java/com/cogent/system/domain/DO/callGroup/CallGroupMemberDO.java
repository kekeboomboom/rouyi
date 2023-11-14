package com.cogent.system.domain.DO.callGroup;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author keboom
 * @Date 2023-06-26 14:39
 */
@TableName("call_group_member")
@Data
public class CallGroupMemberDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private MemberType memberType;
    private String memberNumber;
    private String memberName;
    private Integer callGroupNumber;
    private Integer memberLevel;
    private MemberStatus memberStatus;
    private String memberRole;
    private String memberStreamId;
    private Date createTime;
    private Date updateTime;

}
