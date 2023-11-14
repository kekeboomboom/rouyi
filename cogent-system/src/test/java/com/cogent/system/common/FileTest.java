package com.cogent.system.common;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.compress.CompressUtil;
import cn.hutool.extra.compress.extractor.Extractor;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.DateUtils;
import com.cogent.common.utils.HttpUtil;
import com.cogent.common.utils.StringUtils;
import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/16
 * {@code @description:}
 */
public class FileTest {

    @SneakyThrows
    @Test
    void ymlTest() {
        Yaml yaml = new Yaml();

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("upgrade-info.yml");
        Map<String, Object> obj = yaml.load(inputStream);
        System.out.println(obj);

        String deviceType = (String) obj.get("deviceType");
        String description = (String) obj.get("description");
        System.out.println(description);
        System.out.println(deviceType);
        inputStream.close();
    }

    @Test
    void testMd5() {

//        InputStream inputStream = this.getClass()
//                .getClassLoader()
//                .getResourceAsStream("upgrade-info.yml");
        BufferedInputStream inputStream = FileUtil.getInputStream("C:\\Users\\keboo\\Desktop\\a\\1.1.1.2023.tar.gz");
        String s = SecureUtil.md5().digestHex(inputStream);

        System.out.println(s);

//        Assert.assertEquals("md5 not equal",s,"35a56940acb9aa8e578687056345d5c5");
    }

    @Test
    void deleteDirectory() {
        // 我在resource目录创建了hehe目录
//        File hehe = FileUtil.file("hehe");
        //D:\codeProject\javaProject\FC\cogent-system\target\test-classes\hehe
//        System.out.println(hehe.getAbsolutePath());
        // 因为这是在test-class目录，因此访问不到 resource编译后放到classes目录的 hehe目录
//        boolean hehe = FileUtil.del("hehe");
//        System.out.println(hehe);

    }

    @Test
    void testPath() {
        String canonicalPath2 = FileUtil.getAbsolutePath(new File(""));
        //D:\codeProject\javaProject\FC\cogent-system
        System.out.println(canonicalPath2);

        //D:\codeProject\javaProject\FC\cogent-system\target\classes\ upgrade-info.yml ，这个hutool如果传入相对路径，那么就是相对classpath路径
        File file = FileUtil.file("upgrade-info.yml");
        System.out.println(file.getAbsolutePath());

    }

    @Test
    void endWith() {
        String[] split = StringUtils.split("stet/图三.png", "/");
        String s = split[split.length - 1];

        System.out.println(s);
    }

