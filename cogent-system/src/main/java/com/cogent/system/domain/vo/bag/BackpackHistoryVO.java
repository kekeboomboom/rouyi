package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author keboom
 * @Date 2023-06-08 17:42
 */
@Data
public class BackpackHistoryVO implements Serializable {
    private static final long serialVersionUID = -4118874071836510703L;

    private String sn;
    private Integer pageNum;
    private Integer pageSize;
    private Long startTimestamp;
    private Long endTimestamp;
    private Integer minBitrate;
    private Integer maxBitrate;
    private Integer minDropRate;
    private Integer maxDropRate;
    private Integer minRtt;
    private Integer maxRtt;
    private Integer minLatency;
    private Integer maxLatency;
    private Integer minBuffer;
    private Integer maxBuffer;
}
