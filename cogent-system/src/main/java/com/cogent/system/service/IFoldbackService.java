package com.cogent.system.service;

import com.cogent.system.domain.DO.foldback.FoldbackSourceDO;
import com.cogent.system.domain.vo.foldback.FoldbackSourceVO;
import com.cogent.system.domain.vo.foldback.FoldbackStreamInfoVO;

import java.util.List;

public interface IFoldbackService {

    void createSource(String name, Integer delay);

    void updateSource(Integer id, String name, Integer delay);

    void deleteSource(Integer id);

    List<FoldbackSourceVO> getSourceList();

    FoldbackSourceDO getSource(Integer id);

    FoldbackStreamInfoVO getSourceStreamInfo(String streamId);
}
