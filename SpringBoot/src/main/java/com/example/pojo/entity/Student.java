package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Student {

    @TableId(type = IdType.INPUT,value = "student_id")
    @Schema(name = "id",description = "学号")
    private Integer id;

    @Schema(name = "password",description = "密码")
    private String password;

    @Schema(name = "name",description = "姓名")
    private String name;

    @Schema(name = "clazz",description = "专业及班级")
    private String clazz;

    @Schema(name = "sex",description = "性别")
    private Integer sex;
}