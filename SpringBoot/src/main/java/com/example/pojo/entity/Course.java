package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Course {

    @Schema(name = "id",description = "课程号")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(name = "name",description = "课程名称")
    private String name;

    @Schema(name = "teacherId",description = "授课老师id")
    private Integer teacherId;

    @Schema(name = "courseStatue",description = "0为进行中，1为批阅中，2为完成,默认0")
    @TableField(fill = FieldFill.INSERT)
    private Integer courseStatue;

    @Schema(name = "termPeriod",description = "学期时间段")
    private String termPeriod;

    @Schema(name = "description",description = "课程描述")
    private String description;

    @Schema(name = "createTime",description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(name = "updateTime",description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
