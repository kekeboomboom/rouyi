package com.cogent.system.common;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/6
 * {@code @description:}
 */
public class IOTest {

    @SneakyThrows
    @Test
    void inputTest() {
        String filePath = "D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\abcde.txt";
        BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));

//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());

//        inputStream.skip(1);
//        System.out.println("-----------------");
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
//        System.out.println(inputStream.read());
        long fileSize = FileUtil.file(filePath).length();
        long start = 2;
        long length = fileSize-2;
        byte[] buf = new byte[10];
        int bytesRead;
        inputStream.skip(start);
        // sum 是当前读取的字节数
        int sum = 0;
        while (sum<length && (bytesRead = inputStream.read(buf, 0, (length - sum) <= buf.length ? (int) (length - sum) : buf.length)) != -1) {
            if (bytesRead < buf.length) {
                buf = Arrays.copyOf(buf, bytesRead);
            }
            System.out.print(Arrays.toString(buf) + "   ");
            System.out.println(bytesRead);
            sum += bytesRead;
        }

        inputStream.close();
    }

    @Test
    void charTest() {
        System.out.println((char) 122);


    }
}
