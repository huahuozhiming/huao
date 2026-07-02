package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChangeWorkNameDTO {

    @Schema(name = "id",description = "作业唯一标识")
    private Integer id;

    @Schema(name = "name",description = "作业名")
    private String name;
}
