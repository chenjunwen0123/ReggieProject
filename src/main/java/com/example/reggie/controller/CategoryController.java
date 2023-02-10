package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.Res;
import com.example.reggie.pojo.Category;
import com.example.reggie.pojo.Dish;
import com.example.reggie.pojo.Employee;
import com.example.reggie.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.startup.Catalina;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;
    @GetMapping("/page")
    public Res<IPage<Category>> page(int page, int pageSize){
        // 构造分页
        IPage<Category> p = new Page<>(page,pageSize);
        // 构造条件
        LambdaQueryWrapper<Category> wrapper= new LambdaQueryWrapper<>();
        // 同一条记录按照更新时间降序排列
        wrapper.orderByDesc(Category::getSort);
        // 分页查询
        categoryService.page(p,wrapper);
        return Res.success(p);
    }

    @PostMapping
    public Res<String> addCategory(@RequestBody Category category){
        log.info("addCategory:{}",category);
        categoryService.save(category);
        return Res.success("Success: Category Saved");
    }
    @DeleteMapping
    public Res<String> deleteCategory(@RequestParam("ids")Long id){
        categoryService.remove(id);
        return Res.success("Success: Category Removed");
    }

    @GetMapping("/{id}")
    public Res<Category> queryCategoryById(@PathVariable Long id){
        Category category = categoryService.getById(id);
        if(category!=null)
            return Res.success(category);
        return Res.error("Error: non-category was found");
    }
    @PutMapping
    public Res<String> editCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return Res.success("Success: Category Updated");
    }
    @GetMapping("/list")
    public Res<List<Category>> getCategoryList(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<Category>().eq(category.getType() != null, Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(queryWrapper);
        return Res.success(categoryList);
    }


}
