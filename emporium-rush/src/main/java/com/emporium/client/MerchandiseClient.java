package com.emporium.client;

import com.emporium.item.api.MerchandiseApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface MerchandiseClient extends MerchandiseApi {
}
