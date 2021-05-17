package com.emporium.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum  EnumsStatus {
    //枚举 类似单例
//    private static final EnumsStatus ff = new EnumsStatus(400,"价格不能为空"),下面代码类似
    CATAGORY_IS_NOT_FOUND(404,"商品分类未能查询到！"),
    BRAND_IS_NOT_FOUND(404,"商品品牌未能查询到！"),
    BRAND_IS_INSERT_FAILED(500,"商品品牌新增失败！"),
    BRAND_CATEGORY_IS_INSERT_FAILED(500,"商品品牌新增失败！"),
    FILEUPLOAD_IS_FAILED(500,"文件上传失败！"),
    FILE_TYPE_IS_INSUPPORTABLE(500,"文件类型不支持！"),
    SPEC_GROUP_IS_NOT_FOUND(500,"商品规格参数组未查询到！"),
    SPEC_PARAM_IS_NOT_FOUND(500,"商品规格参数信息未查询到！"),
    MERCHANDISE_IS_NOT_FOUND(500,"商品不存在！"),
    MERCHANDISE_SAVE_FALIURE(500,"商品新增失败！"),
    MERCHANDISE_STOCK_NOT_FOUND(500,"商品库存不存在！"),
    MERCHANDISE_UPDATE_FAILED(500,"商品修改失败！"),
    MERCHANDISE_ID_CANNOT_BE_NULL(400,"商品id不能为空！"),
    REQ_PARAM_IS_NOT_CORRECT(400,"请求的参数不正确！"),
    CODE_INVALID(400,"验证码不正确！"),
    USER_INVALID(400,"用户名不正确！"),
    PASSWORD_INVALID(400,"密码不正确！"),
    TOKEN_GEN_FAILED(500,"无法生成令牌！"),
    USER_NOT_LOGIN(403,"用户未登录！"),
    CART_NULL(403,"购物车为空！"),
    ORDER_CREATED_FAILED(500,"订单创建失败！"),
    STOCK_NOT_AFFLUENT(500,"库存不足！"),
    ORDERS_NOT_EXSIT(500,"订单不存在！"),
    ORDERSDETAILS_NOT_EXSIT(500,"订单详情不存在！"),
    ORDERSTATUS_NOT_EXSIT(500,"订单状态不存在！"),
    WX_PAY_ORDER_FALI(500,"微信支付订单失败！"),
    INVALID_SIGN_ERROR(500,"微信签名无效！"),
    INVALID_ORDER_PARAM(500,"订单参数错误！"),
    UPDATE_ORDERSTATUS_ERROR(500,"更新订单参数失败！"),
    ORDERSTATUS_ERROR(500,"订单有误！")



    ;
    private int code;
    private String msg;
}
