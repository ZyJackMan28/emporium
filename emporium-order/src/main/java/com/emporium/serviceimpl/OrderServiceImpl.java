package com.emporium.serviceimpl;

import com.emporium.auth.pojo.UserInfo;
import com.emporium.client.AddressClient;
import com.emporium.client.MerchandiseClient;
import com.emporium.common.dto.CartDto;
import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.utils.IdWorker;
import com.emporium.dto.AddressDTO;
import com.emporium.dto.OrderDto;
import com.emporium.enums.OrderStatusEnum;
import com.emporium.enums.PayState;
import com.emporium.interceptor.UserInterceptor;
import com.emporium.item.pojo.Sku;
import com.emporium.mapper.OrderDetailMapper;
import com.emporium.mapper.OrderMapper;
import com.emporium.mapper.OrderStatusMapper;
import com.emporium.pojo.Order;
import com.emporium.pojo.OrderDetail;
import com.emporium.pojo.OrderStatus;
import com.emporium.service.OrderService;
import com.emporium.utils.PayHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MerchandiseClient merchandiseClient;

    @Autowired
    private PayHelper payHelper;
    /*
    * 创建订单--需要订单id前台id?id=...
    * */
    @Override
    public Long createOrder(OrderDto orderDto) {

        //新增订单
        Order order = new Order();
        //订单编号非自增长，需要自己生成，
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDto.getPaymentType());
        //order.set
        //用户信息
        UserInfo user = UserInterceptor.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        //收获人信息
        AddressDTO addr = AddressClient.findById(orderDto.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());
        //金额--cartDto存着数量
        Map<Long, Integer> map = orderDto.getCarts().stream().collect(Collectors.toMap(CartDto::getSkuId, CartDto::getNum));
        //获取商品所有id,查询
        Set<Long> ids = map.keySet();
        //集合set--->list
        List<Sku> skus = merchandiseClient.querySkuBySpuIds(new ArrayList<>(ids));
        //准备orderDetail集合
        List<OrderDetail> details = new ArrayList<>();
        Long totalPay = 0L;
        //Long nums = 0L;
        for (Sku sku : skus) {
            totalPay += sku.getPrice() * map.get(sku.getId());
            //nums += totalPay;
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(),","));
            orderDetail.setNum(map.get(sku.getId()));
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            orderDetail.setPrice(sku.getPrice().longValue());

            details.add(orderDetail);
        }
        order.setTotalPay(totalPay);
        //实际付款,总金额+邮费+优惠
        order.setActualPay(1L + order.getPostFee() -0);
        //写入数据库
        int count = orderMapper.insertSelective(order);
        if (count !=1){
            log.error("订单创建失败，orderId:{}",orderId);
            throw new EpException(EnumsStatus.ORDER_CREATED_FAILED);
        }
        //新增订单详情(orderDetail)表
        int dcount = orderDetailMapper.insertList(details);
        if (dcount != details.size()){
            log.error("订单创建失败，orderId:{}",orderId);
            throw new EpException(EnumsStatus.ORDER_CREATED_FAILED);
        }
        //新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        //
        orderStatus.setStatus(OrderStatusEnum.UN_PAY.value());
        int oCount = orderStatusMapper.insertSelective(orderStatus);
        if (oCount !=1){
            log.error("订单创建失败，orderId:{}",orderId);
            throw new EpException(EnumsStatus.ORDER_CREATED_FAILED);
        }
        //别忘了事务，一旦失败，需要回滚，增删改
        //库存处理---而在商品微服务里面
        List<CartDto> cartDtos = orderDto.getCarts();
        merchandiseClient.decreaseStock(cartDtos);

        return orderId;
    }

    /*
    * 提交订单后，需要查出订单，页面展示订单，订单详情以及订单状态
    * */
    @Override
    public Order queryOrderById(Long id) {
        //查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        if (null == order){
            throw new EpException(EnumsStatus.ORDERS_NOT_EXSIT);
        }
        //因为我的信息--里面需要查询收货人信息，支付时间，所以还需要查询其他两张表
        //orderdetail -order 之间关联是orderId,但orderId不是orderDetail主键
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        List<OrderDetail> details = orderDetailMapper.select(orderDetail);
        if (null == orderDetail){
            throw new EpException(EnumsStatus.ORDERSDETAILS_NOT_EXSIT);
        }
        order.setOrderDetails(details);
        //订单状态
        OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
        if(null == orderStatus){
            throw new EpException(EnumsStatus.ORDERSDETAILS_NOT_EXSIT);
        }
        order.setOrderStatus(orderStatus);
        return order;
    }

    /*
    * 提交订单创建支付订单链接
    * */
    @Override
    public String createOrderUrl(Long orderId) {
        //查询订单
        Order order = queryOrderById(orderId);
        //判断订单的状态
        if (order.getOrderStatus().getStatus() != OrderStatusEnum.UN_PAY.value()){
            //如果不是未支付状态
            throw new EpException(EnumsStatus.ORDERSTATUS_NOT_EXSIT);
        }
        //获取支付金额
        Long actualPay = order.getActualPay();
        //商品描述--<>,可以抽取其中一个标题
        String title = order.getOrderDetails().get(0).getTitle();
        return payHelper.createOrder(orderId, actualPay, title);

    }

    /*
    * 通知处理，根据传递的参数，通信标记，业务通知
    * */
    @Override
    public void handleNotify(Map<String, String> result) {
        //判断标识
        payHelper.isSuccess(result);
        //校验签名
        payHelper.isValidSign(result);
        //校验金额
        String total_fee = result.get("total_fee");
        //订单id
        String tradeNo = result.get("out_trade_no");
        if (StringUtils.isEmpty(total_fee) || StringUtils.isEmpty(tradeNo)){
            throw new EpException(EnumsStatus.UPDATE_ORDERSTATUS_ERROR);
        }
        //获取金额
        Long fee = Long.valueOf(total_fee);
        Long orderId = Long.valueOf(tradeNo);

        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (fee != /*order.getActualPay()*/1L){
            //防止页面篡改金额
            throw new EpException(EnumsStatus.UPDATE_ORDERSTATUS_ERROR);
        }


        //最终修改订单的状态
        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatusEnum.PAYED.value());
        status.setOrderId(orderId);
        status.setPaymentTime(new Date());

        int count = orderStatusMapper.updateByPrimaryKeySelective(status);
        if(count !=1){
            throw new EpException(EnumsStatus.UPDATE_ORDERSTATUS_ERROR);
        }
        log.info("订单支付成功！订单编号:{}",orderId);
    }

    /*
    * 查询支付状态
    * */
    @Override
    public PayState queryOrderState(Long id) {
        try {
            //查订单状态
            OrderStatus orderStatus = orderStatusMapper.selectByPrimaryKey(id);
            Integer status = orderStatus.getStatus();
            //微信通知15s才通知，如果付款后，微信通知还未收到
            //判断已支付
            if (status != OrderStatusEnum.UN_PAY.value()){
                //确实已经支付
                return PayState.SUCCESS;
            }
            //orderStatus.setPaymentTime(new Date());
            //orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
            Thread.sleep(2000L);
            //支付了，但微信还未收到通知，需要微信主动查询
            return payHelper.queryPayState(id);
        }catch (InterruptedException e){
            return null;
        }


    }
}
