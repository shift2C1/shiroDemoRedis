package com.pipichao.springboot.withDataBase.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MsgException extends Exception{
    private String code;
    private String msg;
}
