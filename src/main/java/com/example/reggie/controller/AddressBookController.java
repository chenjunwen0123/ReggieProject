package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Res;
import com.example.reggie.pojo.AddressBook;
import com.example.reggie.service.AddressBookService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
@Api(tags="地址簿管理接口",value="/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增
     */

    @ApiOperation(value="新增地址")
    @ApiImplicitParam(name="addressBook",value="新地址薄信息",required = true,dataType = "AddressBook",dataTypeClass = AddressBook.class,paramType = "body")
    @PostMapping
    public Res<AddressBook> save(@RequestBody AddressBook addressBook, HttpSession session) {
        addressBook.setUserId((Long)session.getAttribute("userSession"));
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return Res.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @ApiOperation(value="设置默认地址")
    @PutMapping("default")
    public Res<AddressBook> setDefault(@RequestBody AddressBook addressBook,HttpSession session) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, session.getAttribute("userSession"));
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return Res.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @ApiOperation(value="根据地址Id查询地址")
    @GetMapping("/{id}")
    public Res get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return Res.success(addressBook);
        } else {
            return Res.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @ApiOperation(value="获取默认地址")
    @GetMapping("default")
    public Res<AddressBook> getDefault(HttpSession session) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, session.getAttribute("userSession"));
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return Res.error("没有找到该对象");
        } else {
            return Res.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @ApiOperation(value="查询指定用户的全部地址")
    @GetMapping("/list")
    public Res<List<AddressBook>> list(AddressBook addressBook,HttpSession session) {
        addressBook.setUserId((Long)session.getAttribute("userSession"));
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return Res.success(addressBookService.list(queryWrapper));
    }

    @ApiOperation(value="更新地址")
    @ApiImplicitParam(name = "addressBook", paramType = "body", value = "新地址信息", required = true,dataType = "AddressBook", dataTypeClass = AddressBook.class)
    @PutMapping
    public Res<String> updateAddress(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return Res.success("修改成功");
    }

    @ApiOperation(value="删除地址",httpMethod = "DELETE")
    @DeleteMapping
    public Res<String> deleteAddress(@RequestParam("ids") Long ids){
        addressBookService.removeById(ids);
        return Res.success("删除成功");
    }

}
