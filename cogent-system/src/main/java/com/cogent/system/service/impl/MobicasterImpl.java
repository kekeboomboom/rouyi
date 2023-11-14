package com.cogent.system.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cogent.common.exception.ServiceException;
import com.cogent.common.utils.HttpUtil;
import com.cogent.common.utils.StringUtils;
import com.cogent.system.common.DevType;
import com.cogent.system.common.LivePortPoolUtil;
import com.cogent.system.dao.*;
import com.cogent.system.domain.DO.bag.BagDO;
import com.cogent.system.domain.DO.bag.MediaCurrentCfgDO;
import com.cogent.system.domain.DO.bag.MediaCurrentInfoDO;
import com.cogent.system.domain.DO.route.DestDO;
import com.cogent.system.domain.DO.route.RouteDestRelDO;
import com.cogent.system.domain.DO.route.SourceDO;
import com.cogent.system.domain.Route;
import com.cogent.system.domain.vo.SRTMore;
import com.cogent.system.domain.vo.mobicaster.*;
import com.cogent.system.mapper.*;
import com.cogent.system.service.IMobicasterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/22
 * {@code @description:}
 */
@Slf4j
@Service
public class MobicasterImpl implements IMobicasterService {

    @Autowired
    private LivePortPoolUtil livePortPoolUtil;
    @Autowired
    private RouteServiceImpl routeService;
    @Resource
    private SourceMapper sourceMapper;
    @Resource
    private SourceDao sourceDao;
    @Resource
    private RouteMapper routeMapper;
    @Resource
    private RouteDao routeDao;
    @Resource
    private DestMapper destMapper;
    @Resource
    private BagMapper bagMapper;
    @Resource
    private MediaCurrentInfoMapper mediaCurrentInfoMapper;
    @Resource
    private MediaCurInfoDao mediaCurInfoDao;
    @Value("${mobicaster_url}")
    private String mobicasterUrl;
    @Resource
    private RouteDestRelDao routeDestRelDao;
    @Resource
    private DestDao destDao;

