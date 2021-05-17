package com.emporium.crew.web;

import com.emporium.crew.pojo.User;
import com.emporium.crew.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /*
    * 数据验证唯一性
    * */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable(value = "data") String data,
                                                 @PathVariable(value = "type" ) Integer type){
        return ResponseEntity.ok(userService.checkUserData(data,type));
    }
    /* 发送验证码
    * */
    @PostMapping("code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone") String phone){
        userService.sendCode(phone);
        //无返回值，no_content 204
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    /*
    * 用户注册,需要有验证码,自定义返回结果
    * */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user,
                                         //BindingResult result,
                                         @RequestParam("code") String code){
        /*if(result.hasFieldErrors()){
            throw new RuntimeException(result.getFieldErrors().stream().map(e -> e.getDefaultMessage()).collect(Collectors.joining("-")));
        }*/
        //code不属于用户
        userService.register(user,code);
        //状态码201 创建成功
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*
    * 用户登录
    * */
    @GetMapping("/query")
    public ResponseEntity<User> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password){
        return ResponseEntity.ok(userService.login(username,password));
    }
}
