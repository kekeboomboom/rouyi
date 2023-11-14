package com.cogent.system.domain.vo.fileUpload;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/24
 * {@code @description:}
 */
@Data
public class DeleteArgs {

    @NotEmpty
    private String path;
}
