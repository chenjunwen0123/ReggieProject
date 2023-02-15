package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Res;
import com.example.reggie.pojo.ShoppingCart;
import com.example.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService cartService;

    @GetMapping("/list")
    public Res<List<ShoppingCart>> getShoppingCart(HttpSession session){
        Long userId = (Long)session.getAttribute("userSession");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ShoppingCart::getCreateTime);
        wrapper.eq(ShoppingCart::getUserId,session.getAttribute("userSession"));
        return Res.success(cartService.list(wrapper));
    }

    @PostMapping("/add")
    public Res<String> add(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        // 指定用户购物车
        Long userId = (Long)session.getAttribute("userSession");
        shoppingCart.setUserId(userId);
        shoppingCart.setCreateTime(LocalDateTime.now());

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        // 判断添加的是菜品还是套餐
        if(shoppingCart.getDishId() != null){
            // 添加的是菜品，不同口味归属不同的菜品对象
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
            //wrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());
        }else{
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        // 若已经存在，叠加价格和份数
        ShoppingCart cart = cartService.getOne(wrapper);
        if(cart != null){
            cart.setNumber(cart.getNumber() + 1);
            cartService.updateById(cart);
        }else{
            cartService.save(shoppingCart);
        }

        return Res.success("成功添加至购物车");
    }

    @DeleteMapping("/clean")
    public Res<String> clean(HttpSession session){
        Long userId = (Long)session.getAttribute("userSession");
        cartService.remove(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId,userId));
        return Res.success("清空购物车成功");
    }

    @PostMapping("/sub")
    public Res<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        Long userId = (Long) session.getAttribute("userSession");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());
        wrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        //wrapper.eq(shoppingCart.getDishFlavor()!=null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());

        ShoppingCart cart = cartService.getOne(wrapper);
        Integer currentNumber = cart.getNumber();
        if(currentNumber - 1 == 0){
            cartService.removeById(cart);
        }else{
            cart.setNumber(currentNumber - 1);
            cartService.updateById(cart);
        }
        return Res.success(cart);
    }
}
