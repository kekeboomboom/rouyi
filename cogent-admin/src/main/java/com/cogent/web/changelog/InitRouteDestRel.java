package com.cogent.web.changelog;

import com.cogent.common.utils.StringUtils;
import com.cogent.system.dao.RouteDao;
import com.cogent.system.dao.RouteDestRelDao;
import com.cogent.system.domain.DO.route.RouteDestRelDO;
import com.cogent.system.domain.Route;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * {@code @author:} keboom
 * {@code @date:} 2023/8/11
 * {@code @description:} 新建了route_dest_rel表，将route表中的路由与目的地的关系数据放到新建的表中
 */
public class InitRouteDestRel implements CustomTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        RouteDao routeDao = context.getBean(RouteDao.class);
        RouteDestRelDao routeDestRelDao = context.getBean(RouteDestRelDao.class);

        List<Route> routeList = routeDao.list();
        LinkedList<RouteDestRelDO> insertCollections = new LinkedList<>();
        for (Route route : routeList) {
            String destIds = route.getDestIds();
            if (StringUtils.isEmpty(destIds)) {
                continue;
            }
            String[] destArr = StringUtils.split(destIds, ",");
            for (String s : destArr) {
                RouteDestRelDO routeDestRelDO = new RouteDestRelDO(route.getId(), Integer.valueOf(s));
                insertCollections.add(routeDestRelDO);
            }
        }
        routeDestRelDao.saveBatch(insertCollections);
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}
