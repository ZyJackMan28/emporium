package com.emporium.item.web;

import com.emporium.common.dto.CartDto;
import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Sku;
import com.emporium.item.pojo.Spu;
import com.emporium.item.pojo.SpuDetail;
import com.emporium.item.service.MerchandiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MerchandiseController {

    @Autowired
    private MerchandiseService merchandiseService;

    /*
    *  pageResult少属性，需要添加两个字段，但直接再pageResult里面直接添加字段不合理 PO(persistent object 持久层与数据库字段严格对应) VO（value object)
     *  页面有name等，
    * */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            //当前页，默认第一页
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            //每页显示条数
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            //上下架
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            //搜索关键字 默认查所有，不传参数
            @RequestParam(value = "key",required = false) String key
    ){
            return  ResponseEntity.ok(merchandiseService.querySpuByPage(page,rows,saleable,key));
    }
    /*
    * 因为请求的参数是json 对象，所以使用requestBody
    * */
    @PostMapping("goods")
    public ResponseEntity<Void> saveCommodity(@RequestBody Spu spu){
            merchandiseService.saveCommodity(spu);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    * 商品的修改，主要是回显数据，回显出spu,spu_detail,sku,stock--->
    * 1 . 查spu detail
    * */
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryCommodityDetailById(@PathVariable Long id){
        return ResponseEntity.ok(merchandiseService.queryCommodityDetailById(id));
    }

    /* 2. 查sku
    * */


    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        return ResponseEntity.ok(merchandiseService.querySkuBySpuId(id));
    }

    /* 2. 查sku---购物车页面需要查所添加的商品
     * */
    @GetMapping("sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuBySpuIds(@RequestParam("ids") List<Long> ids){
        return ResponseEntity.ok(merchandiseService.querySkuBySpuIds(ids));
    }

    /*
    *  3.修改，后需要提交，请求方式put --和前端新增是同一个接口
    * */
    @PutMapping("goods")
    public ResponseEntity<Void> updateCommodity(@RequestBody Spu spu){
        merchandiseService.updateCommodity(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /*
    * 根据商品id查询sku
    * */
    @GetMapping("sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(merchandiseService.querySkuById(id));
    }
    /*
    * */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        return ResponseEntity.ok(merchandiseService.querySpuById(id));
    }

    /*
    * 商品减库存
    * */
    @PostMapping("stock/decrease")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDto> carts){
        merchandiseService.decreaseStock(carts);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
