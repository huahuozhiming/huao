package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Work {

    @TableId()
    @Schema(name = "id",description = "作业唯一标识")
    private Integer id;

    @Schema(name = "name",description = "作业内容而且后面附带日期")
    private String name;

    @Schema(name = "courseId",description = "交的作业所属课程id")
    private Integer courseId;
}
