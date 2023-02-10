package com.example.reggie.common;

public class BaseContext {
    private static ThreadLocal<Long> localId = new ThreadLocal<>();
    public static void setCurrentId(Long currentId){
        localId.set(currentId);
    }
    public static Long getCurrentId(){
        return localId.get();
    }
}
