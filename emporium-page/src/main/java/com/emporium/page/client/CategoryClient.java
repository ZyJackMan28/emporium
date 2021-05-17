package com.emporium.page.client;

import com.emporium.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface CategoryClient extends CategoryApi {
    //调用微服务，只需要调用所被调用的接口
    //但这里我们不需要ResponseEntity,这个只是标记，让springMVC知道， 如果接收ResponseEntity，需要自己判断里面的状态码
    //可以作单元测试---alt + enter
    /*@GetMapping("category/list/ids")
    public List<Category> queryCategorybyIds(@RequestParam("ids") List<Long> ids);*/

}
