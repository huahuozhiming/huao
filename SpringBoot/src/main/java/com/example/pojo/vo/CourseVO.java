package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CourseVO {

    @Schema(name = "id",description = "课程号")
    private Integer id;

    @Schema(name = "name",description = "课程名称")
    private String name;

    @Schema(name = "id",description = "课程号")
    private Integer teacherId;

    @Schema(name = "name",description = "课程名称")
    private String teacherName;

    @Schema(name = "courseStatus",description = "0为进行中，1为批阅中，2为完成,默认0")
    private Integer courseStatus;

    @Schema(name = "description",description = "课程描述")
    private String description;

    @Schema(name = "termPeriod",description = "学期时间段")
    private String termPeriod;

    @Schema(name = "studentTotal",description = "参课总人数")
    private Long studentTotal;
}
