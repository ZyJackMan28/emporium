package com.emporium.cart.service;

import com.emporium.cart.pojo.Cart;

import java.util.List;

public interface CartService {
    //登录新增购物车
    void addCart(Cart cart);
    //查询购物车列表
    List<Cart> queryCartList();
    //更新购物车数量
    void updateCartNum(Long skuId, Integer num);
    //根据商品id删除购物车内容
    void deleteCartContent(Long skuId);

    void merge(List<Cart> cartList);
    //合并购物车，localStorage 和redis
}
