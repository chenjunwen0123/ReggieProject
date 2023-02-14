package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.reggie.common.Res;
import com.example.reggie.pojo.User;
import com.example.reggie.service.UserService;
import com.example.reggie.utils.SMSUtils;
import com.example.reggie.utils.ValidateCodeUtils;
import kotlin.jvm.internal.Lambda;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public Res<String> sendMsg(@RequestBody User user,HttpSession httpSession){

        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //SMSUtils.sendMessage();
            httpSession.setAttribute("phone",phone);
            httpSession.setAttribute("code",code);
            return Res.success("Success: ValidateCode Sent");
        }
        return Res.error("Failed: ValidateCode sent failed");
    }

    @PostMapping("/login")
    public Res<User> login(@RequestBody Map<String,Object> userMap, HttpSession session){
        String phone = userMap.get("phone").toString();
        String code = userMap.get("code").toString();
        Object phoneInSession = session.getAttribute("phone");
        Object codeInSession = session.getAttribute("code");

        // 同时校验手机和验证码是否对应
        if( (phoneInSession!=null && phoneInSession.equals(phone))
                && (codeInSession != null && codeInSession.equals(code)) ){

            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("userSession",user.getId());
            return Res.success(user);
        }
        return Res.error("Error: Login failed");
    }
}
