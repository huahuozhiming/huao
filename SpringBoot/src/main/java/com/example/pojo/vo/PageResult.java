package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult {

    @Schema(name = "current",description = "当前页")
    private Long current;

    @Schema(name = "total",description = "总页数")
    private Long total;

    @Schema(name = "pageSize",description = "每页大小")
    private Long pageSize;

    @Schema(name = "data",description = "每页内容")
    private Object data;
}
