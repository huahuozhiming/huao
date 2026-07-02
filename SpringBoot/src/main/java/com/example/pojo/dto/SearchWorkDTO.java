package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SearchWorkDTO {

    @Schema(name = "current",description = "当前页")
    private Long current;

    @Schema(name = "pageSize",description = "每页大小")
    private Long pageSize;

    @Schema(name = "id",description = "作业号")
    private Integer id;

    @Schema(name = "name",description = "作业名")
    private String name;

    @Schema(name = "courseId",description = "课程号")
    private Integer courseId;
}
