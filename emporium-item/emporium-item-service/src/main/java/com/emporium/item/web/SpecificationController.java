package com.emporium.item.web;

import com.emporium.item.pojo.SpecGroup;
import com.emporium.item.pojo.SpecParam;
import com.emporium.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /*
    *  写程序，先从前端，web-->service-->dao
    *http://lynx.emporium.com/api/item/spec/groups/76
    *  根据前端vue页面返回data :items:group; 返回规格组信息<集合>
    *      这里前端页面没有需要分页
    * */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupInfoByCid(@PathVariable("cid") Long cid){

        return ResponseEntity.ok(specificationService.queryGroupInfoByCid(cid));
    }

    /*
    * 根据组id查询
    * 这里注意因为gid,不能使用RequestParable
    *http://lynx.emporium.com/api/item/category/list?pid=75
    * */
   /* @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamInfoByGid(@RequestParam("gid") Long gid){
        return ResponseEntity.ok(specificationService.queryParamInfoByGid(gid));
    }*/

    /*
    * 商品品牌规格参数，因为已经通过gid查询到规格参数
    * 但该方法是在当前分类下 可以根据组id, gid cid查，但搜索微服务可以根据seaching来查询
    * required false 是否必须
    *  因搜索功能---多加一个参数
    *  @Param gid
    *  @Param cid
    *  @Param searching   前台新增商品，---商品分类变化，规格参数也跟着变化，所以watch需要再发起查询
    * */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParam(@RequestParam(value = "gid", required = false) Long gid
    ,@RequestParam(value = "cid",required = false) Long cid, @RequestParam(value = "searching",required = false) Boolean searching){
        return ResponseEntity.ok(specificationService.queryParam(gid,cid,searching));
    }

    /*
    * 根据分类查询规格组以及规格参数
    * */
    @GetMapping("group")
    public ResponseEntity<List<SpecGroup>> queryGroupAndSpecParamsByCid(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryGroupAndSpecParamsByCid(cid));
    }
}