    /**
     * 如果是开启直播，那么创建路由，如果是关闭直播，那么删除路由
     * 关于分配端口，在redis中维护一个端口池，开启直播分配一个端口，关闭直播释放端口。
     * 这个端口池，是一个redis set，这个集合中存储已经分配的端口。
     * <p>
     * Java 程序重启后，所有端口都释放。
     *
     * @param req
     */
    @Override
    public int live(LiveReq req) {
        String sn = req.getAndroidID();
        boolean liveEnable = req.getLiveAction();

        // 如果有路由，先删除一次，然后再去smh创建一个新路由。因为关闭直播是关闭路由而不是删除路由，
        // 因此开启直播时，要先删除之前存在的路由，然后再去创建路由。为什么关闭直播不删除路由？因为用户想在关闭直播后仍然能看到路由，并修改路由
        if (liveEnable) {
            Route route = routeDao.selectByRouteName(sn);
            // 因为在关闭直播时，路由已经被关闭，因此在这里删除路由，原需求是关闭直播时，删除路由。
            if (route != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("src_uuid", route.getUuid());
                jsonObject.put("key", HttpUtil.apiKey);
                jsonObject.put("start", "false");
                JSONObject resp = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/source/switch", jsonObject);
                // 如果路由状态是开启的，那么就释放端口
                if (resp.getString("states").equals("success") && route.getStartStop()) {
                    // 释放端口
                    SourceDO sourceDO = sourceMapper.selectById(route.getSourceId());
                    int port = sourceDO.getPort();
                    livePortPoolUtil.releasePort(port);
                }

                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("src_uuid", route.getUuid());
                jsonObject2.put("key", HttpUtil.apiKey);
                JSONObject respDel = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/edit/deltask", jsonObject2);
            }
        }

        // 如果是开启直播，第一次开启那么smh创建路由，新增数据库，第二次开启则关闭路由改变源端口，开启路由
        // 如果是关闭直播，仅仅关闭路由
        if (liveEnable) {
            // 分配端口
            int port = livePortPoolUtil.getPort();
            if (port == -1) {
                throw new ServiceException("端口已经用完，无法开启直播");
            }

            // 先构建默认的路由，源，目的地
            SourceDO sourceDO = new SourceDO();
            sourceDO.setSourceName(sn);
            sourceDO.setProtocol("srt");
            sourceDO.setProtocolType("listener");
            sourceDO.setAddr("null");
            sourceDO.setPort(port);
            sourceDO.setSrtMore("{\"bandwidth\":25,\"delay\":125,\"encryption\":\"none\",\"mtu\":1496,\"password\":\"null\",\"tos\":0,\"ttl\":64}");
            sourceDO.setSourceState(2);

            // 这个目的地默认是用来做预览的, 国标。
            DestDO destDO = new DestDO();
            destDO.setDestName("default-preview-srt" + sn);
            destDO.setProtocol("srt");
            destDO.setProtocolType("caller");
            destDO.setAddr(HttpUtil.HOST_IP);
            destDO.setPort(9000);
            destDO.setSrtMore("{\"bandwidth\":25,\"delay\":125,\"encryption\":\"none\",\"mtu\":1496,\"password\":\"null\",\"tos\":0,\"ttl\":64}");
            destDO.setDestUuid("");
            destDO.setStartStop(true);
            destDO.setSrcUuid("");
            destDO.setDestState(2);
            destDO.setIsSdi(false);

            Route route = new Route();
            route.setRouteName(sn);
            route.setStartStop(true);

            Route selectByRouteName = routeDao.selectByRouteName(sn);
            JSONObject resp2 = routeService.SMHCreateRoute(sn, sourceDO);
            if (!Objects.equals(resp2.getString("states"), "success")) {
                throw new ServiceException("SMH创建路由失败" + resp2.toString());
            }
            String srcUuid = resp2.getString("uuid");
            route.setUuid(srcUuid);

            // 如果有路由，那么就根据路由去创建就行了，注意source的port已经改变
            if (ObjectUtils.isNotEmpty(selectByRouteName)) {
                sourceMapper.insertSource(sourceDO);
                if (StringUtils.isNotEmpty(selectByRouteName.getDestIds())) {
                    List<String> destList = Arrays.stream(StringUtils.split(selectByRouteName.getDestIds(), ",")).collect(Collectors.toList());
                    // smh创建用户自定义的目的地
                    if (CollectionUtils.isNotEmpty(destList)) {
                        for (String destId : destList) {
                            DestDO destDO1 = destMapper.selectById(Integer.valueOf(destId));
                            String destDO1UUID = addSRTDestToSMH(srcUuid, sn, destDO1);
                            if (StringUtils.isEmpty(destDO1UUID)) {
                                throw new ServiceException("SMH添加目的地失败");
                            }
                            destDO1.setDestUuid(destDO1UUID);
                            destDO1.setSrcUuid(srcUuid);
                            destDO1.setDestState(2);
                            // 更新 uuid
                            destMapper.insertDest(destDO1);
                        }
                    }
                }
                routeMapper.insertRoute(route);
            } else {
                destDO.setSrcUuid(srcUuid);
                String destUUID = addSRTDestToSMH(srcUuid, sn, destDO);
                if (StringUtils.isEmpty(destUUID)) {
                    throw new ServiceException("SMH添加目的地失败");
                }
                destDO.setDestUuid(destUUID);
                destMapper.insertDest(destDO);
                sourceMapper.insertSource(sourceDO);
                // 如果是更新，那么id是不会设置到source 和 dest的
                route.setSourceId(sourceDao.selectByName(sn).getId());
                Long destId = destDao.selectByName(destDO.getDestName()).getId();
                route.setDestIds(String.valueOf(destId));
                routeMapper.insertRoute(route);
                Integer routeId = route.getId();
                if (routeId != null) {
                    routeDestRelDao.save(new RouteDestRelDO(routeId, destId.intValue()));
                } else {
                    routeDestRelDao.save(new RouteDestRelDO(routeDao.selectByRouteName(route.getRouteName()).getId(), destId.intValue()));
                }
            }
            return port;
        } else {
            closeBagLive(sn);
            return -1;
        }
    }

    /**
     * 手机每次上线都会上报一次
     *
     * @param req
     */
    @Override
    public void deviceInfo(DeviceInfoReq req) {
        BagDO bagBySN = bagMapper.getBagBySN(req.getAndroidID());
        if (bagBySN == null) {
            BagDO bagDO = new BagDO();
            bagDO.setSn(req.getAndroidID());
            bagDO.setDevName(req.getAndroidID());
            bagDO.setDevType(DevType.APP.getName());
            bagDO.setAndroidVersion(req.getAndroidVersion());
            bagDO.setState("online");
            bagMapper.insertBag(bagDO);
        } else {
            bagBySN.setDevType(DevType.APP.getName());
            bagBySN.setAndroidVersion(req.getAndroidVersion());
            bagBySN.setState("online");
            bagMapper.updateBag(bagBySN);
        }

        // 如果设备离线了，那么就不知道设备直播状态了，那么在设备上线的时候，将设备直播状态初始化为false
        LambdaUpdateWrapper<MediaCurrentInfoDO> update = Wrappers.lambdaUpdate();
        update.eq(MediaCurrentInfoDO::getSn, req.getAndroidID());
        update.set(MediaCurrentInfoDO::getStaLiveState, 0);
        mediaCurrentInfoMapper.update(null, update);
    }

