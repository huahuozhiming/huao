package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.pojo.entity.Work;
import com.example.pojo.vo.*;

public interface WorkMapper extends BaseMapper<Work> {
    //    教师端分页查询学生作业
    IPage<WorkVO> pageFind(Page<WorkVO> page, Integer id, String name,
                   Integer workStatus, Integer courseId, Integer studentId,
                   Integer teacherId);

    //    教师端分页查询作业
    IPage<WorkVO> searchWork(Page<WorkVO> page, Integer id, String name, Integer courseId,Integer teacherId);

    // 老师布置所有作业的总数
    Long workTotal(Integer teacherId);

    //学生查询作业
    IPage<StudentWorkVO> searchWorkStudent(Page<StudentWorkVO> page, Integer id, String name,
                                           Integer courseId,String courseName,Integer studentId);

    //学生查询自己作业
    IPage<StudentWorkVO2> findWorkStudent(Page<StudentWorkVO2> page, Integer id, String name,
                                          Integer courseId, String courseName,Integer workStatus,
                                          Integer studentId);


}
