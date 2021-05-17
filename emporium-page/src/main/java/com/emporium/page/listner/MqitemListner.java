package com.emporium.page.listner;

import com.emporium.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqitemListner {
    @Autowired
    private PageService pageService;
    /*
    *监听---item 新增，修改（新增一个到索引库）
    * ------删除(索引库删除)
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="webpage.item.update.queue", durable = "true"),
            exchange = @Exchange(name="emporium.item.exchange", type = ExchangeTypes.TOPIC),
            //routerKey规则
            key={"item.insert", "item.update"}
    ))
    public void listnerInsertAndUpadate(Long spuId){
        if(null == spuId){
            //倘若没有收到spuId，说明消息没接收到
            return;
        }
        //--->创建静态页
        pageService.createHtml(spuId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="search.item.delete.queue", durable = "true"),
            exchange = @Exchange(name="emporium.item.exchange", type = ExchangeTypes.TOPIC),
            //routerKey规则
            key={"item.delete"}
    ))
    public void listnerDelete(Long spuId){
        if(null == spuId){
            //倘若没有收到spuId，说明消息没接收到
            return;
        }
        //删除静态页
        pageService.deleteHtml(spuId);

    }
}
