package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreditVO {

    @Schema(name = "studentId",description = "学号")
    private Integer studentId;

    @Schema(name = "name",description = "姓名")
    private String name;

    @Schema(name = "clazz",description = "专业及班级")
    private String clazz;

    @Schema(name = "sex",description = "性别")
    private Integer sex;

    @Schema(name = "credit",description = "学分")
    private String credit;

    @Schema(name = "courseName",description = "课程名称")
    private String courseName;

    @Schema(name = "courseId",description = "课程id")
    private Integer courseId;

    @Schema(name = "courseStatus",description = "课程状态")
    private Integer courseStatus;

}
