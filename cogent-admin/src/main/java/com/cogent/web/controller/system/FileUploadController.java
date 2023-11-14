package com.cogent.web.controller.system;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.annotation.Anonymous;
import com.cogent.common.core.controller.BaseController;
import com.cogent.common.core.domain.AjaxResult;
import com.cogent.common.core.page.TableDataInfo;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.domain.DO.fileUpload.MinioObjectDO;
import com.cogent.system.domain.vo.fileUpload.DeleteArgs;
import com.cogent.system.domain.vo.fileUpload.DeleteObjectReq;
import com.cogent.system.domain.vo.fileUpload.ListObjectVO;
import com.cogent.system.service.IFileUploadService;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * 文件上传的接口，存储使用minio
 * {@code @author:} keboom
 * {@code @date:} 2023/8/24
 * {@code @description:}
 */
@Slf4j
@Validated
@Anonymous
@RestController
@RequestMapping("/buckets")
public class FileUploadController extends BaseController {

    @Value("${minio.addr}")
    private String minioAddress;
    @Value("${minio.accessKey}")
    private String minioAccessKey;
    @Value("${minio.secretKey}")
    private String minioSecretKey;

    MinioClient minioClient;

    @Resource
    private IFileUploadService fileUploadService;

    @PostConstruct
    void init() {
        minioClient = MinioClient.builder()
                .endpoint("http://" + minioAddress)
                .credentials(minioAccessKey, minioSecretKey)
                .build();
    }

