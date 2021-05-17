package com.emporium.page.web;

import com.emporium.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller //防止restController 滤掉js
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("item/{id}.html")
    //视图
    public String itemsDetailPage(@PathVariable("id") Long spuId, Model model){
        //准备模型数据
        Map<String,Object> attributes = pageService.loadModel(spuId);

        model.addAllAttributes(attributes);
        /*
        * 1. spu  ---根据id查询
        * 2. spu详情
        * 3. spu下所有sku
        * 4. 品牌
        * 5. 商品三级分类
        * 6. 商品规格参数，规格参数组*/
        //添加到视图

        //最后返回视图
        return "item";
    }
}
