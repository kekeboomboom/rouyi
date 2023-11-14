package com.cogent.system.domain.vo.record;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/25
 * {@code @description:}
 */
@Data
public class DeleteRecordReq {

    @NotEmpty(message = "id 列表不能为空")
    private List<Integer> id;
}
