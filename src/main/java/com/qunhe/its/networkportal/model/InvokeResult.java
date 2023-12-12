package com.qunhe.its.networkportal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvokeResult {
    private int code;
    private String message;
    private Object data;

    // 省略构造方法

    public static InvokeResult ok() {
        return new InvokeResult(0, "OK", null);
    }

    public static InvokeResult ok(Object data) {
        return new InvokeResult(0, "OK", data);
    }

    public static InvokeResult failure(String message) {
        return new InvokeResult(1, message, null);
    }

    public static InvokeResult error() {
        return new InvokeResult(2, "Internal Server Error", null);
    }

    // 省略 getter 和 setter 方法
}
