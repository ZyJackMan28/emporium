package com.emporium.crew.serviceimpl;

import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.utils.NumberUtils;
import com.emporium.crew.mapper.UserMapper;
import com.emporium.crew.pojo.User;
import com.emporium.crew.service.UserService;
import com.emporium.crew.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String KEY_PREFIX="user:codeVfy:phoneNo-";

    /*校验数据
    * */
    @Override
    public Boolean checkUserData(String data, Integer type) {
        //校验数据--从数据库查实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验(唯一则可用[数据库中查没有则true])
        User record = new User();
        //record.setUsername(data);
        switch (type){
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new EpException(EnumsStatus.REQ_PARAM_IS_NOT_CORRECT);
        }

        //userMapper.selectOne(record);
        //只需要查询数量，不要查用户
        int count = userMapper.selectCount(record);
        //会判断count==0是true
        return count == 0;
    }
    /*
    根据用户输入手机号，生成随机验证码，6位，并调用短信服务，发送到用户手机
    * 验证发送短信
    * */
    @Override
    public void sendCode(String phone) {
        String key = KEY_PREFIX + phone;
        //生成验证码--长度6位，随机数
        String code = NumberUtils.generateCode(6);

        Map<String,String> msg = new HashMap<>();
        msg.put("phoneNumber",phone);
        msg.put("code",code);
        //发送验证码-->到队列， listener监听到之后，验证码才真的发送到用户手机
        amqpTemplate.convertAndSend("emp.sms.exchange","sms.validate.code",msg);
        //保存验证码--->用户点击发送验证码-->验证码发送过去，但用户还需要验证该验证码，所以需要将验证码保存 5分钟过期--redis
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);
    }
    /*
     * 用户注册
     * */
    @Override
    public void register(User user, String code) {
        //校验验证码-->从redis
        String catcheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code,catcheCode)){
            //校验用户输入的code和redis code是否相同
            throw new EpException(EnumsStatus.CODE_INVALID);
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //对密码加密 (md5加密+加盐)--注意工具CodeUtils.md5(,salt-->是用户)
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),user.getSalt()));
        //写入数据库,补全
        user.setCreated(new Date());
        userMapper.insert(user);
    }

    /*
    * 用户登录
    * */
    @Override
    public User login(String username, String password) {
        //new出来的是空user
        User record = new User();
        record.setUsername(username);
        System.out.println(record.getSalt());
        //通用mapper-->
        User user = userMapper.selectOne(record);
        //密码是有盐, md5---->只能根据用户名来查询
        if(null == user){
            throw new EpException(EnumsStatus.USER_INVALID);
        }
        //校验密码---<单独校验，不需要多层if嵌套>
        if (!StringUtils.equals(CodecUtils.md5Hex(password,user.getSalt()),user.getPassword())){
            throw new EpException(EnumsStatus.PASSWORD_INVALID);
        }


        //返回user,springMVC自动序列化为json

        return user;
    }


}
