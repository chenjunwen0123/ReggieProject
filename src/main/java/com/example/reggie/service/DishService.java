package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.dto.DishDto;
import com.example.reggie.pojo.Dish;

import java.util.Map;

public interface DishService extends IService<Dish> {
    void saveDish(DishDto dishDto);
    DishDto getDishWithFlavorsById(Long id);
    void updateDishDtoById(DishDto dishDto);
}
