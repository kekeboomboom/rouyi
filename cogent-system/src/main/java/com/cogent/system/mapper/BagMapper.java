package com.cogent.system.mapper;

import com.cogent.system.domain.DO.bag.BagDO;
import com.cogent.system.domain.vo.bag.GB;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedList;
import java.util.List;

public interface BagMapper {

    int insertBag(BagDO bagDO);

    int updateBag(BagDO bagDO);

    int deleteBag(List<Integer> ids);

    List<BagDO> getBagList(BagDO bagDO);

    List<BagDO> getAndroidList();

    BagDO getBagById(Integer id);

    int insertGB(GB gb);

    int updateGB(GB gb);

    GB selectGBById(String id);

    int deleteGB(List<String> ids);

    BagDO getBagBySN(String sn);

    BagDO getBagByDevName(String name);

    int setAutoReg(Integer enable);

    int getAutoReg();

    List<BagDO> getBagListByIds(List<Integer> ids);

    List<String> getBagBlackList();

    int updateBagBlacklistBySN(List<String> snList);

    List<BagDO> getBagListBySNList(List<String> snList);

    int selectFoldbackStateCount(String streamId);

    int selectBagCount();

    void updateBagState(@Param("list") List<String> snList, @Param("state") String state);
}
