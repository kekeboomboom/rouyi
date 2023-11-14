package com.cogent.system.domain.vo.callGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author keboom
 * @Date 2023-06-27 16:49
 */
@Data
public class GroupInfoVO {


    private String groupName;

    private Integer groupNumber;

    private String groupSpeaker;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date groupSpeakerStartTime;

    private String streamId;
    private String app;

    private List<MemberInfoVO> members;
}
