package com.cogent.system.service.impl;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.setting.Setting;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.dialect.PropsUtil;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.system.domain.vo.bag.BagDto;
import com.cogent.system.domain.vo.bag.Channel;
import com.cogent.system.domain.vo.bag.ChannelElement;
import com.cogent.system.domain.vo.callGroup.GroupInfoVO;
import lombok.SneakyThrows;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.net.SocketException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * @Author keboom
 * @Date 2023-06-26 14:59
 */
class CallGroupServiceImplTest {


    @Test
    void randomInt() {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);

        System.out.println(integers.contains(1));
        System.out.println(integers.contains(9));
    }

    @Test
    void randomString() {
        for (int i = 0; i < 20; i++) {
            String s = RandomStringUtils.randomAlphanumeric(8);
            System.out.println(s);
        }
    }

    @Test
    void getEnumValues() {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        integers.add(4);
        List<Integer> collect = integers.stream().filter(item -> item == 2).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    void Set() {

    }

    @SneakyThrows
    @Test
    void schedule() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello");
            }
        }, 2, 2, TimeUnit.SECONDS);
        Thread.sleep(10000);
    }

    @Test
    void addAll() {
        GroupInfoVO groupInfoVO = new GroupInfoVO();
        groupInfoVO.setGroupName("group");
        groupInfoVO.setGroupSpeaker("sss");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", groupInfoVO);
        System.out.println(jsonObject.toString());
    }

    @Test
    void remove() {
        GroupInfoVO groupInfoVO = new GroupInfoVO();
        groupInfoVO.setGroupNumber(123);
        groupInfoVO.setApp("app");

        JSONObject from = JSONObject.from(groupInfoVO);
        System.out.println(from.toString());
    }

    @Test
    void setting() {
        BagDto bagDto = new BagDto();

        Channel channel = new Channel();
        ChannelElement channel0 = new ChannelElement();
        channel.setChannel0(channel0);
        channel0.setApn("sss");
        channel0.setGateway("localhost");

        bagDto.setChannel(channel);
        JSONObject from = JSONObject.from(bagDto);

        System.out.println(from);


    }

    @Test
    void runTimeUtils() {

        Process ipconfig = RuntimeUtil.exec("ipconfig");

        ipconfig.getInputStream();

        FastByteArrayOutputStream read = IoUtil.read(ipconfig.getInputStream(), true);

//        System.out.println(read.toString());

    }

    @Test
    void getInterface() {


        try {
            // 枚举所有接口
            Enumeration<NetworkInterface> enuNetworkInterface = NetworkInterface.getNetworkInterfaces();
            // 所有接口信息存入 ArrayList 对象
            ArrayList<NetworkInterface> arryNetworkInterface = Collections.list(enuNetworkInterface);

            // 遍历存入接口的 ArryList 对象
            Iterator<NetworkInterface> iteratorInterface = arryNetworkInterface.iterator();
            while (iteratorInterface.hasNext() == true) {
                // 得到一个借口给
                NetworkInterface networkInterface = iteratorInterface.next();
                // 获取每个接口中的所有ip网络接口集合，因为可能有子接口
                ArrayList<InetAddress> arryInetAddress = Collections.list(networkInterface.getInetAddresses());

                // 获取接口名
                final String strInterface = networkInterface.getName();

                // 遍历某个接口下的所有 IP 地址
                Iterator<InetAddress> iteratorAddress = arryInetAddress.iterator();
                while (iteratorAddress.hasNext() == true) {
                    InetAddress inet = iteratorAddress.next();
                    // 筛选地址类型
                    if (inet instanceof Inet4Address) {
                        // 列出 ipv4 地址
                        String strIP = inet.getHostAddress();
                        System.out.printf("%-10s %-5s %-6s %-15s\n", "InetfaceName:", strInterface, " IPv4:", strIP);
                    } else {
                        // 列出 ipv6 地址
                        String strIP = inet.getHostAddress();
                        System.out.printf("%-10s %-5s %-6s %-15s\n", "InetfaceName:", strInterface, " IPv6:", strIP);
                    }
                }
            }
        } catch (SocketException s) {
            s.printStackTrace();
        }


    }


    @Test
    void getDefaultInterface() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress() && address.isSiteLocalAddress()) {
                        String ipAddress = address.getHostAddress();
                        String interfaceName = networkInterface.getName();
                        System.out.println("IP Address: " + ipAddress);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getENV() throws SocketException {
//        Props props = new Props("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\service\\impl\\env");
//
//        props.setProperty("FOLDBACK_PORT","localhost");
//
//        props.store("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\service\\impl\\env");

        //修改mediakit 中的配置文件
//        Setting setting = new Setting("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\service\\impl\\config.ini");
//
//        setting.putByGroup("externIP", "rtc", "132446546");
//        setting.putByGroup("localIp", "srt", "asdfsdaf432adsf1");
//
//        setting.store();

        ArrayList<String> interfaceS = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            interfaceS.add(networkInterface.getName());
        }
        System.out.println();
    }

    @Test
    void getFilePath() {
        String tmpDirPath = FileUtil.getAbsolutePath("");
        String canonicalPath = FileUtil.getCanonicalPath(new File(""));
        File file = new File("file://../default.conf");
        System.out.println(tmpDirPath);
    }
}