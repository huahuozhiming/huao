package com.example.service;

import com.example.pojo.dto.LoginDTO;
import com.example.pojo.entity.Teacher;

public interface TeacherService {

    Teacher login(LoginDTO teacherLoginDTO);

    Integer register(Teacher teacher);

}
