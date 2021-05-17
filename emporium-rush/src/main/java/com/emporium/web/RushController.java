package com.emporium.web;

import com.emporium.pojo.RushMerchandise;
import com.emporium.pojo.SeckillParameter;
import com.emporium.service.RushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class RushController {

    @Autowired
    private RushService rushService;
    /**根据传入的商品id，将其设置为抢购的商品
     * 请求路径 api.emporium.seckill/addSeckill
     */
    @PostMapping("addSeckill")
    public ResponseEntity<Boolean> addSeckillGoods(@RequestBody SeckillParameter seckillParameter){
        if (seckillParameter !=null)
        {
            this.rushService.addSeckillGoods(seckillParameter);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 抢购商品列表查询
     */
    @GetMapping("list")
    public ResponseEntity<List<RushMerchandise>> queryRushMerchandise(){
        List<RushMerchandise> list = this.rushService.queryRushMerchandise();
        if (CollectionUtils.isEmpty(list))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(list);
    }

    /**
     *  每个用户只能抢购一件商品
     */
    @PostMapping("seck")
    public ResponseEntity<Long> seckillOrder(@RequestBody RushMerchandise rushMerchandise){
        //1.创建订单
        Long id = this.rushService.createOrder(rushMerchandise);
        //2.判断秒杀是否成功
        if (null == id)
        {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        return ResponseEntity.ok(id);
    }

}
