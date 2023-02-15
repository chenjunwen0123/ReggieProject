package com.example.reggie.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseContext {
    private static ThreadLocal<Long> employeeId = new ThreadLocal<>();

    public static void setCurrentId(Long currentId){
        employeeId.set(currentId);
    }
    public static Long getCurrentId(){
        log.info("current EmployeeId:{}",employeeId.get());
        return employeeId.get();
    }
}