    @SneakyThrows
    @Test
    void zipFileTest() {
        final PipedOutputStream output = new PipedOutputStream();
        final PipedInputStream input = new PipedInputStream(output);


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    output.write("Hello world, pipe!".getBytes());
                } catch (IOException e) {
                }
            }
        });


        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int data = input.read();
                    while (data != -1) {
                        System.out.print((char) data);
                        data = input.read();
                    }
                } catch (IOException e) {
                }
            }
        });

        thread1.start();
        thread2.start();

        Thread.sleep(5000);
    }

    @SneakyThrows
    @Test
    void testPipe() {
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream();
        pos.connect(pis);
        Runnable producer = () -> produceData(pos);
        Runnable consumer = () -> consumeData(pis);
        new Thread(producer).start();
        new Thread(consumer).start();

        Thread.sleep(9000);
    }

    public static void produceData(PipedOutputStream pos) {
        try {
            for (int i = 1; i <= 50; i++) {
                pos.write((byte) i);
                pos.flush();
                System.out.println("Writing: " + i);
                Thread.sleep(500);
            }
            pos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void consumeData(PipedInputStream pis) {
        try {
            int num = -1;
            while ((num = pis.read()) != -1) {
                System.out.println("Reading: " + num);
            }
            pis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 先从list拿到所有object，压缩成zip
     */
    @SneakyThrows
    @Test
    void testZip() {
//        https://github.com/srikanth-lingala/zip4j#working-with-streams
        int listObjects = 5;
        ZipFile zipFile = new ZipFile("filename.zip");
        for (int i = 0; i < listObjects; i++) {
//            intputstream=getObject().bucket().objectName().build();
//            ZipParameters zipParameters = new ZipParameters();
//            zipParameters.setCompressionMethod(CompressionMethod.DEFLATE);
//            zipParameters.setLastModifiedFileTime(111L);
//            zipFile.addStream(inputStream, new ZipParameters());
        }
    }

    @SneakyThrows
    @Test
    void dateTest() {
        Date date1 = DateUtils.parseDate("2023-08-28T05:56:00.485Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Date date2 = DateUtils.addHours(date1, 8);
        System.out.println(date2.toString());
        System.out.println(date2.getTime());

        System.out.println(new Date());

    }

    @Test
    void compressTest() {

        Extractor extractor = CompressUtil.createExtractor(
                CharsetUtil.CHARSET_UTF_8, ArchiveStreamFactory.TAR,
                FileUtil.file("D:\\codeProject\\javaProject\\FC\\cogent-admin\\device-upgrade-pack-repo\\1.1.2.2023\\1.1.1.2023.tar"));

        extractor.extract(FileUtil.file("D:\\codeProject\\javaProject\\FC\\cogent-admin\\device-upgrade-pack-repo\\1.1.2.2023"));


    }

    @Test
    void downloadObject() {
        OkHttpClient httpClient = HttpUtil.getHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("http://localhost:8081/device/version/download?versionNum=1.1.1.2023")
                .header("Range", "bytes=0-5000")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            InputStream inputStream = response.body().byteStream();
            byte[] buf = new byte[5];
            int readBytes;
            BufferedOutputStream outputStream = FileUtil.getOutputStream(new File("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\1.1.1.2023.tar"));

            IoUtil.copy(inputStream, outputStream);
//            while ((readBytes = inputStream.read(buf)) != -1) {
//                if (readBytes < buf.length) {
//                    buf = Arrays.copyOf(buf, readBytes);
//                }
//                System.out.println(Arrays.toString(buf) + "   " + readBytes);
//            }
            inputStream.close();
        } catch (IOException e) {
            throw new ServiceException("请求异常");
        }

        OkHttpClient httpClient2 = HttpUtil.getHttpClient();
        Request request2 = new Request.Builder()
                .get()
                .url("http://localhost:8081/device/version/download?versionNum=1.1.1.2023")
                .header("Range", "bytes=5001-")
                .build();

        try (Response response2 = httpClient2.newCall(request2).execute()) {
            InputStream inputStream = response2.body().byteStream();
            byte[] buf = new byte[5];
            int readBytes;
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\1.1.1.2023.tar", true);

            IoUtil.copy(inputStream, fileOutputStream);
//            while ((readBytes = inputStream.read(buf)) != -1) {
//                if (readBytes < buf.length) {
//                    buf = Arrays.copyOf(buf, readBytes);
//                }
//                System.out.println(Arrays.toString(buf) + "   " + readBytes);
//            }
            inputStream.close();
        } catch (IOException e) {
            throw new ServiceException("请求异常");
        }
    }

    @Test
    void testPow() {
        BigDecimal bigDecimal = new BigDecimal(311789451);
        BigDecimal divide = bigDecimal.divide(new BigDecimal(10000000));
        System.out.println(divide);
    }

    @SneakyThrows
    @Test
    void downloadImage() {
        OkHttpClient httpClient = HttpUtil.getHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("http://192.168.16.46:8081/index/api/getSnap?secret=035c73f7-bb6b-4889-a715-d9eb2d1925cc&url=/opt/java-server/mediakit/www/record/live/C10821A0212/2023-10-25/13-50-48-0.mp4&timeout_sec=10&expire_sec=10")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            InputStream inputStream = response.body().byteStream();

            BufferedOutputStream outputStream = FileUtil.getOutputStream(new File("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\my.jpeg"));

            IoUtil.copy(inputStream, outputStream);
        }
    }

    @Test
    void getSnapPath() {
        String path = "record/live/C10821A0212/2023-10-24/11-26-52-0.mp4";
        String[] split = StringUtils.split(path, "/");
        split[0]="snap";
        split[split.length-1]=split[split.length-1].replace(".mp4",".jpeg");
        System.out.println(StringUtils.join(split,"/"));
    }

    @Test
    void getAbsoluteSnapPath() {
        String filePath = "/opt/java-server/mediakit/www/record/live/C10821A0212/2023-10-24/11-26-52-0.mp4";
        String fileUrl = "record/live/C10821A0212/2023-10-24/11-26-52-0.mp4";
        String snapPath = "snap/C10821A0212/2023-10-24/11-26-52-0.jpeg";
        String snap = filePath.replace(fileUrl, snapPath);

        System.out.println(snap);
    }

    @Test
    void deleteFile() {
//        boolean del = FileUtil.del(new File("D:\\\\codeProject\\\\javaProject\\\\FC\\\\cogent-system\\\\src\\\\test\\\\java\\\\com\\\\cogent\\\\system\\\\common\\\\my2.jpeg"));
        boolean del = FileUtil.del(new File("D:\\codeProject\\javaProject\\FC\\cogent-system\\src\\test\\java\\com\\cogent\\system\\common\\my.jpeg"));
        System.out.println(del);

    }
}
