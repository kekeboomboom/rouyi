package com.cogent.system.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.setting.yaml.YamlUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/4
 * {@code @description:}
 */
public class YmlTest {

    @SneakyThrows
    @Test
    void testYml() {
//        String canonicalPath = FileUtil.getCanonicalPath(new File(""));
//        String confPath = canonicalPath + "/application-server.yml";
        String confPath = "D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\application-server.yml";
        Dict dict = YamlUtil.loadByPath(confPath);
        Map<String,Object> server = (Map<String, Object>) dict.get("server");
        Integer port = (Integer) server.get("port");
        System.out.println(port);
        System.out.println(dict.getBool("sdi_switch"));

        String hostIp = dict.getStr("HOST_IP");
        String externIp = dict.getStr("externIp") == null ? "" : dict.getStr("externIp");
        String anInterface = dict.getStr("interface") == null ? "" : dict.getStr("interface");
        String gbEnable = dict.getStr("GB_ENABLE");
        Boolean sdiSwitch = dict.getBool("sdi_switch");
        Boolean nmsEnable = dict.getBool("NMS_enable");
        System.out.println(hostIp);
        System.out.println(externIp);
        System.out.println(anInterface);


//        server.replace("port", 8084);
//
//        dict.replace("sdi_switch", true);
//        YamlUtil.dump(dict, new FileWriter(confPath));


    }

}
