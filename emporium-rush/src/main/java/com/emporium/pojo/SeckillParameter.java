package com.emporium.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
public class SeckillParameter {
    /**
     * 要抢购的sku id
     */
    private Long id;

    /**
     * 秒杀开始时间
     */
    private Date startTime;

    /**
     * 秒杀结束时间
     */
    private Date endTime;

    /**
     * 参与秒杀的商品数量
     */
    private Integer count;

    /**
     * 折扣
     */
    private double  discount;
}
