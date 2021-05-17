package com.emporium.search.service;

import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Spu;
import com.emporium.pojo.SearchMerchandise;
import com.emporium.pojo.SearchRequest;

public interface EsearchService {

    //要把查询到的数据封装成searchMerchandise,返回值,参数是spu(根据spu)
    SearchMerchandise buildMerchandise(Spu spu);


    PageResult<SearchMerchandise> search(SearchRequest request);


    void createOrUpdateIndexToeS(Long spuId);

    void deleteFromIndex(Long spuId);
}
