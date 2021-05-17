package com.emporium.cart.client;

import com.emporium.item.api.MerchandiseApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface MerchandiseClient extends MerchandiseApi {
}
