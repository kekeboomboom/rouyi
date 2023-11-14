package com.cogent.system.domain.DO.devUpgrade;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/16
 * {@code @description:}
 */
@Data
@TableName("device_upgrade")
public class DeviceUpgradeDO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String checkCode;
    private String description;
    private String devType;
    private Boolean enable;
    private Boolean forcedUpgrade;
    private String versionNum;

    private String fileUrl;
    private Date uploadTime;

    private Integer majorVersion;
    private Integer subVersion;
    private Integer stageVersion;
}
