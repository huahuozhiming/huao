package com.example.service.iml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.BaseContext;
import com.example.constant.Status;
import com.example.mapper.CourseMapper;
import com.example.mapper.CreditMapper;
import com.example.pojo.entity.Course;
import com.example.pojo.entity.Credit;
import com.example.pojo.vo.CreditVO;
import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.StudentCourseVO;
import com.example.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CreditServiceImpl implements CreditService {

    @Autowired
    CreditMapper creditMapper;
    @Autowired
    private CourseMapper courseMapper;

    //教师端分页查询学分
    @Override
    public PageResult FindCredit(Long current, Long pageSize, Integer courseId,Integer teacherId,Integer studentId,Integer courseStatus) {
        Page<CreditVO> page=new Page<>(current,pageSize);
        IPage<CreditVO> ipage = creditMapper.FindCredit(page, courseId, teacherId, studentId,courseStatus);
        return new PageResult(ipage.getCurrent(),ipage.getTotal(),ipage.getSize(),ipage.getRecords());
    }

    //教师修改学分 （当课程不为已完成状态无法修改学分）
    @Override
    public Integer changeCredit(Integer studentId, Integer courseId, String credit) {
        Course course = courseMapper.selectById(courseId);
        if (!course.getCourseStatue().equals(Status.inProgress) ) {
            LambdaUpdateWrapper<Credit> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(Credit::getCourseId, courseId)
                    .eq(Credit::getStudentId, studentId)
                    .set(Credit::getCredit, credit);
            return creditMapper.update(wrapper);
        }else throw new RuntimeException("课程仍在进行无法修改学分");
    }



    //学生端查询自己的课程及学分
    @Override
    public PageResult pageFind(String name, Long current, Long pageSize, Integer id,Integer courseId, Integer statue) {

        Page<StudentCourseVO> page = new Page<>(current,pageSize);
        IPage<StudentCourseVO> iPage = creditMapper.pageFind(page, courseId, name, statue, BaseContext.get());
        return new PageResult(iPage.getCurrent(),
                iPage.getTotal(),
                iPage.getSize(),
                iPage.getRecords());
    }

    //    学生选课 （会查询credit表来确保这个课不会被选两次）
    @Override
    public Integer saveCourse(Integer courseId, Integer studentId) {

        Credit credit0 = creditMapper.selectByKeys(studentId, courseId);
        if (credit0==null){
            Credit credit = new Credit();
            credit.setCourseId(courseId);
            credit.setStudentId(studentId);
            return creditMapper.insert(credit);
        }
        return -1;
    }

    //    学生退课仅仅删除对应的学分表
    @Override
    public Integer deleteCourse(Integer id,Integer studentId) {

        Credit credit = new Credit();
        credit.setCourseId(id);
        credit.setStudentId(studentId);
        LambdaQueryWrapper<Credit> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Credit::getStudentId,studentId)
                .eq(Credit::getCourseId,id);
        return creditMapper.delete(wrapper);
    }


}
