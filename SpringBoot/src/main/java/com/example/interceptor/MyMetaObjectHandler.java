package com.example.interceptor;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.constant.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "courseStatue", Integer.class, Status.inProgress);
        log.info("自动填充属性{}:{}","courseStatue", Status.inProgress);
        this.strictInsertFill(metaObject, "workStatus", Integer.class, Status.reviewing);
        log.info("自动填充属性{}:{}","workStatus", Status.reviewing);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        log.info("自动填充属性{}:{}","createTime",LocalDateTime.now());
        this.strictInsertFill(metaObject,"updateTime",LocalDateTime.class,LocalDateTime.now());
        log.info("自动填充属性{}:{}","updateTime",LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject,"updateTime",LocalDateTime.class,LocalDateTime.now());
        log.info("自动填充属性{}:{}","updateTime",LocalDateTime.now());

    }
}
