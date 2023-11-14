package com.cogent.system.domain.vo.callGroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author keboom
 * @Date 2023-06-27 16:17
 */
@Data
public class JoinedGroupVO {

    private String groupCreater;
    private String groupName;
    private Integer groupNumber;
    private String groupSpeaker;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date groupSpeakerStartTime;
    private Integer memberCount;
}
