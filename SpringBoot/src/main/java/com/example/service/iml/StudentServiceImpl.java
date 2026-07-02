package com.example.service.iml;


import com.example.mapper.StudentMapper;
import com.example.pojo.dto.LoginDTO;
import com.example.pojo.entity.Student;
import com.example.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class StudentServiceImpl implements StudentService {

    @Autowired
    StudentMapper studentMapper;

    @Override
    public Student login(LoginDTO dto) {

        //查询学号获取Student对象
        Student student = studentMapper.selectById(dto.getId());
        log.info("验证账号{}",dto.getId());
        if (student==null) throw new RuntimeException("学号不存在");
        //2验证md5加密密码
        log.info("验证密码{}",dto.getPassword());
        if (!student.getPassword().equals(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes())))
            throw new RuntimeException("密码错误");
        return student;
    }

    @Override
    public Integer register(Student student) {

        log.info("验证学号{}是否存在",student.getId());
        if (studentMapper.selectById(student.getId()) !=null)
            throw new RuntimeException("学号已经存在");
        //加密密码
        student.setPassword(DigestUtils.md5DigestAsHex(student.getPassword().getBytes()));
        return studentMapper.insert(student);
    }
}