    private void closeBagLive(String sn) {
        Route route = routeDao.selectByRouteName(sn);
        if (route != null) {
            switchRoute(route.getId(), false);
        }
    }

    public int switchRoute(int routeId, boolean startStop) {
        Route route = routeMapper.selectRouteById(routeId);

//        if (getLiveState(route.getRouteName()) && !startStop) {
//            throw new ServiceException(route.getRouteName() + " is living. Route cannot switch.");
//        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("src_uuid", route.getUuid());
        jsonObject.put("key", HttpUtil.apiKey);
        jsonObject.put("start", String.valueOf(startStop));

        JSONObject resp = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/source/switch", jsonObject);


        int res = routeMapper.switchRoute(routeId, startStop);
        // 切换状态，同时要更新源的state
        SourceDO sourceDO = new SourceDO();
        sourceDO.setId(route.getSourceId());
        sourceDO.setSourceState(startStop ? 2 : 1);
        sourceMapper.updateSource(sourceDO);

        // 释放端口
        int port = sourceMapper.selectById(route.getSourceId()).getPort();
        livePortPoolUtil.releasePort(port);

        // 路由启停，更新目的地state
        if (StringUtils.isEmpty(route.getDestIds())) {
            return res;
        }
        List<Long> dests = Arrays.stream(StringUtils.split(route.getDestIds(), ",")).map(Long::parseLong).collect(Collectors.toList());
        for (Long destId : dests) {
            DestDO destDO = new DestDO();
            destDO.setId(destId);
            if (startStop) {
                destDO.setDestState(2);
            } else {
                destDO.setDestState(1);
            }
            destMapper.updateDest(destDO);
        }
        return res;
    }

    private String addSRTDestToSMH(String srcUuid, String sn, DestDO data) {
        // 老的SMH接口创建新路由的时候必须填写src_uuid，而且创建成功后，不返回dest_uuid，因此还需我我们再去查询。
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("src_uuid", srcUuid);
        jsonObject.put("destname", data.getDestName());
        jsonObject.put("protocol", data.getProtocol());
        jsonObject.put("start", data.getStartStop().toString());
        jsonObject.put("srtmode", data.getSrtMore().equals("null") || StringUtils.isEmpty(data.getSrtMore()) ? "listener" : data.getProtocolType());
        jsonObject.put("addr", data.getAddr());
        jsonObject.put("port", data.getPort());
        // 老界面可以设置网卡，新接口没有这个字段
        jsonObject.put("interface", "auto");
        if (data.getProtocol().equals("srt") && StringUtils.isEmpty(data.getSrtMore())) {
            SRTMore srtMore = JSONObject.parseObject(data.getSrtMore(), SRTMore.class);
            jsonObject.put("latency", srtMore.getDelay());
            jsonObject.put("encryption", srtMore.getEncryption());
            jsonObject.put("passphrase", srtMore.getPassword());
            jsonObject.put("bandover", srtMore.getBandwidth());
            jsonObject.put("ttl", srtMore.getTtl());
            jsonObject.put("tos", srtMore.getTos());
            jsonObject.put("mtu", srtMore.getMtu());
        } else {
            // 如果不是srt，那么赋默认值
            jsonObject.put("latency", 125);
            jsonObject.put("encryption", "none");
            jsonObject.put("passphrase", "null");
            jsonObject.put("bandover", 25);
            jsonObject.put("ttl", 64);
            jsonObject.put("tos", 0);
            jsonObject.put("mtu", 1496);
        }
        jsonObject.put("rate", 0);
        jsonObject.put("status", 0);
        jsonObject.put("key", HttpUtil.apiKey);
        jsonObject.put("streamid", "#!::r=live/" + sn + ",m=publish");

        JSONObject resp2 = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/edit/adddest", jsonObject);
        if (resp2.getString("states").equals("failed")) {
            throw new ServiceException("SMH添加目的地失败" + resp2.getString("message"));
        }

        return destTableUuidFromSMH(srcUuid, data.getDestName());
    }

