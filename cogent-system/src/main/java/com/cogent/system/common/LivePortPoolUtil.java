package com.cogent.system.common;

import com.alibaba.fastjson2.JSONObject;
import com.cogent.common.utils.HttpUtil;
import com.cogent.system.dao.RouteDao;
import com.cogent.system.domain.DO.bag.BagDO;
import com.cogent.system.domain.Route;
import com.cogent.system.mapper.BagMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.util.NumberUtil.add;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/9/25
 * {@code @description:}
 */
@Component
public class LivePortPoolUtil {

    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private BagMapper bagMapper;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private HttpUtil httpUtil;

    /**
     * 为了保证端口池的可用性，需要在项目启动的时候，初始化端口池
     * 1. 初始化redis端口池
     * 2. 删除所有SMH中路由，与Android有关的路由
     *
     * 做这些操作都是为了保证端口的正确性。我无法保证设备，和这个几个服务能够正常启动，停止，宕机。
     */
    @PostConstruct
    public void init() {
        RSet<Object> livePortPool = redissonClient.getSet("livePortPool");
        // 初始化端口池
        for (int i = 40506; i <= 40550; i++) {
            livePortPool.add(i);
        }
        // 找到所有安卓
        // 找到所有安卓设备的路由
        // 删除此SMH上此路由
        List<BagDO> androidList = bagMapper.getAndroidList();
        if (CollectionUtils.isEmpty(androidList)) {
            return;
        }
        List<Route> routes = routeDao.selectByRouteNames(androidList.stream().map(BagDO::getSn).collect(Collectors.toList()));
        for (Route route : routes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("src_uuid", route.getUuid());
            jsonObject.put("key", HttpUtil.apiKey);
            jsonObject.put("start", "false");
            JSONObject resp = HttpUtil.postSMHRequest(httpUtil.SMH_BASE_URL + "/route/source/switch", jsonObject);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("src_uuid", route.getUuid());
            jsonObject2.put("key", HttpUtil.apiKey);
            JSONObject respDel = HttpUtil.postSMHRequest(httpUtil.SMH_BASE_URL + "/route/edit/deltask", jsonObject2);
        }
    }


    public int getPort() {
        RSet<Object> livePortPool = redissonClient.getSet("livePortPool");
        Integer port = (Integer) livePortPool.removeRandom();
        if (port == null) {
            return -1;
        }
        return port;
    }

    public void releasePort(int port) {
        redissonClient.getSet("livePortPool").add(port);
    }
}
