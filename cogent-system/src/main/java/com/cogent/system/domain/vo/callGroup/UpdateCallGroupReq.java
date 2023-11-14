package com.cogent.system.domain.vo.callGroup;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author keboom
 * @Date 2023-06-27 10:33
 */
@Data
public class UpdateCallGroupReq {

    @NotNull
    private Integer groupNumber;
    @NotBlank
    private String groupName;
    @NotEmpty(message = "web用户不能为空")
    private List<String> webMember;
    @NotEmpty(message = "设备列表不能为空")
    private List<String> deviceMember;

}