    private String destTableUuidFromSMH(String srcUuid, String destName) {
        HashMap<String, String> param = new HashMap<>();
        param.put("src_uuid", srcUuid);
        JSONObject resp;
        try {
            resp = HttpUtil.getSMHRequest(HttpUtil.SMH_BASE_URL + "/route/query/dests", param);
        } catch (Exception e) {
            throw new ServiceException("SMH查询目的地失败" + e.getMessage());
        }

        // 根据destname，拿到相应的dest_uuid
        JSONArray dests = resp.getJSONArray("dests");
        for (int i = 0; i < dests.size(); i++) {
            JSONObject jsonObject3 = dests.getJSONObject(i);
            if (StringUtils.equals(jsonObject3.getString("destname"), destName)) {
                return jsonObject3.getString("dest_uuid");
            }
        }
        return "";
    }

    @Override
    public void statusReport(AndroidStatusReq req) {
        MediaCurrentInfoDO mediaCurrentInfoDO = mediaCurInfoDao.selectBySn(req.getAndroidID());
        if (mediaCurrentInfoDO == null) {
            MediaCurrentInfoDO mediaCurrentInfoDO1 = new MediaCurrentInfoDO();
            mediaCurrentInfoDO1.setSn(req.getAndroidID());
            mediaCurrentInfoDO1.setStaLiveState(req.getLiveStatus());
            mediaCurrentInfoDO1.setStaFeedbackState(req.getFoldBackStatus() ? 2 : 1);
            mediaCurrentInfoMapper.insert(mediaCurrentInfoDO1);
        } else {
            LambdaUpdateWrapper<MediaCurrentInfoDO> update = Wrappers.lambdaUpdate();
            update.eq(MediaCurrentInfoDO::getSn, req.getAndroidID());
            update.set(MediaCurrentInfoDO::getStaLiveState, req.getLiveStatus());
            update.set(MediaCurrentInfoDO::getStaFeedbackState, req.getFoldBackStatus() ? 2 : 1);
            mediaCurrentInfoMapper.update(null, update);
        }
    }

