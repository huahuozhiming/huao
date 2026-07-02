package com.example.pojo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Credit {

    @Schema(name = "courseId",description = "课程号")
    private Integer courseId;

    @Schema(name = "studentId",description = "学号")
    private Integer studentId;

    @Schema(name = "credit",description = "学分")
    private String credit;
}
