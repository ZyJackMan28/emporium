package com.emporium.service;

import com.emporium.dto.OrderDto;
import com.emporium.enums.PayState;
import com.emporium.pojo.Order;

import java.util.Map;

public interface OrderService {
    Long createOrder(OrderDto orderDto);

    Order queryOrderById(Long id);

    String createOrderUrl(Long orderId);

    void handleNotify(Map<String, String> result);

    PayState queryOrderState(Long id);
}
