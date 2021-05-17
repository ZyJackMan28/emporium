package com.emporium.item.service;

import com.emporium.common.dto.CartDto;
import com.emporium.common.vo.PageResult;
import com.emporium.item.pojo.Sku;
import com.emporium.item.pojo.Spu;
import com.emporium.item.pojo.SpuDetail;

import java.util.List;

public interface MerchandiseService {
    PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key);

    void saveCommodity(Spu spu);

    SpuDetail queryCommodityDetailById(Long id);

    List<Sku> querySkuBySpuId(Long id);

    void updateCommodity(Spu spu);

    Spu querySpuById(Long id);

    List<Sku> querySkuBySpuIds(List<Long> ids);

    void decreaseStock(List<CartDto> carts);

    Sku querySkuById(Long id);

    /*
    *  商品增删改发送消息 ---> 就需要在业务逻辑里使用amqptemplate convertandsend进行处理
    * */
}
