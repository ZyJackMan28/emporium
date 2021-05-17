package com.emporium.search.web;

import com.emporium.common.vo.PageResult;
import com.emporium.pojo.SearchMerchandise;
import com.emporium.pojo.SearchRequest;
import com.emporium.search.service.EsearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    @Autowired
    private EsearchService esearchService;
    /*分析: 搜索索引库数据，返回结果是分页对象(泛型是搜索映射的对象)，利用通用分页pageResult,
    * 参数，是json
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<SearchMerchandise>> search(@RequestBody SearchRequest request){
       return ResponseEntity.ok(esearchService.search(request));

    }
}
