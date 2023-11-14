package com.cogent.system.domain.vo.callGroup;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Author keboom
 * @Date 2023-06-26 14:07
 */
@Data
public class CreateCallGroupReq {

    @NotBlank
    private String groupName;
    @NotBlank
    private String groupCreater;
    @NotEmpty
    private List<String> webMember;
    @NotEmpty
    private List<String> deviceMember;

}
