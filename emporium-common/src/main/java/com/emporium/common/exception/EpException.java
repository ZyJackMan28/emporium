package com.emporium.common.exception;

import com.emporium.common.enums.EnumsStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
//自定义异常类，属性为状态码和消息
public class EpException extends RuntimeException {

    //通过构造函数接收枚举
    private EnumsStatus enumsStatus;
}
