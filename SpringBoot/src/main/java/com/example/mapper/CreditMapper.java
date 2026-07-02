package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pojo.entity.Credit;
import com.example.pojo.vo.CreditVO;
import com.example.pojo.vo.StudentCourseVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CreditMapper extends BaseMapper<Credit> {

//    教师课程管理删除对应课程后相应的学分表也要删除
    @Delete("delete from credit where course_id=#{courseId}")
    Integer deleteCredit(Integer courseId);

//    教师端分页查询学分
    IPage<CreditVO> FindCredit(IPage<CreditVO> page,Integer courseId,Integer teacherId,Integer studentId,Integer courseStatus);

//        学生端查询自己的课程
    IPage<StudentCourseVO> pageFind(IPage<StudentCourseVO> page,
                                    @Param("courseId") Integer courseId,
                                    @Param("courseName") String courseName,
                                    @Param("courseStatus") Integer status,
                                    @Param("studentId") Integer studentId);

//    查询某课程的学生总数
    @Select("select count(*) from credit where course_id=#{courseId}")
    Long studentNumByCourseId(Integer courseId);

//    获取某个学分对象
    @Select("select * from credit where student_id=#{studentId} and course_id=#{courseId}")
    Credit selectByKeys(Integer studentId,Integer courseId);

//    老师所教课程的学生总数之和
    Long findStudentTotal(Integer teacherId);
}
