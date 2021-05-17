package com.emporium.pojo;

import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Brand;
import com.emporium.item.pojo.Category;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
// 注意这里最好手写全参，因为继承父类，不好找到父类的实体
public class SearchResult extends PageResult<SearchMerchandise> {

    private List<Category> categories;// 分类过滤条件

    private List<Brand> brands; // 品牌过滤条件

    //规格参数过滤条件
    private List<Map<String, Object>> specs;
    public SearchResult() {
    }

    //23种设计模式，构造函数过长，可以采用工厂模式去创建

    public SearchResult(Long total, int totalPage, List<SearchMerchandise> items, List<Category> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }
    //private List<Map<String,String>> specs; // 规格参数过滤条件

/*    public SearchResult(Long total, Integer totalPage, List<SearchMerchandise> items,
                        List<Category> categories, List<Brand> brands,
                        List<Map<String,String>> specs) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
    }*/
}