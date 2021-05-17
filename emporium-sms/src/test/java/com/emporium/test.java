package com.emporium;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EpSmsApplication.class)
public class test {

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void testSendMsg(){
        Map<String,String> msg = new HashMap<>();
        msg.put("phoneNumber","13777385464");
        msg.put("code","52312");
        amqpTemplate.convertAndSend("emp.sms.exchange","sms.validate.code",msg);

        try{
            Thread.sleep(10000L);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
