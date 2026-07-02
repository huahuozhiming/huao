package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdateCourseDTO {

    @Schema(name = "id",description = "id作为课程的唯一标识")
    private Integer id;

    @Schema(name = "name",description = "课程名称")
    private String name;

    @Schema(name = "courseStatus",description = "0为进行中，1为批阅中，2为完成")
    private Integer courseStatus;

    @Schema(name = "description",description = "课程描述")
    private String description;

    @Schema(name = "termPeriod",description = "学期时间段")
    private String termPeriod;
}
