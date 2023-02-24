package com.example.reggie.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 地址簿
 */
@Data
@ApiModel(value = "AddressBook", description = "地址薄信息")
public class AddressBook implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "地址薄id",hidden = true,required = false)
    private Long id;

    @ApiModelProperty(value="用户id",hidden = true,required = false)
    //用户id
    private Long userId;

    @ApiModelProperty("收货人")
    //收货人
    private String consignee;

    @ApiModelProperty("手机号")
    //手机号
    private String phone;

    @ApiModelProperty("收货人性别")
    //性别 0 女 1 男
    private String sex;

    @ApiModelProperty("省编号")
    //省级区划编号
    private String provinceCode;

    @ApiModelProperty("省级名称")
    //省级名称
    private String provinceName;

    @ApiModelProperty("市级编号")
    //市级区划编号
    private String cityCode;

    @ApiModelProperty("市级名称")
    //市级名称
    private String cityName;

    @ApiModelProperty("区级编号")
    //区级区划编号
    private String districtCode;

    @ApiModelProperty("区级名称")
    //区级名称
    private String districtName;

    @ApiModelProperty("详细地址")
    //详细地址
    private String detail;

    @ApiModelProperty("标签")
    //标签
    private String label;

    @ApiModelProperty("是否为默认地址")
    //是否默认 0 否 1是
    private Integer isDefault;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value="创建时间",hidden = true,required = false)
    private LocalDateTime createTime;


    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value="更新时间",hidden = true,required = false)
    private LocalDateTime updateTime;


    //创建人
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value="创建用户",hidden = true,required = false)
    private Long createUser;


    //修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value="更新用户",hidden = true,required = false)
    private Long updateUser;


    //是否删除
    @ApiModelProperty(value="逻辑删除",hidden = true,required = false)
    private Integer isDeleted;
    public String getAddress(){
        return (this.provinceName == null?"":this.provinceName)
                + (this.cityName == null?"":this.cityName)
                + (this.districtName == null?"":this.districtName)
                + (this.detail == null?"":this.detail);
    }
}
