package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaveCourseDTO {

    @Schema(name = "name",description = "课程名称")
    private String name;

    @Schema(name = "description",description = "课程描述")
    private String description;

    @Schema(name = "termPeriod",description = "学期时间段")
    private String termPeriod;
}
