package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.mapper.CategoryMapper;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.DishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public int remove(Long id) {
        Long dishCount = dishService.count(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id));
        if(dishCount > 0){
            throw new CustomException("Error: associated with exist dish(es)");
        }
        Long setmealCount = setmealService.count(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, id));
        if(setmealCount > 0){
            throw new CustomException("Error: associated with exist Set meal");
        }
        super.removeById(id);
        return 0;
    }

    @Override
    public String getCategoryNameById(Long id) {
        return super.getById(id).getName();
    }
}
