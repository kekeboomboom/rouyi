package com.cogent.system.service;

import com.cogent.system.domain.vo.callGroup.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * @Author keboom
 * @Date 2023-06-26 14:14
 */
public interface ICallGroupService {

    void createCallGroup(CreateCallGroupReq req);

    void updateCallGroup(UpdateCallGroupReq req);

    void deleteCallGroup(Integer groupNumber);

    List<JoinedGroupVO> getJoinedGroup(String memberNumber, Integer groupNumber, String groupName);

    GroupInfoVO getGroupInfo(Integer groupNumber);

    long getJoinedGroupCount(String memberNumber);

    String enterGroup(EnterGroupReq req);

    boolean applyForSpeak(ApplySpeakReq req);

    void endSpeak(EndSpeakReq req);

    void exitGroup(ExitGroupReq req);

    List<MemberItemVO> getMemberList();

    void interruptCall(InterruptCallReq req);

    void specifySpeak(SpecifySpeakReq req);

    void putSseInJoinedGroupMap(String memberNumber, SseEmitter emitter);

    void putSseInGroupInfoMap(Integer groupNumber, SseEmitter emitter);
}
