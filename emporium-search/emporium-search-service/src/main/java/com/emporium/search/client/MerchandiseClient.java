package com.emporium.search.client;

import com.emporium.item.api.MerchandiseApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface MerchandiseClient extends MerchandiseApi {
}
