package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class FindCourseDTO {

    @Schema(name = "current",description = "当前页")
    private Long current;

    @Schema(name = "pageSize",description = "每页大小")
    private Long pageSize;

    @Schema(name = "id",description = "课程号")
    private Integer id;

    @Schema(name = "name",description = "课程名")
    private String name;

    @Schema(name = "courseStatus",description = "课程状态")
    private Integer courseStatus;

    @Schema(name = "isAll",description = "是否查看所有课程")
    private boolean isAll;
}
