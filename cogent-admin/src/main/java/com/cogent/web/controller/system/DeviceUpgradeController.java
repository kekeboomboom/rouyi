package com.cogent.web.controller.system;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.compress.CompressUtil;
import cn.hutool.extra.compress.extractor.Extractor;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.annotation.Anonymous;
import com.cogent.common.constant.HttpStatus;
import com.cogent.common.core.controller.BaseController;
import com.cogent.common.core.domain.AjaxResult;
import com.cogent.common.core.page.TableDataInfo;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.HttpUtil;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.domain.DO.devUpgrade.DeviceUpgradeDO;
import com.cogent.system.domain.vo.devUpgrade.AddDevVerReq;
import com.cogent.system.domain.vo.devUpgrade.CheckUpgradeReq;
import com.cogent.system.domain.vo.devUpgrade.UpdateVerReq;
import com.cogent.system.service.IDeviceUpgradeService;
import io.minio.GetObjectArgs;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.Arrays;
import java.util.Map;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/16
 * {@code @description:}
 */
@Slf4j
@Anonymous
@Validated
@RestController
@RequestMapping("/device/version")
public class DeviceUpgradeController extends BaseController {

    private final IDeviceUpgradeService deviceUpgradeService;

    public DeviceUpgradeController(IDeviceUpgradeService deviceUpgradeService) {
        this.deviceUpgradeService = deviceUpgradeService;
    }

    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String versionNum,
                              @RequestParam(required = false) String devType) {
        startPage();
        TableDataInfo dataTable = getDataTable(deviceUpgradeService.list(versionNum, devType));
        dataTable.setTotal(deviceUpgradeService.total());
        return dataTable;
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(deviceUpgradeService.getInfo(id));
        jsonObject.remove("fileUrl");
        jsonObject.remove("uploadTime");
        return success(jsonObject);
    }

    @PostMapping
    public AjaxResult addVersion(@RequestBody @Valid AddDevVerReq req) {
        // 如果启用是false，那么强制更新一定为false。如果一个版本不启用，那就不可能强制更新
        if (!req.getEnable() && req.getForcedUpgrade()) {
            return error("如果是未启用状态，那么则不可能为强制更新");
        }
        deviceUpgradeService.addVersion(req);
        return success();
    }

    /**
     * 规定，一个归档文件比如叫 1.1.1.2023.tar 这个归档文件里面有两个文件一个叫：upgrade-info.yml 一个叫：1.1.1.2023.tar.gz
     * 这个归档文件放到 /device-upgrade-pack-repo/1.1.1.2023 文件夹，然后将归档文件在此目录解压，然后删除归档文件
     * 最终目录结构为：
     * /device-upgrade-pack-repo/1.1.1.2023
     * |--> 1.1.1.2023.tar.gz
     * |--> upgrade-info.yml
     * |--> 1.1.1.2023.tar
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return error("文件不能为空");
        }
        String filename = file.getOriginalFilename();
        // 文件大概名字为：1.1.1.2023.tar ，不要 .tar 后缀
        String versionNum = filename.substring(0, filename.length() - 4);
        // jar包的上一级目录
        String canonicalPath = FileUtil.getCanonicalPath(new File(".."));
        // 创建目录
        String directory = canonicalPath + "/device-upgrade-pack-repo/" + versionNum;
        FileUtil.mkdir(directory);
        // 将文件放到目录
        String filePath = directory + "/" + filename;
        try {
            // 此方法是，如果文件已经存在，那么会删除老的，然后用新的文件
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new ServiceException("文件上传失败");
        }
        Extractor extractor = CompressUtil.createExtractor(
                CharsetUtil.defaultCharset(),
                FileUtil.file(filePath));

        extractor.extract(FileUtil.file(directory));

        // 读取upgrade-info.yml 文件， 对比md5，如果不正确则返回错误，并删除目录
        Yaml yaml = new Yaml();
        BufferedInputStream inputStream = FileUtil.getInputStream(directory + "/upgrade-info.yml");
        Map<String, Object> obj = yaml.load(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new ServiceException("读取upgrade-info.yml异常，查看文件是否存在");
        }
        String md5 = (String) obj.get("md5");
        String version = (String) obj.get("versionNum");
        String devType = (String) obj.get("devType");
        // 校验md5值
        String digestHex = SecureUtil.md5().digestHex(FileUtil.file(filePath + ".gz"));

        // 如果md5值不相等
        if (!StringUtils.equals(md5, digestHex)) {
            log.info("md5 {} digestHex {}", md5, digestHex);
            // 删除目录，并且返回错误
            boolean del = FileUtil.del(directory);
            if (del) {
                return error("压缩包与yml文件中校验码不一致，请确认升级包是否正确");
            } else {
                return error("压缩包与yml文件中校验码不一致，且上传的文件删除失败，请联系系统管理员");
            }
        }
        // 返回upgrade-info.yml 中的信息
        JSONObject res = new JSONObject();
        res.put("versionNum", version);
        res.put("checkCode", md5);
        res.put("devType", devType);
        return success(res);
    }

    @PutMapping
    public AjaxResult updateVersion(@RequestBody @Valid UpdateVerReq req) {
        deviceUpgradeService.updateVersion(req);
        return success();
    }

    /**
     * 删除一个版本的升级包，同时删除数据库和相应的目录
     *
     * @param param
     * @return
     */
    @DeleteMapping
    public AjaxResult deleteVersion(@RequestBody Map<String, Object> param) {
        Integer id = (Integer) param.get("id");
        if (id == null) {
            return error("id 不能为空");
        }
        deviceUpgradeService.deleteVersion(id);
        return success();
    }

    /**
     * 版本号就是目录名字，因此删除目录即可
     *
     * @return
     */
    @PostMapping("/uploadCancel")
    public AjaxResult uploadCancel(@RequestBody Map<String, Object> param) {
        String versionNum = (String) param.get("versionNum");
        if (StringUtils.isEmpty(versionNum)) {
            return error("版本号不能为空");
        }
        // jar包的上一级目录
        String canonicalPath = FileUtil.getCanonicalPath(new File(".."));
        String directory = canonicalPath + "/device-upgrade-pack-repo/" + versionNum;
        // 删除目录
        FileUtil.del(directory);
        return success();
    }

    @SneakyThrows
    @GetMapping("/download")
    public void download(HttpServletResponse response,
                         @RequestParam String versionNum,
                         @RequestHeader(value = HttpHeaders.RANGE, required = false) String range) {
        if (StringUtils.isEmpty(versionNum)) {
            throw new ServiceException("版本号不能为空");
        }
        // jar包的上一级目录
        String canonicalPath = FileUtil.getCanonicalPath(new File(".."));
        String filePath = canonicalPath + "/device-upgrade-pack-repo/" + versionNum + "/" + versionNum + ".tar";
        long fileSize = FileUtil.file(filePath).length();

        // 获得文件流
        BufferedInputStream inputStream = FileUtil.getInputStream(filePath);

        // offset表示从哪个字节开始读取，length表示读取多少个字节
        long offset = 0;
        long length;
        // 如果range为空，那么就是读取整个文件
        if (StringUtils.isEmpty(range)) {
            length = fileSize;
        } else {
            // 根据range计算offset和length
            Pair<Long, Long> pair = getOffset(range);
            offset = pair.getLeft();
            length = pair.getRight();
            // length == -1 表示 end range 为最后一个字节
            if (length == -1) {
                length = fileSize - offset;
            }
            // 跳过文件的offset个字节
            inputStream.skip(offset);
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition",
                "attachment;filename=" + versionNum + ".tar");
        response.setHeader("Content-Length", String.valueOf(length));
        response.setHeader("Content-Range", "bytes" + " " +
                offset + "-" + (offset + length - 1) + "/" + fileSize);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        ServletOutputStream os = response.getOutputStream();

        byte[] buf = new byte[1024];
        int bytesRead;
        // sum 是当前读取的字节数
        int sum = 0;
        while (sum < length && (bytesRead = inputStream.read(buf, 0, (length - sum) <= buf.length ? (int) (length - sum) : buf.length)) != -1) {
            os.write(buf, 0, bytesRead);
            sum += bytesRead;
        }

        inputStream.close();
        os.close();
    }

    /**
     * 此接口是被设备调用。用来查询针对此设备是否有更新可用
     * 根据设备类型、设备当前版本号、是启用的，来返回一个版本、并返回是否强制更新
     * 如果没有符合条件的版本，返回状态码204
     *
     * @return
     */
    @PostMapping("/checkUpgrade")
    public AjaxResult checkUpgrade(@RequestBody CheckUpgradeReq req) {
        DeviceUpgradeDO latestVersion = deviceUpgradeService.checkUpgrade(req);
        if (latestVersion == null) {
            return new AjaxResult(HttpStatus.NO_CONTENT, "没有新版本可更新");
        }
        JSONObject resp = new JSONObject();
        resp.put("checkCode", latestVersion.getCheckCode());
        resp.put("description", latestVersion.getDescription());
        resp.put("forcedUpgrade", latestVersion.getForcedUpgrade());
        String versionNum = latestVersion.getVersionNum();
        resp.put("versionNum", versionNum);
        String canonicalPath = FileUtil.getCanonicalPath(new File(".."));
        String filePath = canonicalPath + "/device-upgrade-pack-repo/" + versionNum + "/" + versionNum + ".tar";
        long fileSize = FileUtil.file(filePath).length();
        resp.put("fileSize", fileSize);
        return success(resp);
    }

    /**
     * left is offset, right is length
     *
     * @param range
     * @return
     */
    private Pair<Long, Long> getOffset(String range) {
        if (!range.contains("bytes=") || !range.contains("-")) {
            throw new ServiceException("range 格式错误");
        }
        range = range.substring(range.lastIndexOf("=") + 1).trim();
        String[] split = StringUtils.split(range, "-");

        if (range.startsWith("-")) {
            return Pair.of(0L, Long.parseLong(split[0]) + 1);
        } else if (range.endsWith("-")) {
            return Pair.of(Long.parseLong(split[0]), -1L);
        } else {
            if (split.length != 2) {
                throw new ServiceException("range 格式错误");
            }
            return Pair.of(Long.parseLong(split[0]), Long.parseLong(split[1]) - Long.parseLong(split[0]) + 1);
        }
    }
}