    @Override
    public void offlineReport(OfflineReportReq req) {
        // close live and close foldback
        closeBagLive(req.getAndroidId());
        LambdaUpdateWrapper<MediaCurrentInfoDO> update = Wrappers.lambdaUpdate();
        update.eq(MediaCurrentInfoDO::getSn, req.getAndroidId());
        update.set(MediaCurrentInfoDO::getStaLiveState, 0);
        update.set(MediaCurrentInfoDO::getStaFeedbackState, 0);
        mediaCurrentInfoMapper.update(null, update);

        // close foldback
        BagDO bagBySN = bagMapper.getBagBySN(req.getAndroidId());
        if (bagBySN == null) {
            log.error("{} device not exist", req.getAndroidId());
            return;
        }
        bagBySN.setFoldbackState(false);
        bagBySN.setFoldbackStreamId("");
        // update bag state offline
        bagBySN.setState("offline");
        bagMapper.updateBag(bagBySN);

        // 删除路由
        Route route = routeDao.selectByRouteName(req.getAndroidId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("src_uuid", route.getUuid());
        jsonObject.put("key", HttpUtil.apiKey);
        JSONObject resp = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/edit/deltask", jsonObject);
        // 删除路由同时，把route表的uuid设置为空，还有source的state设置为3，以此来表示这个源和路由在smh中不存在
        route.setUuid("");
        routeDao.updateById(route);
        SourceDO sourceDO = new SourceDO();
        sourceDO.setSourceName(req.getAndroidId());
        sourceDO.setSourceState(3);
        sourceDao.updateByName(sourceDO);
    }

    @Override
    public AppInfo getDeviceInfo(Integer id) {
        BagDO bagById = bagMapper.getBagById(id);

        if (bagById == null) {
            throw new ServiceException("设备id不存在");
        }
        JSONObject resp = HttpUtil.getRequest(mobicasterUrl + "/deviceInfo/" + bagById.getSn(), Collections.emptyMap());
        if (resp.getInteger("code") != 200) {
            throw new ServiceException("获取设备信息失败");
        }

        MediaCurrentInfoDO mediaCurrentInfoDO = mediaCurInfoDao.selectBySn(bagById.getSn());

        AppInfo appInfo = new AppInfo();
        AppMedia appMedia = JSONObject.parseObject(resp.getString("data"), AppMedia.class);
        AppBase appBase = new AppBase();
        appBase.setAlias(bagById.getDevName());
        appBase.setFoldbackState(mediaCurrentInfoDO.getStaFeedbackState());
        appBase.setFoldbackStateSwitch(bagById.getFoldbackState() ? 1 : 0);
        appBase.setId((long) bagById.getId());
        appBase.setLiveState(mediaCurrentInfoDO.getStaLiveState());
        appBase.setSn(bagById.getSn());
        appBase.setState(bagById.getState());
        appBase.setType(bagById.getDevType());
        appInfo.setBase(appBase);
        appInfo.setMedia(appMedia);
        return appInfo;
    }

    @Override
    public void updateApp(UpdateAppReq req) {
        BagDO bagById = bagMapper.getBagById(req.getId());
        if (bagById == null || !bagById.getSn().equals(req.getSn())) {
            throw new ServiceException("设备id不存在");
        }
        if (req.getDevice().getBase() != null && StringUtils.isNotEmpty(req.getDevice().getBase().getAlias())) {
            bagById.setDevName(req.getDevice().getBase().getAlias());
            bagMapper.updateBag(bagById);
        }
        JSONObject resp = HttpUtil.postRequest(mobicasterUrl + "/modifySettings", JSONObject.from(req));
        if (resp.getInteger("code") != 200) {
            throw new ServiceException("修改设备信息失败");
        }
    }

    @Override
    public void appLive(AppLiveReq req) {
        String requestId = UUID.randomUUID().toString();
        req.setRequestId(requestId);
        BagDO bag = bagMapper.getBagBySN(req.getAndroidID());
        if (bag.getState().equals("offline")) {
            throw new ServiceException("设备离线了，不可直播");
        }
        String sn = req.getAndroidID();
        boolean liveEnable = req.getLive();

        // 如果有路由，先删除一次，然后再去smh创建一个新路由。因为关闭直播是关闭路由而不是删除路由，
        // 因此开启直播时，要先删除之前存在的路由，然后再去创建路由。为什么关闭直播不删除路由？因为用户想在关闭直播后仍然能看到路由，并修改路由
        if (liveEnable) {
            Route route = routeDao.selectByRouteName(sn);
            // 因为在关闭直播时，路由已经被关闭，因此在这里删除路由，原需求是关闭直播时，删除路由。
            if (route != null) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("src_uuid", route.getUuid());
                jsonObject.put("key", HttpUtil.apiKey);
                jsonObject.put("start", "false");
                JSONObject resp = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/source/switch", jsonObject);
                // 如果路由状态是开启的，那么就释放端口
                if (resp.getString("states").equals("success") && route.getStartStop()) {
                    // 释放端口
                    SourceDO sourceDO = sourceMapper.selectById(route.getSourceId());
                    int port = sourceDO.getPort();
                    livePortPoolUtil.releasePort(port);
                }

                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("src_uuid", route.getUuid());
                jsonObject2.put("key", HttpUtil.apiKey);
                JSONObject respDel = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/edit/deltask", jsonObject2);
            }
        }

        // 如果是开启直播，第一次开启那么smh创建路由，新增数据库，第二次开启则关闭路由改变源端口，开启路由
        // 如果是关闭直播，仅仅关闭路由
        if (liveEnable) {
            // 分配端口
            int port = livePortPoolUtil.getPort();
            if (port == -1) {
                throw new ServiceException("端口已经用完，无法开启直播");
            }

            // 先构建默认的路由，源，目的地
            SourceDO sourceDO = new SourceDO();
            sourceDO.setSourceName(sn);
            sourceDO.setProtocol("srt");
            sourceDO.setProtocolType("listener");
            sourceDO.setAddr("null");
            sourceDO.setPort(port);
            sourceDO.setSrtMore("{\"bandwidth\":25,\"delay\":125,\"encryption\":\"none\",\"mtu\":1496,\"password\":\"null\",\"tos\":0,\"ttl\":64}");
            sourceDO.setSourceState(2);

            // 这个目的地默认是用来做预览的, 国标。
            DestDO destDO = new DestDO();
            destDO.setDestName("default-preview-srt" + sn);
            destDO.setProtocol("srt");
            destDO.setProtocolType("caller");
            destDO.setAddr(HttpUtil.HOST_IP);
            destDO.setPort(9000);
            destDO.setSrtMore("{\"bandwidth\":25,\"delay\":125,\"encryption\":\"none\",\"mtu\":1496,\"password\":\"null\",\"tos\":0,\"ttl\":64}");
            destDO.setDestUuid("");
            destDO.setStartStop(true);
            destDO.setSrcUuid("");
            destDO.setDestState(2);
            destDO.setIsSdi(false);

            Route route = new Route();
            route.setRouteName(sn);
            route.setStartStop(true);

            Route selectByRouteName = routeDao.selectByRouteName(sn);
            JSONObject resp2 = routeService.SMHCreateRoute(sn, sourceDO);
            if (!Objects.equals(resp2.getString("states"), "success")) {
                throw new ServiceException("SMH创建路由失败" + resp2.toString());
            }
            String srcUuid = resp2.getString("uuid");
            route.setUuid(srcUuid);

            // 如果有路由，那么就根据路由去创建就行了，注意source的port已经改变
            if (ObjectUtils.isNotEmpty(selectByRouteName)) {
                sourceMapper.insertSource(sourceDO);
                if (StringUtils.isNotEmpty(selectByRouteName.getDestIds())) {
                    List<String> destList = Arrays.stream(StringUtils.split(selectByRouteName.getDestIds(), ",")).collect(Collectors.toList());
                    // smh创建用户自定义的目的地
                    if (CollectionUtils.isNotEmpty(destList)) {
                        for (String destId : destList) {
                            DestDO destDO1 = destMapper.selectById(Integer.valueOf(destId));
                            String destDO1UUID = addSRTDestToSMH(srcUuid, sn, destDO1);
                            if (StringUtils.isEmpty(destDO1UUID)) {
                                throw new ServiceException("SMH添加目的地失败");
                            }
                            destDO1.setDestUuid(destDO1UUID);
                            destDO1.setSrcUuid(srcUuid);
                            destDO1.setDestState(2);
                            // 更新 uuid
                            destMapper.insertDest(destDO1);
                        }
                    }
                }
                routeMapper.insertRoute(route);
            } else {
                destDO.setSrcUuid(srcUuid);

                String destUUID = addSRTDestToSMH(srcUuid, sn, destDO);
                if (StringUtils.isEmpty(destUUID)) {
                    throw new ServiceException("SMH添加目的地失败");
                }
                destDO.setDestUuid(destUUID);
                destMapper.insertDest(destDO);
                sourceMapper.insertSource(sourceDO);
                // 如果是更新，那么id是不会设置到source 和 dest的
                route.setSourceId(sourceDao.selectByName(sn).getId());
                Long destId = destDao.selectByName(destDO.getDestName()).getId();
                route.setDestIds(String.valueOf(destId));
                routeMapper.insertRoute(route);
                Integer routeId = route.getId();
                if (routeId != null) {
                    routeDestRelDao.save(new RouteDestRelDO(routeId, destId.intValue()));
                } else {
                    routeDestRelDao.save(new RouteDestRelDO(routeDao.selectByRouteName(route.getRouteName()).getId(), destId.intValue()));
                }
            }
            req.setIp(HttpUtil.HOST_IP);
            req.setPort(port);
            JSONObject resp = HttpUtil.postRequest(mobicasterUrl + "/appLive", JSONObject.from(req));
            if (resp.getString("code").equals("500")) {
                closeBagLive(sn);
                throw new ServiceException("开启直播失败 " + resp.getString("msg"));
            }

        } else {
            req.setIp("");
            req.setPort(0);
            JSONObject resp = HttpUtil.postRequest(mobicasterUrl + "/appLive", JSONObject.from(req));
            // 无论安卓是否关闭成功，我这边都关闭直播
            closeBagLive(sn);
        }

    }

