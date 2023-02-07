package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.Res;
import com.example.reggie.pojo.Employee;
import com.example.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
            return Res.error("User Not Found!");
        }
        // 3. 若username存在，则比对password
        if(!emp.getPassword().equals(pwd)){
            return Res.error("Username/Password is not correct!");
        }
        // 4. 若用户密码都正确，查询该user的状态（是否可用）
        if(emp.getStatus() == 0){
            return Res.error("This user is blocked now!");
        }
        // 5. 若合法登陆，获取当前的Session并将当前用户的信息存入
        request.getSession().setAttribute("employee",emp.getId());
        // 6. 返回成功信息
        return Res.success(emp);
    }
}
