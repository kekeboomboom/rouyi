package com.cogent.system.domain.DO.fileUpload;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/28
 * {@code @description:}
 */
@Data
@TableName("minio_object")
public class MinioObjectDO {

    private String keyName;
    private Integer size;
    private Date uploadTime;
    private String objectName;
}
