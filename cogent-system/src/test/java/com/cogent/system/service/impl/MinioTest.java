package com.cogent.system.service.impl;

import com.cogent.common.utils.StringUtils;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/7/13
 * {@code @description:}
 */
class MinioTest {

    MinioClient minioClient;

    @BeforeEach
    void beforeAll() throws Exception {
        minioClient = MinioClient.builder()
                .endpoint("http://192.168.16.46:9002")
                .credentials("xFkEokGRxAlwIs4Vw5cO", "jaz4FlvCDDKvbBuQYJ9NIohMUlRDsT09F1PZH6KB")
                .build();
    }


    @SneakyThrows
    @Test
    void testMinio() {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("http://47.101.140.133:9000")
                            .credentials("rqhY60lGTGgS1WzJtOVs", "5lpbSogGo4wEI7iFUte3UYnozJ1Ug3xYxs5PJole")
                            .build();

            // Make 'asiatrip' bucket if not exist.
//            boolean found =
//                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("asiatrip").build());
//            if (!found) {
//                // Make a new bucket called 'asiatrip'.
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket("asiatrip").build());
//            } else {
//                System.out.println("Bucket 'asiatrip' already exists.");
//            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("asiatrip")
                            .object("hehe.txt")
                            .filename("D:\\codeProject\\javaProject\\minio\\src\\test\\java\\com\\example\\minio\\hello2.txt")
                            .build());
            System.out.println(
                    "'/home/user/Photos/asiaphotos.zip' is successfully uploaded as "
                            + "object 'asiaphotos-2015.zip' to bucket 'asiatrip'.");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }

    @Test
    void listObjects() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket("cogent").recursive(true).build());
//        Iterable<Result<Item>> results = minioClient.listObjects(
//                ListObjectsArgs.builder().bucket("cogent").startAfter("test/1.1.2.2023.tar").build());
        for (Result<Item> result : results) {
            Item item = result.get();
            String objectName = item.objectName();
            System.out.print(objectName + "   ");
            if (objectName.endsWith("/")) {
                continue;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            System.out.print(item.lastModified().format(formatter.withZone(ZoneId.of("Asia/Shanghai"))) + "   ");
            System.out.print(item.size());
            System.out.println();

        }
    }

    @SneakyThrows
    @Test
    void dir() {
        minioClient.putObject(
                PutObjectArgs.builder().bucket("cogent").object("hello/world/").stream(
                                new ByteArrayInputStream(new byte[]{}), 0, -1)
                        .build());
    }

    @SneakyThrows
    @Test
    void getObjectInfo() {
        StatObjectResponse statObjectResponse = minioClient.statObject(
                StatObjectArgs.builder().bucket("cogent").object("shellman-2021-02-19.pdf.zip").build());

        Map<String, String> stringStringMap = statObjectResponse.userMetadata();
        System.out.println(stringStringMap.toString());
        System.out.println(statObjectResponse);
    }


    @SneakyThrows
    @Test
    void deleteObjects() {
        List<DeleteObject> objects = new LinkedList<>();
        objects.add(new DeleteObject("hello/world/"));
        objects.add(new DeleteObject("hello/logo.svg"));
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder().bucket("cogent").objects(objects).build());
        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            System.out.println(
                    "Error in deleting object " + error.objectName() + "; " + error.message());
        }

    }

    @SneakyThrows
    @Test
    void notifyEvent() {
        // 此方法会阻塞住，直到有响应的事件发生，然后执行while循环中代码，直到所有事件都处理完，然后接着阻塞。
        String[] events = {"s3:ObjectCreated:*", "s3:ObjectAccessed:*"};
        try (CloseableIterator<Result<NotificationRecords>> ci =
                     minioClient.listenBucketNotification(
                             ListenBucketNotificationArgs.builder()
                                     .bucket("cogent")
                                     .prefix("")
                                     .suffix("")
                                     .events(events)
                                     .build())) {
            while (ci.hasNext()) {
                NotificationRecords records = ci.next().get();
                Event event = records.events().get(0);
                System.out.println(event.bucketName() + "/" + event.objectName() + " has been created");
            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e);
        }
    }


    @Test
    void test22() {
        String a = "cogent/jslkdfj/sdfjlje.txt";
        int index = a.indexOf("/");
        String substring = a.substring(index + 1, a.length());
        System.out.println(substring);
    }

}
