package com.cogent.system.domain.vo.bag;

import lombok.Data;

import java.util.List;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 13:43
 * @Description:
 */
@Data
public class Total {

    /**
     * 下行码率采样点数据
     */
    private List<Long> rateDown;
    /**
     * 上行码率采样点数据
     */
    private List<Long> rateUp;
}
