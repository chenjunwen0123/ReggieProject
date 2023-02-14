package com.example.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie.common.CustomException;
import com.example.reggie.dto.SetmealDto;
import com.example.reggie.mapper.SetmealMapper;
import com.example.reggie.pojo.Setmeal;
import com.example.reggie.pojo.SetmealDish;
import com.example.reggie.service.SetmealDishService;
import com.example.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;
    @Transactional
    public void saveWithDish(SetmealDto setmealDto){
        // 保存setmeal表
        this.save(setmealDto);

        // 保存setmeal_dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        // 取出来的表中的SetmealDish对象没有setmeal_id
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    public void removeWithDish(List<Long> ids){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);  // 0:停用 1:启用

        if(this.count(queryWrapper) > 0){
            throw new CustomException("Failed: Setmeal in sale can not be deleted");
        }
        this.removeByIds(ids);
        // 删除setmeal_dish表的条目（根据setmeal_id）
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getSetmealId,ids);

        setmealDishService.remove(wrapper);
    }
}
