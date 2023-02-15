package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Res;
import com.example.reggie.pojo.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/hello")
    public Res<String> returnHello(){
        return Res.success("helloworld");
    }
    @PostMapping("/login")
    public Res<Employee> login(HttpServletRequest request,  @RequestBody Employee employee){
        // 1. 解析获取的username和password，将password用MD5加密（因为数据库中的密码是用MD5加密的）
        String pwd = employee.getPassword();
        pwd = DigestUtils.md5DigestAsHex(pwd.getBytes());
        // 2. 比对username：根据收到的username在employee表中查找，看是否有该用户（前提：数据库将username做了唯一性的约束）
        Employee emp = employeeService.getOne(new LambdaQueryWrapper<Employee>().eq(Employee::getUsername, employee.getUsername()));
        if(emp == null){
            return Res.error("Error: User Not Found!");
        }
        // 3. 若username存在，则比对password
        if(!emp.getPassword().equals(pwd)){
            return Res.error("Error: Username/Password is not correct!");
        }
        // 4. 若用户密码都正确，查询该user的状态（是否可用）
        if(emp.getStatus() == 0){
            return Res.error("Error: This user is blocked now!");
        }
        // 5. 若合法登陆，获取当前的Session并将当前用户的信息存入
        request.getSession().setAttribute("employee",emp.getId());
        // 6. 返回成功信息
        return Res.success(emp);
    }

    @PostMapping("/logout")
    public Res<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Res.success("Success: Employee Logout");
    }

    @PostMapping
    public Res<String> save(HttpServletRequest request, @RequestBody Employee employee){

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return Res.success("Success: Userinfo Saved");
    }

    @GetMapping("/page")
    public Res<IPage> page(int page,int pageSize, String name){
        // 构造分页
        IPage<Employee> p = new Page<>(page,pageSize);
        // 构造条件
        LambdaQueryWrapper<Employee> wrapper= new LambdaQueryWrapper<>();
        // name为空的时候condition为false，wrapper为空
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 同一条记录按照更新时间降序排列
        wrapper.orderByDesc(Employee::getUpdateTime);
        // 分页查询
        employeeService.page(p,wrapper);
        return Res.success(p);
    }

    @GetMapping("/{id}")
    public Res<Employee> queryEmployeeById(@PathVariable("id") Long empId){
        log.info("Query Employee By Id:{}",empId);
        Employee employee = employeeService.getById(empId);
        if(employee != null)
            return Res.success(employee);

        return Res.error("Error: Failed to get userinfo");
    }

    @PutMapping
    public Res<String> enableOrDisableEmployee(HttpServletRequest request, @RequestBody Employee employee){
        // 取当前操作的用户,用于更新updateUser属性
        // Long empId = (Long)request.getSession().getAttribute("employee");
        // employee.setUpdateUser(empId);
        //        employee.setUpdateTime(LocalDateTime.now());
        // 提交更新
        employeeService.updateById(employee);
        return Res.success("Success: Status Updated");
    }
}