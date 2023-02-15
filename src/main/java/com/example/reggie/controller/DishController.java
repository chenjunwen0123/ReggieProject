package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.reggie.common.Res;
import com.example.reggie.dto.DishDto;

import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.DishFlavor;
import com.example.reggie.service.CategoryService;

import com.example.reggie.service.DishFlavorService;
import com.example.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public Res<String> addDish(@RequestBody DishDto dishDto){
        if(dishDto==null){
            return Res.error("Error：received non-info");
        }
        dishService.saveDish(dishDto);
        return Res.success("Success: dish saved");
    }

    @GetMapping("/page")
    public Res<Page> getDishPage(Integer page, Integer pageSize, String name){
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new PageDTO<>();

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Dish::getName,name);
        wrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo);

        List<DishDto> dtoList = pageInfo.getRecords().stream().map((item)->{
            Long categoryId = item.getCategoryId();
            String categoryName = categoryService.getCategoryNameById(categoryId);
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        dishDtoPage.setRecords(dtoList);
        return Res.success(dishDtoPage);
    }
    @GetMapping("/{id}")
    public Res<DishDto> getDishById(@PathVariable Long id){
        DishDto dishDto = dishService.getDishWithFlavorsById(id);
        return Res.success(dishDto);
    }

    @PutMapping
    public Res<String> updateDishWithFlavor(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateDishDtoById(dishDto);
        return Res.success("Success: dish info updated");
    }
//    @GetMapping("/list")
//    public Res<List<Dish>> getDishListByCategoryId(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishList = dishService.list(queryWrapper);
//        return Res.success(dishList);
//    }

    @Autowired
    private DishFlavorService dishFlavorService;
    @GetMapping("/list")
    public Res<List<DishDto>> getDishListByCategoryId(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);

        List<DishDto> dishDtos = dishList.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            String categoryName = categoryService.getCategoryNameById(item.getCategoryId());
            List<DishFlavor> flavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,item.getId()));
            dishDto.setFlavors(flavors);
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        return Res.success(dishDtos);
    }

}
