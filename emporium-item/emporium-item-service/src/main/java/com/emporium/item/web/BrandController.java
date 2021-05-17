package com.emporium.item.web;

import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Brand;
import com.emporium.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // 1) 根据关键字查询品牌并分页
    //controller曾分析，请求的路径，请求的方式，请求的参数，返回的值（类型）--值得注意的是，这里返回的是复杂数据类型，所以封装一个类
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByKey(
            //当前页，默认第一页
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            //每页显示条数
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            //排序关键字
            @RequestParam(value = "sortBy",required = false) String sortBy,
            //是否降序
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            //搜索关键字 默认查所有，不传参数
            @RequestParam(value = "key",required = false) String key
    ){
       PageResult<Brand> result=  brandService.queryBrandByKey(page, rows, sortBy, desc, key);
        return ResponseEntity.ok(result);
    }

    // 2） 品牌新增功能， 前台页面 form 表单提交数据，请求参数等,返回结果为空值
    //但Brand 类里面没有cids1，cids2,... java参数接受使用List,业务新增的内容需要根据你新增--分类(具体)
    //axois请求表单的时候需要将对象转成string 如name=zhangsan&age=21, 所以需要借助qs.stringfy
    @PostMapping
    public ResponseEntity<Void> insertBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        //分析业务逻辑
        brandService.insertBrand(brand,cids);
        //新增成功一般这样写,没有返回值build即可
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 3) 品牌分类的查询 , vuetify select要求返回值是[]集合，返回品牌列表
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid){
        //怎么查，根据品牌service查询
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /* 查询品牌根据品牌id查
    * */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id){
        return ResponseEntity.ok(brandService.queryById(id));
    }

    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(brandService.queryBrandByIds(ids));
    }
}
