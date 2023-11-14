package com.cogent.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.domain.DO.route.DestDO;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


class BagServiceImplTest {


    BagServiceImpl bagService;

    @Before
    void init() {
        bagService = new BagServiceImpl();
    }

    @Test
    void statusReport() {
        bagService = new BagServiceImpl();
        bagService.statusReport("sn,", "{\"Media_sta_LinkInfo\":{\"feedback\":[0,3,0,0,0,1,0,0,0,0,0],\"live\":[0,0,0,0,0,0,0,0,0,0,0]}}");
    }

    @Test
    void selectUserDefined() {
        List<DestDO> destDOS = new ArrayList<>();
        DestDO destDO1 = new DestDO();
        destDO1.setId(1L);
        destDO1.setDestName("default-previewC108sfgasdf");
        destDOS.add(destDO1);

        DestDO destDO2 = new DestDO();
        destDO2.setId(2L);
        destDO2.setDestName("default-preadfsadfsdf");
        destDOS.add(destDO2);

        DestDO destDO3 = new DestDO();
        destDO3.setId(3L);
        destDO3.setDestName("abcdsa");
        destDOS.add(destDO3);


        List<Long> collect = destDOS.stream()
                .filter(destDO -> !StringUtils.startsWith(destDO.getDestName(), "default-preview"))
                .map(DestDO::getId)
                .collect(Collectors.toList());

        System.out.println(collect.toString());
    }

    @Test
    void json() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("latency", 125);
        jsonObject.put("encryption", "none");
        jsonObject.put("passphrase", "null");
        jsonObject.put("bandover", 25);
        jsonObject.put("ttl", 64);
        jsonObject.put("tos", 0);
        jsonObject.put("mtu", 1496);

        System.out.println(jsonObject);
    }

    @Test
    void filter() {
        String hehe = "http://127.0.0.1:8081/device/backpack/statusReportRequestMethod";
        if (StringUtils.containsIgnoreCase(hehe,"statusReport")) {

            System.out.println("d");
        } else {
            System.out.println("hehe");
        }
    }

    @Test
    void streamFilter() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        List<Integer> collect = list.stream().filter(num -> {
            if (num % 2 == 0) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        System.out.println(collect.toString());
    }


    @Test
    void testCollectionRemove() {
        ArrayList<Integer> destids = new ArrayList<>();

        destids.add(1);
        destids.add(2);
        destids.add(3);
        destids.add(4);

        ArrayList<Integer> defaultDest = new ArrayList<>();
        defaultDest.add(2);
        defaultDest.add(4);

        List<Integer> removeAll = (List<Integer>) CollectionUtils.removeAll(destids, defaultDest);

        System.out.println(destids);
        System.out.println(defaultDest);
        System.out.println(removeAll);
    }
}