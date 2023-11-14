package com.cogent.system.common;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServlet;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @Author keboom
 * @Date 2023-06-06 11:36
 */
class BagInfoReportTest {

    @Test
    void testJson() {
        String media = "{\"Media_sta_LinkInfo\":{\"feedback\":[0,3,0,0,0,1,0,0,0,0,0],\"live\":[0,0,0,0,0,0,0,0,0,0,0]}}";
        JSONObject jsonObject = JSONObject.parseObject(media);
        JSONArray jsonArray = jsonObject.getJSONObject("Media_sta_LinkInfo").getJSONArray("feedback");
        System.out.println(jsonArray.toString());
        assertEquals("[0,3,0,0,0,1,0,0,0,0,0]", jsonArray.toString());
        assertEquals(3, jsonArray.getInteger(1));
    }

    @Test
    void tesVideoInput() {
        String video = "{\"Media_sta_VideoInput\":{\"hdmiinfo\":\"2160P60\",\"hdmilock\":1,\"sdiinfo\":\"\",\"sdilock\":0}}";
        JSONObject jsonObject = JSONObject.parseObject(video);

        JSONObject mediaStaVideoInput = jsonObject.getJSONObject("Media_sta_VideoInput");

        String s = mediaStaVideoInput.toString();

        System.out.println(s);

    }

    @SneakyThrows
    @Test
    void queue() {
        BlockingQueue<String> mediaLinkInfoQueue = new LinkedBlockingQueue<>(50);
        mediaLinkInfoQueue.put("a");
        mediaLinkInfoQueue.put("b");
        mediaLinkInfoQueue.put("c");
        mediaLinkInfoQueue.put("d");

        System.out.println(mediaLinkInfoQueue.size());
        System.out.println(mediaLinkInfoQueue);

        System.out.println(mediaLinkInfoQueue.size());

//        Collections.sort();
//        HttpServlet


    }
}