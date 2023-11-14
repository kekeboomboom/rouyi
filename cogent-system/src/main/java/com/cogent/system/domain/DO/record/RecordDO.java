package com.cogent.system.domain.DO.record;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/24
 * {@code @description:}
 */
@Data
@TableName("video_record")
public class RecordDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String fileAlias;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String folder;
    private String mediaServerId;
    private Date startTime;
    private String stream;
    private String app;
    private String url;
    private String vhost;
    private String snapPath;

}
