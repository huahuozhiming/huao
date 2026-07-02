package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FindWorkStudentDTO {
    @Schema(name = "current",description = "当前页")
    private Long current;

    @Schema(name = "pageSize",description = "每页大小")
    private Long pageSize;

    @Schema(name = "id",description = "作业id")
    private Integer id;

    @Schema(name = "name",description = "作业名")
    private String name;

    @Schema(name = "courseId",description = "查询课程id")
    private Integer courseId;

    @Schema(name = "courseName",description = "课程名")
    private  String courseName;
}
