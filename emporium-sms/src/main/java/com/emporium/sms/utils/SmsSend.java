

package com.emporium.sms.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.emporium.sms.config.SmsReadProps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created on 17/6/7.
 * 短信API产品的DEMO程序,工程中包含了一个SmsDemo类，直接通过
 * 执行main函数即可体验短信产品API功能(只需要将AK替换成开通了云通信-短信产品功能的AK即可)
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 *
 * 备注:Demo工程编码采用UTF-8
 * 国际短信发送请勿参照此DEMO
 */
@Slf4j
@Component
@EnableConfigurationProperties(SmsReadProps.class)
public class SmsSend {

    @Autowired
    private SmsReadProps props;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    private static final String KEY_PREFIX = "sms:phoneNo-";

    private static final long MIN_INTERVAL = 60000;
    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    //static final String accessKeyId = "LTAIihB9ALQro3kO";
    //static final String accessKeySecret = "5vrw10C7m5kNJw3PkKIis8GMZGOVzu";
    // rabbitMq限流， 使用redis,记录手机号

    public  SendSmsResponse sendSms(String phoneNumber, String signName,String templateCode,String templateContent) {

        String key = KEY_PREFIX + phoneNumber;
        //读取发送成功后的时间
        String lastTime = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(lastTime)){
            Long longLT = Long.valueOf(lastTime);
            if(System.currentTimeMillis()-longLT < MIN_INTERVAL){
                //直接返回null,不继续执行发送
                log.info("[短信服务] 发送频率过高， 手机号码：{}",phoneNumber);
                return null;
            }
        }

        //这里try
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", props.getAccessKeyId(), props.getAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            request.setMethod(MethodType.POST);
            //必填:待发送手机号
            request.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam(templateContent);

            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            // request.setOutId("123456");

            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            if (!"OK".equals(sendSmsResponse.getCode())){
                log.info("短信发送失败: phoneNumber:{},原因 :{}", phoneNumber , sendSmsResponse.getMessage());
            }
            //发送短信成功后，写入redis,记录当前时间
            /*
             * 如果是高并发下，rabbitmq服务器上收到成千上万条消息，那么当打开消费端时，这些消息必定喷涌而来，导致消费端消费不过来甚至挂掉都有可能。
             * 在非自动确认的模式下，可以采用限流模式，rabbitmq 提供了服务质量保障qos机制来控制一次消费消息数量。
             * */

            //成功发送记录日志
            log.info("[短信服务] 发送短信的手机号是: {}",phoneNumber);
            redisTemplate.opsForValue().set(key,String.valueOf(System.currentTimeMillis()),1, TimeUnit.MINUTES);
            return sendSmsResponse;

        }catch (Exception e){
            log.error("短信服务异常， 手机号:{},原因:{}",phoneNumber,e);
            return  null;
        }

    }

}



