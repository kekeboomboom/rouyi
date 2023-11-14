package com.cogent.system.domain.DO.bag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author keboom
 * @Date 2023-06-06 16:42
 */
@Data
@TableName("media_history_info")
public class MediaHistoryInfoDO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String sn;
    private Integer bitrate;
    private Integer dropRate;
    private Integer rtt;
    private Integer srtbuffer;
    private Integer srtlatency;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
