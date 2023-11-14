package com.cogent.web.controller.system;

import com.cogent.common.annotation.Anonymous;
import com.cogent.common.core.controller.BaseController;
import com.cogent.common.core.domain.AjaxResult;
import com.cogent.common.core.page.TableDataInfo;
import com.cogent.system.domain.vo.record.DeleteRecordReq;
import com.cogent.system.domain.vo.record.RecordSwitchReq;
import com.cogent.system.domain.vo.record.RecordVO;
import com.cogent.system.domain.vo.record.UpdateRecordReq;
import com.cogent.system.service.IRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/10/23
 * {@code @description:}
 */
@Slf4j
@Validated
@Anonymous
@RestController
@RequestMapping("/record")
public class RecordController extends BaseController {

    @Resource
    private IRecordService recordService;


    /**
     * 开启录像，判断直播状态
     *
     * @param req
     * @return
     */
    @PostMapping("/switch")
    public AjaxResult switchRecord(@RequestBody @Valid RecordSwitchReq req) {
        if (req.getRecordSwitch()) {
            recordService.openRecord(req);
        } else {
            recordService.closeRecord(req);
        }
        return AjaxResult.success();
    }

    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String sn,
                              @RequestParam(required = false) String name,
                              @RequestParam(required = false) Long startTime,
                              @RequestParam(required = false) Long endTime) {
        startPage();
        List<RecordVO> list = recordService.list(sn,name, startTime, endTime);
        Long total = recordService.countRecord();
        return new TableDataInfo(list, total.intValue());
    }

    @DeleteMapping()
    public AjaxResult delete(@RequestBody @Valid DeleteRecordReq req) {
        recordService.delete(req.getId());
        return AjaxResult.success();
    }

    @PutMapping
    public AjaxResult update(@RequestBody @Valid UpdateRecordReq req) {
        recordService.update(req);
        return AjaxResult.success();
    }

}
