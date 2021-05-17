package com.emporium.cart.serviceimpl;

import com.emporium.auth.pojo.UserInfo;
import com.emporium.cart.client.MerchandiseClient;
import com.emporium.cart.interceptor.UserInterceptor;
import com.emporium.cart.pojo.Cart;
import com.emporium.cart.service.CartService;
import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private MerchandiseClient merchandiseClient;

    private static final String PRE_KEY = "cart-user-Id:";



    @Override
    public void addCart(Cart cart) {
        //记录cart num
        Integer num = cart.getNum();

        //取登录用户--拦截器--service共享同一个线程
        UserInfo user = UserInterceptor.getUser();
        //key,这个key实际上是用户标识
        String key = PRE_KEY + user.getId();
        //hashkey--field->value--对应java是个双层Map--Map<String,Map<String,String>>--key, field,value --便于快速找到商品id,value-是购物车对象
        String hashkey = cart.getSkuId().toString();
        //因为opsHash需要传递两个key,hashkey，所以采用boundhashops--key(userid)绑定
        BoundHashOperations<String,Object,Object> operation = template.boundHashOps(key);
        //判断当前购物车是否存在
        if(operation.hasKey(hashkey)){
            //存在，只要修改数量-->但是取到的数据需要转成string(redis只存string)--json格式的
            String json = operation.get(hashkey).toString();
            //需要将json转成购物车对象
            //Cart cacheCart = JsonUtils.toBean(json, Cart.class);
            //覆盖原来的cart
            cart = JsonUtils.toBean(json, Cart.class);
            cart.setNum(cart.getNum() + num);
            //cacheCart.setNum(cacheCart.getNum() + cart.getNum());
            //存进去的是json,取出来的还是json
        }/*else{
            //不存在的话需要将localStorage里面的添加到redis
            //根据后台的ajax,需要查询商品微服务得到sku
            *//*Long skuId =Long.valueOf(hashkey);
            Sku sku = merchandiseClient.querySkuById(skuId);
            cart.setSkuId(sku.getId());
            cart.setImage(sku.getImages());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());*//*
        }*/
        operation.put(hashkey,JsonUtils.toString(cart));


    }

    @Override
    public List<Cart> queryCartList() {
        //取登录用户--拦截器--service共享同一个线程
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = PRE_KEY + user.getId();
        if (!template.hasKey(key)){
            //如果field 不存在
            throw new EpException(EnumsStatus.CART_NULL);
        }
        //获取登录用户的所用信息
        BoundHashOperations operation = this.template.boundHashOps(key);
        //只需要field 的value;
        List<Cart> cartList = (List<Cart>) operation.values().stream().map(o -> JsonUtils.toBean(o.toString(), Cart.class)).collect(Collectors.toList());

        return cartList;
    }

    /*
    *  修改--此时是修改redis
    * */
    @Override
    public void updateCartNum(Long skuId, Integer num) {
        //取登录用户--拦截器--service共享同一个线程
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = PRE_KEY + user.getId();
        //
        BoundHashOperations<String, Object, Object> operations = template.boundHashOps(key);
        //判断redis是否存在haskey
        if(!operations.hasKey(skuId.toString())){
            throw new EpException(EnumsStatus.CART_NULL);
        }
        //查get
        Cart cart = JsonUtils.toBean(operations.get(skuId.toString()).toString(), Cart.class);
        //存在，只接设置所更改的值
        cart.setNum(num);
        //写回将购物车写回redis
        operations.put(skuId.toString(),JsonUtils.toString(cart));
    }

    /*
    * 删除购物车
    * */
    @Override
    public void deleteCartContent(Long skuId) {
        //取登录用户--拦截器--service共享同一个线程
        UserInfo user = UserInterceptor.getUser();
        //key
        String key = PRE_KEY + user.getId();
        //根据key删除
        template.opsForHash().delete(key,skuId.toString());

    }

    @Override
    public void merge(List<Cart> cartList) {
        UserInfo user = UserInterceptor.getUser();
        for (Cart cart : cartList) {
            addCart(cart);
        }
    }


}
