package com.cogent.system.domain.vo.bag;

import lombok.Data;

/**
 * @Author: keboom
 * @Date: 2023/5/15/015 13:45
 * @Description:
 */
@Data
public class LAN {
    /**
     * 地址池结束
     */
    private String end;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 子网掩码
     */
    private String mask;
    /**
     * 地址池开始
     */
    private String start;
}
