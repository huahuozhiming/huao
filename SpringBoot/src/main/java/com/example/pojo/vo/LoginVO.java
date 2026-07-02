package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginVO {

    @Schema(name = "id",description = "id")
    private Integer id;

    @Schema(name = "name",description = "名字")
    private String name;

    @Schema(name = "sex",description = "性别")
    private Integer sex;

    @Schema(name = "clazz",description = "班级")
    private String clazz;

    @Schema(name = "token",description = "jwt")
    private String token;

}
