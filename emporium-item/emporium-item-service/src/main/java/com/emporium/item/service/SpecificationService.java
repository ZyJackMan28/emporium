package com.emporium.item.service;

import com.emporium.item.pojo.SpecGroup;
import com.emporium.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {
    //根据商品类别查询商品组信息
    List<SpecGroup> queryGroupInfoByCid(Long cid);

    /*List<SpecParam> queryParamInfoByGid(Long gid);*/

    List<SpecParam> queryParam(Long gid, Long cid, Boolean searching);

    List<SpecGroup> queryGroupAndSpecParamsByCid(Long cid);
}
