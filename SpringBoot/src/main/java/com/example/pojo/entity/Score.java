package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Score {
    @Schema(name = "workId",description = "作业唯一标识")
    private Integer workId;

    @Schema(name = "studentId",description = "学号")
    private Integer studentId;

    @Schema(name = "score",description = "分数")
    private String score;

    @Schema(name = "workStatus",description = "作业是否被批阅")
    @TableField(fill = FieldFill.INSERT)
    private Integer workStatus;

    @Schema(name = "position",description = "作业所保存的路径")
    private String position;
}
