package com.emporium.item.api;

import com.emporium.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {
    /*
     *  索引库的数据来自数据库，但搜索微服务不能直接查询,而是调用微服务
     *  spu,skus,spu details, --all[字段]
     * */
    /*@GetMapping("category/list/ids")
    List<Category> queryCategorybyIds(@RequestParam("ids") List<Long> ids);*/
    @GetMapping("category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam(value = "ids") List<Long> ids);
}
