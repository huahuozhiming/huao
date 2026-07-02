package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FindCreditDTO {

    @Schema(name = "current",description = "当前页")
    private Long current;

    @Schema(name = "pageSize",description = "每页大小")
    private Long pageSize;

    @Schema(name = "courseId",description = "课程id")
    private Integer courseId;

    @Schema(name = "studentId",description = "学生id")
    private Integer studentId;

    @Schema(name = "courseStatus",description = "课程状态")
    private Integer courseStatus;
}
