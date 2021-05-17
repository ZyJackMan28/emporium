package com.emporium.item.api;

import com.emporium.common.dto.CartDto;
import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Sku;
import com.emporium.item.pojo.Spu;
import com.emporium.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
*  这里不适用feign,如果使用feign,service自己也调用自己了
* */

public interface MerchandiseApi {
    /* spu分页信息
    * */
    @GetMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            //当前页，默认第一页
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            //每页显示条数
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            //上下架
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            //搜索关键字 默认查所有，不传参数
            @RequestParam(value = "key",required = false) String key
    );

    /*
     * 商品的修改，主要是回显数据，回显出spu,spu_detail,sku,stock--->
     * 1 . 查spu detail
     * */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryCommodityDetailById(@PathVariable Long id);

    /* 2. 查sku
     * */
    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id") Long id);

    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long id);

    /*
    *  id ---> spu
    * */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);
    /*
    * 根据id,批量查询
    * */
    @GetMapping("sku/list/ids")
    List<Sku> querySkuBySpuIds(@RequestParam("ids") List<Long> ids);

    /*
    * 减库存
    * */
    @PostMapping("stock/decrease")
    void decreaseStock(@RequestBody List<CartDto> carts);
}
