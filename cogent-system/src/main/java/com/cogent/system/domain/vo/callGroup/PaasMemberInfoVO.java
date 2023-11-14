package com.cogent.system.domain.vo.callGroup;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/18
 * {@code @description:}
 */
@Data
public class PaasMemberInfoVO {
    private String memberName;

    private String memberNumber;

    // 0/1 如果离线或者不在群组是0，如果在群组中为1
    private Integer online;

    // 0/1 如果不是他讲话则0，如果是他讲话则1
    private Integer speak;
}
