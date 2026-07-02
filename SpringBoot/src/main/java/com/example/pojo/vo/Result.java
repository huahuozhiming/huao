package com.example.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result <T>{

    @Schema(name = "code",description = "0代表成功，1代表失败")
    private Integer code;

    @Schema(name = "data",description = "返回数据")
    private T data;

    @Schema(name = "message",description = "返回信息")
    private String message;

    public static Result<Void> success(){
        return new Result<Void>(0,null,"");
    }

    public static <T>Result<T> success(T data){
        return new Result<>(0,data,null);
    }

    public static Result<Void> fail(String message){
        return new Result<Void>(1,null,message);
    }
}
