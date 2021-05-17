package com.emporium.page.serviceimpl;

import com.emporium.item.pojo.*;
import com.emporium.page.client.BrandClient;
import com.emporium.page.client.CategoryClient;
import com.emporium.page.client.MerchandiseClient;
import com.emporium.page.client.SpecificationClient;
import com.emporium.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class PageServiceImpl implements PageService {


    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private MerchandiseClient merchandiseClient;
    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private TemplateEngine engine;

    /*
    *  加载thymeleaf页面所需要的数据
    * */
    /**
     * 加载模型
     * @param spuId
     * @return
     */
    public Map<String, Object> loadModel(Long spuId) {
        Map<String,Object> model = new HashMap<>();
        //查询spu
        Spu spu = merchandiseClient.querySpuById(spuId);
        //查询skus
        List<Sku> skus = spu.getSkus();
        //查询detail
        SpuDetail detail = spu.getSpuDetail();
        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        //查询categories
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询specs
        List<SpecGroup> specs = specificationClient.queryGroupAndSpecParamsByCid(spu.getCid3());

        /*为优化从而spu只存页面需要的数据，因为spu是一个很庞大的数据*/
        model.put("title",spu.getTitle());
        model.put("subTitle",spu.getSubTitle());
        model.put("skus",skus);
        model.put("detail",detail);
        model.put("brand",brand);
        model.put("categories",categories);
        model.put("specs",specs);
        return model;
    }

    @Override
    public void createHtml(Long spuId) {
        //上下文

        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //输出流-->关联一个文件
        File dest = new File("E:\\IntelliJ\\local\\temp", spuId + ".html");
        //生态静态页前判断是否存在
        if(dest.exists()){
            dest.delete();
        }
        try {
            //字符流
            PrintWriter writer = new PrintWriter(dest,"utf-8");
            //生成html
            engine.process("item",context,writer);
        }catch (Exception e){
            log.error("静态页生成异常！");
        }


    }
    //
    @Override
    public void deleteHtml(Long spuId) {
        //1.找到文件
        File dest = new File("E:\\IntelliJ\\local\\temp", spuId + ".html");
        //2删除
        if(dest.exists()){

        }
    }


/*    public Map<String, Object> loadModel(Long spuId) {
        Map<String,Object> model = new HashMap<>();
        //数据必然是后台查出来的
        Spu spu = merchandiseClient.querySpuById(spuId);
        List<Sku> skus = spu.getSkus();
        SpuDetail detail = spu.getSpuDetail();
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //规格参数需要第3级分类
        List<SpecGroup> specs = specificationClient.queryGroupAndSpecParamsByCid(spu.getCid3());
        *//*考虑到页面不需要spu所有数据，所以后台可以不传递spu*//*
        //model.put("spu",spu);
        model.put("title",spu.getTitle());
        model.put("subTitle",spu.getSubTitle());
        model.put("skus",skus);
        model.put("detail", detail);
        model.put("brand",brand);
        model.put("categories",categories);
        model.put("specs",specs);

        return model;
    }*/
}
