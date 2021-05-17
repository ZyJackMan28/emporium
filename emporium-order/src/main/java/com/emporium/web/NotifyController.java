package com.emporium.web;

import com.emporium.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("notify")
public class NotifyController {
    @Autowired
    private OrderService orderService;

    /*
    * 微信通知服务, 消息转换器转json(SpringMvc)
    * 需要将xml文件转json, annocationdrivebeandefinitionParser类，可以处理xml
    * 因为xml文件，-->json，接收后需要给微信应答 return_code return_msg
    * */
    @PostMapping(value = "wxpay",produces = "application/xml")
    public Map<String,String> hello(@RequestBody Map<String,String> result){
        //处理回调
        orderService.handleNotify(result);
        log.info("微信支付回调，结果:{}",result);
        //返回map
        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("return_code","SUCCESS");
        msgMap.put("return_code","OK");
        return msgMap;
    }
}
