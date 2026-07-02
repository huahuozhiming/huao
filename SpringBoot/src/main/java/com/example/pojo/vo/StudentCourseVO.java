package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StudentCourseVO {

    @Schema(name = "id",description = "课程号")
    private Integer id;

    @Schema(name = "name",description = "课程名称")
    private String name;

    @Schema(name = "teacherName",description = "授课老师")
    private String teacherName;

    @Schema(name = "courseStatus",description = "0为进行中，1为批阅中，2为完成,默认0")
    private Integer courseStatus;

    @Schema(name = "termPeriod",description = "学期时间段")
    private String termPeriod;

    @Schema(name = "description",description = "课程描述")
    private String description;

    @Schema(name = "credit",description = "学分")
    private String credit;
}
