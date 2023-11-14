package com.cogent.system.domain.vo;/*
    @Auther:yinzh
    @Date:2023/5/4/004
    @Description:com.cogent.web.controller.system.vo
    @param 
    @return
*/

import lombok.Data;

/**
 * @Author:wangke
 * @Date:2023/5/4/00416:29
 */
@Data
public class SRTMore {

    private int delay;
    private String encryption;
    private String password;
    private int bandwidth;
    private int ttl;
    private int tos;
    private int mtu;

}

