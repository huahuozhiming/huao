package com.example.service.iml;

import com.example.mapper.TeacherMapper;
import com.example.pojo.dto.LoginDTO;
import com.example.pojo.entity.Teacher;
import com.example.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    TeacherMapper teacherMapper;

    @Override
    public Teacher login(LoginDTO teacherLoginDTO) {

        //查询工号获取Teacher对象
        Teacher teacher = teacherMapper.selectById(teacherLoginDTO.getId());
        log.info("验证账号{}",teacherLoginDTO.getId());
        if (teacher==null) throw new RuntimeException("工号不存在");
        //验证md5加密密码
        log.info("验证密码{}",teacherLoginDTO.getPassword());
        if (!teacher.getPassword().equals(DigestUtils.md5DigestAsHex(teacherLoginDTO.getPassword().getBytes())))
            throw new RuntimeException("密码错误");
        return teacher;
    }

    @Override
    public Integer register(Teacher teacher) {

        log.info("验证工号{}是否存在",teacher.getId());
        if (teacherMapper.selectById(teacher.getId()) !=null)
            throw new RuntimeException("工号已经存在");
        //加密密码
        teacher.setPassword(DigestUtils.md5DigestAsHex(teacher.getPassword().getBytes()));
        return teacherMapper.insert(teacher);
    }
}
