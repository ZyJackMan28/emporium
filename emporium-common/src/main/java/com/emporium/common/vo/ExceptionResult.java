package com.emporium.common.vo;

import com.emporium.common.enums.EnumsStatus;
import lombok.Data;

@Data
public class ExceptionResult {

    private int status;
    private String message;
    private Long timestamp;

    //因为枚举类 包装到异常结果类
    public ExceptionResult(EnumsStatus es ){
        this.status = es.getCode();
        this.message = es.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
