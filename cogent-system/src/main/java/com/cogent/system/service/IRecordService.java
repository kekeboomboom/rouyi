package com.cogent.system.service;

import com.cogent.system.domain.vo.mediaHook.OnRecordMp4Req;
import com.cogent.system.domain.vo.mediaHook.StreamChangedReq;
import com.cogent.system.domain.vo.record.RecordSwitchReq;
import com.cogent.system.domain.vo.record.RecordVO;
import com.cogent.system.domain.vo.record.UpdateRecordReq;

import java.util.List;

public interface IRecordService {

    void startRecord(StreamChangedReq req);

    void openRecord(RecordSwitchReq req);

    void closeRecord(RecordSwitchReq req);

    void onRecordMp4(OnRecordMp4Req req);

    List<RecordVO> list(String sn, String name, Long startTime, Long endTime);

    void delete(List<Integer> id);

    long countRecord();

    void update(UpdateRecordReq req);
}
