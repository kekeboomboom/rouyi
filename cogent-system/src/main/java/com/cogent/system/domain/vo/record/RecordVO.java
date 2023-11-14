package com.cogent.system.domain.vo.record;

import lombok.Data;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/24
 * {@code @description:}
 */
@Data
public class RecordVO {

    private Integer id;
    private String name;
    private Long size;
    private String origin;
    private String url;
    private String img;
    private Long date;
}
