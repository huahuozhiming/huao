package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TeacherWorkVO {

    @Schema(name = "id",description = "作业号")
    private Integer id;

    @Schema(name = "name",description = "作业名")
    private String name;

    @Schema(name = "courseId",description = "课程号")
    private Integer courseId;

    @Schema(name = "courseId",description = "课程名")
    private String courseName;

    @Schema(name = "NotCompleteWorkNum",description = "未完成作业的人数")
    private Integer NotCompleteWorkNum;

    @Schema(name = "CompleteWorkNum",description = "完成作业的人数")
    private Integer CompleteWorkNum;

}
