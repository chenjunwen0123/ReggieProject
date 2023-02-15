package com.example.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.reggie.common.Res;
import com.example.reggie.dto.OrdersDto;
import com.example.reggie.pojo.Orders;
import com.example.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    public Res<Orders> submit(@RequestBody Orders order,HttpSession session){
        order = ordersService.submit(order,session);
        return Res.success(order);
    }
    @GetMapping("/userPage")
    public Res<Page<OrdersDto>> userPage(Integer page, Integer pageSize, HttpSession session){
        Page<OrdersDto> dtoPage = ordersService.userPage(page, pageSize,session);
        return Res.success(dtoPage);
    }
    @GetMapping("/page")
    public Res<Page<Orders>> page(Integer page,Integer pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        ordersService.page(pageInfo);
        return Res.success(pageInfo);
    }
}
