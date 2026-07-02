package com.example.service.iml;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.BaseContext;
import com.example.constant.Status;
import com.example.mapper.CourseMapper;
import com.example.mapper.CreditMapper;
import com.example.pojo.dto.SaveCourseDTO;
import com.example.pojo.dto.UpdateCourseDTO;
import com.example.pojo.entity.Course;
import com.example.pojo.vo.CourseVO;
import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.TeacherCourseVO;
import com.example.service.CourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    private CreditMapper creditMapper;



    @Override
    //教师端课程管理的分页查询
    public PageResult teacherFind(Long current, Long pageSize, Integer id, String name, Integer statue) {
        Page<TeacherCourseVO> page=new Page<>(current,pageSize);
        IPage<TeacherCourseVO> iPage = courseMapper.teacherFind(page, id, name, statue, BaseContext.get());
        return new PageResult(iPage.getCurrent(), iPage.getTotal(), iPage.getSize(), iPage.getRecords());
    }

    @Override
    //老师添加课程
    public Integer saveCourse(SaveCourseDTO dto) {
        Course course=new Course();
        BeanUtils.copyProperties(dto,course);
        course.setTeacherId(BaseContext.get());
        return courseMapper.insert(course);
    }

    @Override
//    老师修改课程
    public int updateCourse(UpdateCourseDTO dto) {
        Course course=new Course();
        BeanUtils.copyProperties(dto,course);
        course.setCourseStatue(dto.getCourseStatus());
        return courseMapper.updateById(course);
    }

    @Override
//    老师删除当状态为已完成的课程还要删除对应的学分表
    public int deleteCourse(Integer id) {
        Course course = courseMapper.selectById(id);
        if (course.getCourseStatue().equals(Status.completed)) {
            Integer result2 = creditMapper.deleteCredit(id);
            int result=courseMapper.deleteById(id);
            return result;
        }else throw new RuntimeException("课程未完成无法删除");
    }

    @Override
    //    学生端课程管理查看所有课程的分页查询
    public PageResult findCourse(Long current, Long pageSize, Integer id, String name, Integer courseStatus) {
        Page<CourseVO> page=new Page<>(current,pageSize);
        IPage<CourseVO> iPage = courseMapper.findCourse(page, id, name, courseStatus, BaseContext.get());
        return new PageResult(iPage.getCurrent(), iPage.getTotal(), iPage.getSize(), iPage.getRecords());
    }
}
