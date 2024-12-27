package com.example.productcomparisionweb.common;

public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success() {
        Result result = new Result();
        result.setCode(200);
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(200);
        result.setData(data);
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }

    // 手动添加 getter 和 setter 方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
} 