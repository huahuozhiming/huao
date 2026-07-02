package com.example.service;

import com.example.pojo.dto.LoginDTO;
import com.example.pojo.entity.Student;

public interface StudentService {
    Student login(LoginDTO loginDTO);

    Integer register(Student student);
}
