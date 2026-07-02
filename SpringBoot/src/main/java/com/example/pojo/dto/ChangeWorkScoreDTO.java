package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChangeWorkScoreDTO {
    @Schema(name = "id",description = "作业唯一标识")
    private Integer id;

    @Schema(name = "studentId",description = "学号")
    private Integer studentId;

    @Schema(name = "workStatus",description = "作业是否被批阅")
    private Integer workStatus;

    @Schema(name = "score",description = "分数")
    private String score;
}
