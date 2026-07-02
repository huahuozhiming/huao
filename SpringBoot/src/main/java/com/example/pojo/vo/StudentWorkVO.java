package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class StudentWorkVO {

    @Schema(name = "id",description = "作业id")
    private Integer id;

    @Schema(name = "name",description = "作业内容而且后面附带日期")
    private String name;

    @Schema(name = "courseId",description = "课程id")
    private Integer courseId;

    @Schema(name = "courseName",description = "课程名称")
    private String courseName;

    @Schema(name = "teacherName",description = "老师名称")
    private String teacherName;
}
