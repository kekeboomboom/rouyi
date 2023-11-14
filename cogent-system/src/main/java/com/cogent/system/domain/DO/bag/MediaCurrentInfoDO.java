package com.cogent.system.domain.DO.bag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author keboom
 * @Date 2023-06-07 9:25
 */
@Data
@TableName("media_current_info")
public class MediaCurrentInfoDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String sn;
    private Integer uptime;
    private Integer connected;
    private Integer reconnection;
    private Integer dropPkt;
    private Integer lossRate;
    private Integer usedBandWidth;
    private String staVideoInput;
    private Integer staFeedbackState;
    private Integer staLiveState;

    private Integer vol0;
    private Integer vol1;
    private Integer vol2;
    private Integer vol3;
    private Date createTime;
    private Date updateTime;
}
