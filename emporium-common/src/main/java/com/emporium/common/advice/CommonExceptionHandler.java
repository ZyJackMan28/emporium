package com.emporium.common.advice;

import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    //处理异常的逻辑, 我们根据业务此处处理的是runtime异常
    //进一步处理自定义异常
    @ExceptionHandler(EpException.class)
    public ResponseEntity<ExceptionResult> handlerException(EpException e){

        EnumsStatus  es = e.getEnumsStatus();
        //返回状态码和消息 而状态码和消息可以包装到异常结果类
        //方法是，自定义异常类---包装一个枚举类（枚举类里有消息和状态码）---（枚举类又被包装到返回结果类），这样返回的结果就不是String而是我们分装好的对象
        return ResponseEntity.status(e.getEnumsStatus().getCode()).body(new ExceptionResult(e.getEnumsStatus()));
    }
}
