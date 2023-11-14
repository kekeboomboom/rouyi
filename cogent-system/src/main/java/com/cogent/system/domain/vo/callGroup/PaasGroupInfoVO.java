package com.cogent.system.domain.vo.callGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/18
 * {@code @description:}
 */
@Data
public class PaasGroupInfoVO {

    private String groupName;

    private Integer groupNumber;

    private String groupSpeaker;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date groupSpeakerStartTime;

    private String streamId;
    private String app;

    private List<PaasMemberInfoVO> members;
}
