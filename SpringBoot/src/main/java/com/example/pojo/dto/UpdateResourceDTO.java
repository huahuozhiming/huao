package com.example.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateResourceDTO {
    private Integer id;
    private String name;
    private Integer courseId;
    private MultipartFile file;
    private Integer type;
    private MultipartFile picture;
}
