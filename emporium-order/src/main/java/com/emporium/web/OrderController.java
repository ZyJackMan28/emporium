package com.emporium.web;

import com.emporium.dto.OrderDto;
import com.emporium.pojo.Order;
import com.emporium.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;



    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto){
        //返回值是订单的编号id--前端页面所需要的
        return ResponseEntity.ok(orderService.createOrder(orderDto));
    }


    /*
    *   提交订单,
    * */
    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }

    /*
    * 订单，还需要查询url,创建支付连接
    * */
    @GetMapping("/url/{id}")
    public ResponseEntity<String> createOrderUrl(@PathVariable("id") Long orderId){
        return ResponseEntity.ok(orderService.createOrderUrl(orderId));
    }

    /*
    * 后端查询服务给前端判断
    *
    * */
    @GetMapping("/state/{id}")
    public ResponseEntity<Integer> queryOrderState(@PathVariable("id") Long id){
        return ResponseEntity.ok(orderService.queryOrderState(id).getValue());
    }
}