    /**
     * @param file
     * @param path
     * @return
     */
    @SneakyThrows
    @PostMapping("/{bucket}/objects/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file, @RequestParam(required = false, defaultValue = "") String path, @PathVariable String bucket) {
        if (file.isEmpty()) {
            return error("文件不能为空");
        }

        String objectName = path + file.getOriginalFilename();
        // check same name object. if it has same name object, then add number suffix.
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).prefix(objectName).build());
        int sameNameAmount = 0;
        for (Result<Item> ignored : results) {
            sameNameAmount++;
        }
        if (sameNameAmount > 0) {
            String[] split = StringUtils.split(objectName, ".");
            // if length greater than 1, we think object name has suffix.
            if (split.length > 1) {
                int endIndex = objectName.lastIndexOf(".");
                objectName = objectName.substring(0, endIndex) + "(" + sameNameAmount + ")" + "." + objectName.substring(endIndex + 1);
            }
        }

        InputStream initialStream = file.getInputStream();
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucket).object(objectName).stream(
                                initialStream, -1, 10485760)
                        .build());

        initialStream.close();
        log.info("upload file: {} successfully", path);

        return success();
    }

    @GetMapping("/{bucket}/objects/download")
    public void objectDownload(HttpServletResponse response,
                               @PathVariable String bucket,
                               @RequestParam String fullPathName,
                               @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) throws Exception {
        InputStream stream = null;
        // 支持分片下载
        if (StringUtils.isNotEmpty(range)) {
            if (!range.contains("bytes=") || !range.contains("-")) {
                throw new ServiceException("range 格式错误");
            }
            long offset = 0;
            long length = 0;
            Pair<Long, Long> pair = getOffset(range);
            offset = pair.getLeft();
            length = pair.getRight();
            if (length == -1) {
                stream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fullPathName)
                        .offset(offset).build());
            } else {
                stream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(fullPathName)
                        .offset(offset)
                        .length(length).build());
            }
        } else {
            stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fullPathName).build());
        }
        MinioObjectDO objectInfo = fileUploadService.getObjectInfo(bucket, fullPathName);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment;filename=" + URLEncoder.encode(getFileName(fullPathName), "UTF-8"));
        response.setHeader("Content-Length", String.valueOf(objectInfo.getSize()));
        response.setHeader("Content-Range", "bytes" + " " +
                0 + "-" + (objectInfo.getSize() - 1) + "/" + objectInfo.getSize());
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        byte[] buf = new byte[16384];
        int bytesRead;
        while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
            response.getOutputStream().write(buf, 0, bytesRead);
        }

        stream.close();
    }

    /**
     * left is offset, right is length
     *
     * @param range
     * @return
     */
    private Pair<Long, Long> getOffset(String range) {
        range = range.substring(range.lastIndexOf("=") + 1).trim();
        String[] split = range.split("-");
        if (split.length == 0 || split.length > 2) {
            throw new ServiceException("range 格式错误");
        }
        if (split.length == 1) {
            if (range.startsWith("-")) {
                return Pair.of(0L, Long.parseLong(split[0]) + 1);
            } else {
                // -1 表示length无限制
                return Pair.of(Long.parseLong(split[0]), -1L);
            }
        } else {
            return Pair.of(Long.parseLong(split[0]), Long.parseLong(split[1]) - Long.parseLong(split[0]) + 1);
        }
    }

    /**
     * 如果path为空，则获取的是根目录的文件和文件夹
     *
     * @param bucket
     * @param path
     * @return
     */
    @SneakyThrows
    @GetMapping("/{bucket}")
    public AjaxResult getDirectoryInfo(@PathVariable String bucket, @RequestParam(required = false, defaultValue = "") String path) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucket).prefix(path).build());
        JSONArray row = new JSONArray();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Shanghai"));
        for (Result<Item> result : results) {
            JSONObject rowItem = new JSONObject();
            Item item = result.get();
            String objectName = item.objectName();
            rowItem.put("name", objectName);
            // 如果是文件夹，那么没有修改日期和大小
            if (objectName.endsWith("/")) {
                rowItem.put("lastModified", "");
                rowItem.put("size", 0);
            } else {
                rowItem.put("lastModified", item.lastModified().format(formatter));
                rowItem.put("size", item.size());
            }
            row.add(rowItem);
        }
        return success(row);
    }

    @SneakyThrows
    @DeleteMapping("/{bucket}/delete-objects")
    public AjaxResult deleteObject(@PathVariable String bucket, @RequestBody @Valid DeleteObjectReq req) {
        List<DeleteObject> objects = new LinkedList<>();
        for (DeleteArgs deleteArg : req.getDeleteArgs()) {
            String path = deleteArg.getPath();
            // 如果是文件夹，那么应该先查询此文件夹下所有object，然后都添加到删除列表中，这样才能删除此文件夹
            if (path.endsWith("/")) {
                Iterable<Result<Item>> results = minioClient.listObjects(
                        ListObjectsArgs.builder().bucket(bucket).prefix(path).recursive(true).build());
                for (Result<Item> result : results) {
                    Item item = result.get();
                    objects.add(new DeleteObject(item.objectName()));
                }
            } else {
                objects.add(new DeleteObject(path));
            }
        }
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder().bucket(bucket).objects(objects).build());
        JSONArray res = new JSONArray();
        for (Result<DeleteError> result : results) {
            DeleteError error = result.get();
            JSONObject errorJson = new JSONObject();
            String objectName = error.objectName();
            String message = error.message();
            errorJson.put("objectName", objectName);
            errorJson.put("message", message);
            res.add(errorJson);
            log.error("Error in deleting object {} ; {}", objectName, message);
        }
        return success(res);
    }

    /**
     * 通过webhook，更新数据库object信息
     *
     * @param param
     * @return
     */
    @PostMapping("/event")
    public AjaxResult onEvent(@RequestBody String param) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        // 目前就两种，一种put，一种del
        String eventName = jsonObject.getString("EventName");
        String key = jsonObject.getString("Key");
        if (eventName.startsWith("s3:ObjectCreated")) {
            fileUploadService.putObject(key, jsonObject);
        } else if (eventName.startsWith("s3:ObjectRemoved")) {
            fileUploadService.deleteObject(key);
        }
        log.info("minio event report: {} {}", eventName, key);
        return success();
    }

    @GetMapping("/{bucket}/list")
    public TableDataInfo objectList(@PathVariable String bucket,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String device,
                                    @RequestParam(required = false) String format,
                                    @RequestParam(required = false) Long startTime,
                                    @RequestParam(required = false) Long endTime) {
        startPage();
        List<ListObjectVO> vos = fileUploadService.list(bucket, name, device, format, startTime, endTime);
        int total = fileUploadService.total(bucket, name, device, format, startTime, endTime);
        TableDataInfo tableDataInfo = new TableDataInfo(vos, total);
        tableDataInfo.setCode(200);
        tableDataInfo.setMsg("success");
        return tableDataInfo;
    }

    private String getFileName(String fullPathName) {
        String[] split = StringUtils.split(fullPathName, "/");
        return split[split.length - 1];
    }
}
