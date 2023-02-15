package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.dto.OrdersDto;
import com.example.reggie.pojo.Orders;

import javax.servlet.http.HttpSession;

public interface OrdersService extends IService<Orders> {
    Orders submit(Orders orders,HttpSession session);
    Page<OrdersDto> userPage(Integer page, Integer pageSize, HttpSession session);
}
