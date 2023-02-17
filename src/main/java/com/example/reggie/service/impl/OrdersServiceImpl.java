package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.CustomException;
import com.example.reggie.dto.OrdersDto;
import com.example.reggie.mapper.OrdersMapper;
import com.example.reggie.pojo.*;
import com.example.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Transactional
    @Override
    public Orders submit(Orders order,HttpSession session) {
        // 获取当前用户
        Long userId = (Long)session.getAttribute("userSession");

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        // 查询当前用户的购物车
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        if(shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("Error: Empty ShoppingCarts, Order request rejected!");
        }
        Long orderId = IdWorker.getId();

        // 计算购物车总金额
        AtomicInteger amount = new AtomicInteger(0);   // 原子操作
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item)->{
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orderId);
            detail.setNumber(item.getNumber());
            detail.setName(item.getName());

            // 检查当前菜品或者套餐是否仍然在售
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();

            if(dishId != null){
                if(dishService.getById(dishId).getStatus() == 0){
                    throw new CustomException("当前菜品已售罄或停售");
                }
            }
            if(setmealId != null){
                if(setmealService.getById(setmealId).getStatus() == 0){
                    throw new CustomException("当前套餐已售罄或停售");
                }
            }
            detail.setDishId(dishId);
            detail.setSetmealId(setmealId);

            detail.setDishFlavor(item.getDishFlavor());
            detail.setAmount(item.getAmount());
            // 当前菜品的总价钱
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return detail;
        }).collect(Collectors.toList());

        // 查用户信息
        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(order.getAddressBookId());

        // 向订单表插入数据（单条记录）
        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        order.setStatus(2);  // 待派送
        order.setUserId(userId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setAmount(new BigDecimal(amount.get()));
        order.setUserName(user.getName());
        order.setConsignee(addressBook.getConsignee());
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getAddress());
        this.save(order);
        // 向订单明细表插入数据（一项具体菜品一条记录）
        orderDetailService.saveBatch(orderDetails);
        // 清空购物车
        shoppingCartService.remove(wrapper);
        return order;
    }

    @Override
    public Page<OrdersDto> userPage(Integer page, Integer pageSize, HttpSession session) {
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,session.getAttribute("userSession"));
        this.page(pageInfo,wrapper);

        Page<OrdersDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<OrdersDto> dtoRecords = pageInfo.getRecords().stream().map((item)->{
            OrdersDto dto = new OrdersDto();
            List<OrderDetail> details = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, item.getId()));
            BeanUtils.copyProperties(item,dto);
            dto.setOrderDetails(details);
            AtomicInteger sumNum = new AtomicInteger(0);
            details.forEach((detail)->{
                sumNum.addAndGet(detail.getNumber());
            });
            dto.setSumNum(sumNum.get());
            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoRecords);
        return dtoPage;
    }

}
