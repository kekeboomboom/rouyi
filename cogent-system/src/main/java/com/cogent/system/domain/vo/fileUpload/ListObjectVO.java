package com.cogent.system.domain.vo.fileUpload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/28
 * {@code @description:}
 */
@Data
public class ListObjectVO {


    private String devName;

    private String format;

    private String fullPath;

    private String name;

    private Integer size;

    private String sn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadTime;
}
