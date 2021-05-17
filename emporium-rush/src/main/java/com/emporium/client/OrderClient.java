package com.emporium.client;

import com.emporium.api.OrderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("order-service")
public interface OrderClient extends OrderApi {
}
