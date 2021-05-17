package com.emporium.cart.web;

import com.emporium.cart.pojo.Cart;
import com.emporium.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /*
    * 添加购物车
    * */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        //用户登录添加购物车
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/merge")
    public ResponseEntity<Void> merge(@RequestBody List<Cart> cartList){
        //合并
        cartService.merge(cartList);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    *  查询购物车，显示
    * */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList(){
        //因为本地存储的是list
        return ResponseEntity.ok(cartService.queryCartList());
    }


    /*
    * 修改购物车数量
    * */
    @PutMapping
    public ResponseEntity<Void> updateCartNum(@RequestParam("id") Long skuId,
                                              @RequestParam("num") Integer num){
        cartService.updateCartNum(skuId,num);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
    *  删除购物车,根据商品的skuId删除
    * */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCartContent(@PathVariable("skuId") Long skuId){
        //
        cartService.deleteCartContent(skuId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
