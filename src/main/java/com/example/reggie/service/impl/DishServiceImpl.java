package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.dto.DishDto;
import com.example.reggie.mapper.DishMapper;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;

import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    @Transactional
    public void saveDish(DishDto dishDto) {
        // 将Dish相关的信息存入dish表
        this.save(dishDto);
        // 将DishFlavor相关的信息存入DishFlavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id = dishDto.getId();
        flavors.forEach((item)->{
            item.setDishId(id);
        });

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getDishWithFlavorsById(Long id) {
        List<DishFlavor> flavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateDishDtoById(DishDto dishDto) {
        // 先更新Dish相关的部分
        this.updateById(dishDto);
        // 删除原来Dish的DishFlavor
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,dishDto.getId()));
        // 新增当前的DishFlavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

}
