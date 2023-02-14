package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.CustomException;
import com.example.reggie.common.Res;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import kotlin.jvm.internal.Lambda;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Res<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return Res.success("Success: set meal saved");
    }
    @GetMapping("/page")
    public Res<Page<SetmealDto>> page(Integer page, Integer pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null, Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        List<SetmealDto> dtoList = pageInfo.getRecords().stream().map((item)->{
            Long categoryId = item.getCategoryId();
            String categoryName = categoryService.getCategoryNameById(categoryId);

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoList);
        return Res.success(dtoPage);
    }


    @DeleteMapping
    public Res<String> deleteSetmeal(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return Res.success("Success: deleted");
    }

    @PostMapping("/status/{status}")
    public Res<String> batchUpdateStatus(@PathVariable Integer status, @RequestParam List<Long> ids){
        for(Long id:ids){
            Setmeal setmeal = setmealService.getById(id);
            if(status == 0)
                setmeal.setStatus(0);
            else
                setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }
        return Res.success("Success: status batch updated");
    }
}
