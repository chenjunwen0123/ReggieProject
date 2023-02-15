package com.example.reggie.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.reggie.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
      log.info("insertFill: MetaObject = " + metaObject);
      strictInsertFill(metaObject,"createTime", LocalDateTime.class,LocalDateTime.now());
      strictInsertFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
      metaObject.setValue("createUser", BaseContext.getCurrentId());
      metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("updateFill: MetaObject = " + metaObject.toString());
        //strictUpdateFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
