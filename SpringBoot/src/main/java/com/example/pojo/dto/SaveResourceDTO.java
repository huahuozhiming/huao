package com.example.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SaveResourceDTO {
    private MultipartFile file;
    private Integer type;
    private String name;
    private Integer courseId;
    private MultipartFile picture;
}
