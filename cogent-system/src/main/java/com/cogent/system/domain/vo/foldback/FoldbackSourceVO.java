package com.cogent.system.domain.vo.foldback;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/9
 * {@code @description:}
 */
@Data
public class FoldbackSourceVO {

    private Integer id;
    private String name;
    private String streamId;
    private String app;
    private Boolean state;
    private Integer delay;
}
