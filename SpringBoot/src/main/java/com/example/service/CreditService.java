package com.example.service;

import com.example.pojo.vo.PageResult;

public interface CreditService {

    //教师端分页查询学分
    PageResult FindCredit(Long current,Long pageSize,Integer courseId,Integer teacherId,Integer studentId,Integer courseStatus);

    //教师修改学分 （当课程不为已完成状态无法修改学分）
    Integer changeCredit(Integer studentId, Integer courseId, String credit);

    //学生端查询自己的课程及学分
    PageResult pageFind(String name, Long current, Long pageSize, Integer id,Integer courseId, Integer statue);

    //学生选课 （会查询credit表来确保这个课不会被选两次）
    Integer saveCourse(Integer courseId, Integer studentId);

    //    学生退课仅仅删除对应的学分表
    Integer deleteCourse(Integer courseId,Integer studentId);

}