    @Override
    public boolean getLiveState(String androidID) {
        LambdaQueryWrapper<MediaCurrentInfoDO> query = Wrappers.lambdaQuery();
        query.eq(MediaCurrentInfoDO::getSn, androidID);
        MediaCurrentInfoDO mediaCurrentInfoDO = mediaCurrentInfoMapper.selectOne(query);
        if (mediaCurrentInfoDO == null) {
            return false;
        } else {
            // 0 未准备 1 已准备好 2 直播中 3 直播异常
            return mediaCurrentInfoDO.getStaLiveState() == 2;
        }
    }

    @Override
    public void preview(PreviewReq req) {
        String sn = req.getAndroidID();
        Boolean previewEnable = req.getPreviewEnable();
        String streamAppName = req.getStreamAppName();
        // 规定，这个是专门给预览的目的地名字。
        String destName = "default-preview-udp" + sn;
        LambdaQueryWrapper<DestDO> queryDestByName = Wrappers.lambdaQuery();
        queryDestByName.eq(DestDO::getDestName, destName);
        DestDO destByName = destMapper.selectOne(queryDestByName);
        // 第一次开启预览就新建目的地，第二次重复开启预览，就更新
        if (previewEnable) {
            // 重复开启预览。如果这个预览有其他人在用，那么就应该直接return
            JSONObject mediaResp = HttpUtil.getMediaKitRequest(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/getMediaList", Collections.emptyMap());
            // 从数据中找到 app名字为transcoding，schema为rtsp的。如果他是readerCount大于0，那么就说明已经有人开启了预览了，则直接return
            // 如果data为空，那么就没人开预览
            if (ObjectUtils.isNotEmpty(mediaResp.getJSONArray("data"))) {
                JSONArray array = mediaResp.getJSONArray("data");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject dataItem = array.getJSONObject(i);
                    if (dataItem.getString("app").equals("transcoding")
                            && dataItem.getString("schema").equals("rtsp")
                            && dataItem.getInteger("readerCount") > 0) {
                        return;
                    }
                }
            }

            LambdaQueryWrapper<Route> queryRouteByName = Wrappers.lambdaQuery();
            queryRouteByName.eq(Route::getRouteName, sn);
            Route route = routeMapper.selectOne(queryRouteByName);
            if (ObjectUtils.isEmpty(route)) {
                throw new ServiceException("路由已经被删除，无法开启预览");
            }

            JSONObject requestBody = new JSONObject();
            requestBody.put("sn", sn);
            requestBody.put("rate", 2000);
            requestBody.put("width", 1280);
            requestBody.put("height", 720);
            requestBody.put("streamAppName", streamAppName);
            JSONObject resp = HttpUtil.postRequest(HttpUtil.TRANSCODE_BASE_URL + "/v1/decklink/startTranscode", requestBody);
            Integer port = resp.getInteger("port");

            // 开启直播，开启预览，关闭直播，开启直播，此时调用预览接口会报201，但此时路由中没有此目的地，因此这个201状态码就不去用！！！
            // 设备断流等情况
            if (resp.getInteger("code") == 201) {
                JSONObject requestBody2 = new JSONObject();
                requestBody2.put("sn", sn);
                JSONObject resp2 = HttpUtil.postRequest(HttpUtil.TRANSCODE_BASE_URL + "/v1/decklink/stopTranscode", requestBody2);

                JSONObject requestBody3 = new JSONObject();
                requestBody3.put("sn", sn);
                requestBody3.put("rate", 2000);
                requestBody3.put("width", 1280);
                requestBody3.put("height", 720);
                requestBody3.put("streamAppName", streamAppName);
                JSONObject resp3 = HttpUtil.postRequest(HttpUtil.TRANSCODE_BASE_URL + "/v1/decklink/startTranscode", requestBody3);
                port = resp3.getInteger("port");
            }

            if (resp.getInteger("code") == 500) {
                throw new ServiceException("开启转码失败" + resp.toString());
            }


            if (ObjectUtils.isEmpty(destByName)) {
                DestDO destDO2 = new DestDO();
                destDO2.setDestName("default-preview-udp" + sn);
                destDO2.setProtocol("udp");
                destDO2.setProtocolType("listener");
                destDO2.setAddr("127.0.0.1");
                destDO2.setPort(port);
                destDO2.setSrtMore("");
                destDO2.setStartStop(true);
                destDO2.setSrcUuid(route.getUuid());
                destDO2.setDestState(2);
                destDO2.setIsSdi(false);

                // SMH create destination
                String destUUID = addDestToSMH(destDO2, route.getUuid());
                destDO2.setDestUuid(destUUID);

                destMapper.insertDest(destDO2);

                // because open live automatically create destination. so we don't worry about "route.getDestIds" is empty string
                if (StringUtils.isEmpty(route.getDestIds())) {
                    route.setDestIds(destDO2.getId().toString());
                } else {
                    route.setDestIds(route.getDestIds() + "," + destDO2.getId());
                }
                routeDestRelDao.save(new RouteDestRelDO(route.getId(), destDO2.getId().intValue()));
                routeMapper.updateRoute(route);

            }
        } else {
            if (ObjectUtils.isEmpty(destByName)) {
                return;
            }
            // 停止预览。有可能多个用户同时预览，其中一个用户关闭预览，不能影响其他用户也关闭预览
            JSONObject mediaResp = HttpUtil.getMediaKitRequest(HttpUtil.MEDIAKIT_BASE_URL + "/index/api/getMediaList", Collections.emptyMap());
            // 从数据中找到 app名字为transcoding，schema为rtsp的。如果他是readerCount大于0，那么就说明已经有人开启了预览了
            // 如果data为空，那么就没人开预览
            if (ObjectUtils.isNotEmpty(mediaResp.getJSONArray("data"))) {
                JSONArray array = mediaResp.getJSONArray("data");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject dataItem = array.getJSONObject(i);
                    if (dataItem.getString("app").equals("transcoding")
                            && dataItem.getString("schema").equals("rtsp")
                            && dataItem.getInteger("readerCount") > 1) {
                        return;
                    }
                }
            } else {
                // 如果data为空，则说明没有开过预览，再次关闭预览，不做任何处理
                return;
            }
            closeTranscodingAndDeleteDest(sn, destByName);
        }
    }

    private String addDestToSMH(DestDO data, String srcUuid) {
        // 老的SMH接口创建新路由的时候必须填写src_uuid，而且创建成功后，不返回dest_uuid，因此还需我我们再去查询。
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("src_uuid", srcUuid);
        jsonObject.put("destname", data.getDestName());
        jsonObject.put("protocol", data.getProtocol());
        jsonObject.put("start", "true");
        jsonObject.put("srtmode", data.getSrtMore().equals("null") || StringUtils.isEmpty(data.getSrtMore()) ? "listener" : data.getProtocolType());
        jsonObject.put("addr", data.getAddr());
        jsonObject.put("port", data.getPort());
        // 老界面可以设置网卡，新接口没有这个字段
        jsonObject.put("interface", "auto");
        if (data.getProtocol().equals("srt")) {
            SRTMore srtMore = JSONObject.parseObject(data.getSrtMore(), SRTMore.class);
            jsonObject.put("latency", srtMore.getDelay());
            jsonObject.put("encryption", srtMore.getEncryption());
            jsonObject.put("passphrase", srtMore.getPassword());
            jsonObject.put("bandover", srtMore.getBandwidth());
            jsonObject.put("ttl", srtMore.getTtl());
            jsonObject.put("tos", srtMore.getTos());
            jsonObject.put("mtu", srtMore.getMtu());
        } else {
            // 如果不是srt，那么赋默认值
            jsonObject.put("latency", 125);
            jsonObject.put("encryption", "none");
            jsonObject.put("passphrase", "null");
            jsonObject.put("bandover", 25);
            jsonObject.put("ttl", 64);
            jsonObject.put("tos", 0);
            jsonObject.put("mtu", 1496);
        }
        jsonObject.put("rate", 0);
        jsonObject.put("status", 0);
        jsonObject.put("key", HttpUtil.apiKey);
        String s = UUID.randomUUID().toString();
        jsonObject.put("streamid", s.substring(0, 8));

        try {
            JSONObject resp = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/edit/adddest", jsonObject);
            if (resp.getString("states").equals("failed")) {
                throw new ServiceException("SMH添加目的地失败" + resp.getString("message"));
            }
        } catch (Exception e) {
            throw new ServiceException("SMH添加目的地失败" + e.getMessage());
        }

        String destUUID = destTableUuidFromSMH(srcUuid, data.getDestName());
        if (StringUtils.isEmpty(destUUID)) {
            throw new ServiceException("SMH提供的目的地uuid为空");
        }
        return destUUID;
    }

    private void closeTranscodingAndDeleteDest(String sn, DestDO destByName) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("sn", sn);
        JSONObject resp = HttpUtil.postRequest(HttpUtil.TRANSCODE_BASE_URL + "/v1/decklink/stopTranscode", requestBody);

        // 关闭转码， 停止目的地
        switchDest(destByName, false);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("src_uuid", destByName.getSrcUuid());
        jsonObject.put("dest_uuid", destByName.getDestUuid());
        jsonObject.put("key", HttpUtil.apiKey);

        JSONObject resp2 = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/edit/deldest", jsonObject);
        if (resp2.getString("states").equals("failed")) {
            throw new ServiceException("SMH删除目的地失败" + destByName.getDestName());
        } else {
            destMapper.deleteDestByIds(Collections.singletonList(destByName.getId()));
            routeDestRelDao.removeByDestId(destByName.getId());
        }
        // update route
        Route route = routeMapper.selectRouteByUuid(destByName.getSrcUuid());
        String destids = Arrays.stream(StringUtils.split(route.getDestIds(), ",")).filter(item -> !item.equals(String.valueOf(destByName.getId()))).collect(Collectors.joining(","));
        route.setDestIds(destids);
        routeDestRelDao.removeByDestId(destByName.getId());
        routeMapper.updateRoute(route);
//        DestDO destDO = new DestDO();
//        destDO.setId(destByName.getId());
//        destDO.setStartStop(false);
//        destDO.setDestState(1);
//        destMapper.updateDest(destDO);
    }

    private static void switchDest(DestDO destDO, boolean startStop) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("src_uuid", destDO.getSrcUuid());
        jsonObject.put("dest_uuid", destDO.getDestUuid());
        jsonObject.put("key", HttpUtil.apiKey);
        jsonObject.put("start", String.valueOf(startStop));

        JSONObject resp = HttpUtil.postSMHRequest(HttpUtil.SMH_BASE_URL + "/route/dest/switch", jsonObject);
    }
}
