package com.example.service;

import com.example.pojo.dto.SaveCourseDTO;
import com.example.pojo.dto.UpdateCourseDTO;
import com.example.pojo.vo.PageResult;

public interface CourseService{
    //教师端课程管理的分页查询
    PageResult teacherFind(Long current, Long pageSize, Integer id, String name, Integer statue);

    Integer saveCourse(SaveCourseDTO saveCourseDTO);

    int updateCourse(UpdateCourseDTO updateCourseDTO);

    int deleteCourse(Integer id);

//    学生端课程管理查看所有课程的分页查询
    PageResult findCourse(Long current, Long pageSize, Integer id, String name, Integer courseStatus);
}
