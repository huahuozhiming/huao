package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Teacher {

    @TableId(type = IdType.INPUT,value = "teacher_id")
    @Schema(name = "id",description = "工号")
    private Integer id;

    @Schema(name = "password",description = "密码")
    private String password;

    @Schema(name = "name",description = "老师名字")
    private String name;

    @Schema(name = "sex",description = "性别")
    private Integer sex;
}
