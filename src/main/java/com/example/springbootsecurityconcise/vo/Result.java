package com.example.springbootsecurityconcise.vo;

import lombok.Data;

/**
 * 传递数据对象
 */
@Data
public class Result {
    // 0成功 1失败
    Integer code;
    // 200 成功 500失败
    Integer status;
    // 消息
    String msg;
}
