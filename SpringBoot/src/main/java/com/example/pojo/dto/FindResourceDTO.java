package com.example.pojo.dto;

import lombok.Data;

@Data
public class FindResourceDTO {

    private Integer current;           //当前页
    private Integer pageSize;          //每页大小
    private Integer id;              // 主键ID
    private String name;             // 名称
    private Integer courseId;           // 课程ID
    private String courseName;        //课程名
    private Integer type;             // 资料类型
}
