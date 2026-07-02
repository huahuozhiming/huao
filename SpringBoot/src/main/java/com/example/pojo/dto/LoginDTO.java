package com.example.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginDTO {

    @Schema(name = "id",description = "id")
    private Integer id;

    @Schema(name = "password",description = "密码")
    private String password;
}
