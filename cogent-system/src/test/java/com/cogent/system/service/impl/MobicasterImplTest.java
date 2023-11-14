package com.cogent.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.system.domain.vo.mobicaster.AppInfo;
import com.cogent.system.domain.vo.mobicaster.AppMedia;
import org.junit.jupiter.api.Test;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/13
 * {@code @description:}
 */
public class MobicasterImplTest {

    @Test
    void parse() {
        AppInfo appInfo = new AppInfo();
        AppMedia appMedia = new AppMedia();
        appMedia.setAudioCount(1);
        appMedia.setEncodeMode("sdf");
        appInfo.setMedia(appMedia);

        JSONObject from = JSONObject.from(appInfo);

        System.out.println(from.getString("media"));

        System.out.println(from.getJSONObject("media").getString("encodeMode"));
    }
}
