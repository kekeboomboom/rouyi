package com.cogent.system.service;

import com.cogent.system.domain.vo.mobicaster.*;

public interface IMobicasterService {

    int live(LiveReq req);

    void deviceInfo(DeviceInfoReq req);

    void statusReport(AndroidStatusReq req);

    void offlineReport(OfflineReportReq req);

    AppInfo getDeviceInfo(Integer id);

    void updateApp(UpdateAppReq req);

    void appLive(AppLiveReq req);

    boolean getLiveState(String androidID);

    void preview(PreviewReq req);
}
