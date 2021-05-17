package com.emporium.sms.listner;

import com.emporium.common.utils.JsonUtils;
import com.emporium.sms.config.SmsReadProps;
import com.emporium.sms.utils.SmsSend;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListner {
    @Autowired
    private SmsSend smsSend;
    @Autowired
    private SmsReadProps props;
    //发送短信验证码
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.validate.code.queue", durable = "true"),
            exchange = @Exchange(name="emp.sms.exchange",type = ExchangeTypes.TOPIC),
            key = {"sms.validate.code"}
    ))
    public void listenerValidateCode(Map<String,String> message){
        //amqp converAndSend()-->发送一个消息
        if (null == message){
            return;
        }
        //验证码内容-->remove 删除并获取value
        String phoneNumber = message.remove("phoneNumber");
        if (StringUtils.isBlank(phoneNumber)){
            return;
        }
        //只有有手机号才发送短信
        //message<>只剩下value

        //try {
            //遇到异常，要么try,要么throw，编译异常，try, -->但这里try会触发rabbitmq回归，重试，但连续重试会遇到限流
            smsSend.sendSms(phoneNumber,props.getSignName(),props.getVerifyCodeTemplate(), JsonUtils.toString(message));
        //}catch (Exception e){
        //    e.printStackTrace();
        //}
        //发送短信日志

    }
}
