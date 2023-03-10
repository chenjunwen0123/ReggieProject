package com.example.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String sex;
    private String idNumber;
    private Integer status;
    @TableField(fill=FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape= JsonFormat.Shape.STRING)
    private Long updateUser;
}
