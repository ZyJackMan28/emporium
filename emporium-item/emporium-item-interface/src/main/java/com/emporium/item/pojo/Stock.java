package com.emporium.item.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
@Data
@Table(name = "tb_stock")
public class Stock {
    /*库存是spu绑定的所以不要求主键规则*/
    @Id
    private Long skuId;
    private Integer seckillStock;// 秒杀可用库存
    private Integer seckillTotal;// 已秒杀数量
    private Integer stock;// 正常库存
}