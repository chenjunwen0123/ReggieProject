package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @PostMapping
    public Res<String> save(@RequestBody DishDto dishDto){
        if(dishDto==null){
            return Res.error("Error：received non-info");
        }

        dishService.saveDish(dishDto);
        return Res.success("Success: dish saved");
    }

    @GetMapping("/page")
    public Res<Page<DishDto>> getDishPage(Integer page, Integer pageSize, String name){
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

        // 策略1 ：全清理
        String keys = "dish_*";
        Set<Object> targetKeys = redisTemplate.keys(keys);
        if(targetKeys != null)
            redisTemplate.delete(targetKeys);

        // 策略2： 指定清理
//        String keys2 = "dish_" + dishDto.getCategoryId() + "_1";
//        Set<Object> targetKeys2 = redisTemplate.keys(keys2);
//        if(targetKeys != null)
//            redisTemplate.delete(targetKeys2);

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
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);

        if(dishDtos != null){
            return Res.success(dishDtos);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);

        dishDtos = dishList.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            String categoryName = categoryService.getCategoryNameById(item.getCategoryId());
            List<DishFlavor> flavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,item.getId()));
            dishDto.setFlavors(flavors);
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, dishDtos, 120, TimeUnit.MINUTES);
        return Res.success(dishDtos);
    }

}
