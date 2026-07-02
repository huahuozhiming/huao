package com.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Resource {

    @TableId()
    private Integer id;              // 主键ID
    private String name;             // 名称
    private Integer courseId;           // 课程ID
    private Integer teacherId;          // 教师ID
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    private String position;         // 资料位置
    private Integer type;             // 资料类型
    private String picture;  //图片保存位置
    private Long size;           // 资料大小
    private String documentType;     //资料文件类型
}
