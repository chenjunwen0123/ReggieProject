package com.example.reggie.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ApiModel(value = "统一结果集")
@Data
public class Res<T> implements Serializable {
    @ApiModelProperty(name="结果编码")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty(name="错误信息")
    private String msg; //错误信息

    @ApiModelProperty(name="结果体")
    private T data; //数据

    @ApiModelProperty(name="动态数据")
    private Map map = new HashMap(); //动态数据

    public static <T> Res<T> success(T object) {
        Res<T> r = new Res<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> Res<T> error(String msg) {
        Res r = new Res();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Res<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}

