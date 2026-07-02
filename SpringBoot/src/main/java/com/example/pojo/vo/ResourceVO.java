package com.example.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResourceVO {
    private Integer id;              // 主键ID
    private String name;             // 名称
    private Long courseId;           // 课程ID
    private String courseName;       //课程名称
    private Long teacherId;          // 教师ID
    private String teacherName;       //教师名字
    private LocalDateTime updateTime; // 更新时间
    private String position;         // 资料位置
    private String type;             // 资料类型
    private Long size;                 // 文件大小
    private String picture;        //图片保存位置
    private String documentType;     //资料文件类型
}
