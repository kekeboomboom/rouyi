package com.cogent.system.service;

import com.cogent.system.domain.vo.callGroup.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/6/30
 * {@code @description:}
 */
public interface ICallGroupPaasService {
    List<JoinedGroupVO> getJoinedGroup(String memberNumber);

    PaasGroupInfoVO getPaasGroupInfo(Integer groupNumber);

    long getJoinedGroupCount(String memberNumber);

    void enterGroup(EnterGroupPaasReq req);

    boolean applyForSpeak(ApplySpeakReq req);

    void endTheSpeak(EndTheSpeakReq req);

    void exitGroup(ExitGroupPaasReq req);

    void putSseInGroupInfoMap(Integer groupNumber, SseEmitter emitter);
}
