package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pojo.entity.Course;
import com.example.pojo.vo.CourseVO;
import com.example.pojo.vo.TeacherCourseVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CourseMapper extends BaseMapper<Course> {

//    老师分页查询自己教的课程
    IPage<TeacherCourseVO> teacherFind(Page<TeacherCourseVO> page, Integer id, String name,
                                       Integer courseStatus, Integer teacherId);
//    老师教的课程状态为进行中的总数
    @Select("SELECT COUNT(*) FROM course WHERE course_statue = 0 and teacher_id=#{teacherId}")
    Long countByCourseOngoing(Integer teacherId);

//    老师教的课程状态为批阅中的总数
    @Select("SELECT COUNT(*) FROM course WHERE course_statue = 1 and teacher_id=#{teacherId}")
    Long countByCourseReviewing(Integer teacherId);

//    老师教的课程状态为已完成的总数
    @Select("SELECT COUNT(*) FROM course WHERE course_statue = 2 and teacher_id=#{teacherId}")
    Long countByCourseCompleted(Integer teacherId);

//    老师教的课程总数
    @Select("SELECT COUNT(*) FROM course where teacher_id=#{teacherId}")
    Long countCourseTotal(Integer teacherId);

//    学生分页查询所有课程
    Page<CourseVO> findCourse(Page<CourseVO> page, Integer id, String name, @Param("courseStatus")Integer courseStatus, Integer studentId);
}
