package com.cogent.system.domain.vo.callGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/10
 * {@code @description:}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberItemVO {

    private String memberNumber;
    private String memberName;
    private String memberType;
}
