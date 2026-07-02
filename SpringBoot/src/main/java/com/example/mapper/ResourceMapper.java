package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.pojo.entity.Resource;
import com.example.pojo.vo.ResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
    //教师分页查询资源
    IPage<ResourceVO> pageFindTeacher(IPage<ResourceVO> page,
                               @Param("id") Integer id,
                               @Param("name") String name,
                               @Param("courseId") Integer courseId,
                               @Param("courseName") String courseName,
                               @Param("teacherId") Integer teacherId,
                               @Param("type") Integer type);

    //教师上传资源总数
    @Select("SELECT COUNT(*) FROM resource where teacher_id=#{teacherId}")
    Long countResourceTotal(Integer teacherId);

    //教师上传资源课件总数
    @Select("SELECT COUNT(*) FROM resource WHERE type = 1 and teacher_id=#{teacherId}")
    Long countCourseWare(Integer teacherId);

    //教师上传资源试卷总数
    @Select("SELECT COUNT(*) FROM resource WHERE type = 2 and teacher_id=#{teacherId}")
    Long countExamPaper(Integer teacherId);

    //教师上传资源资料总数
    @Select("SELECT COUNT(*) FROM resource WHERE type = 3 and teacher_id=#{teacherId}")
    Long countMaterials(Integer teacherId);

    //教师上传资源课本总数
    @Select("SELECT COUNT(*) FROM resource WHERE type = 4 and teacher_id=#{teacherId}")
    Long countTextbook(Integer teacherId);

    IPage<ResourceVO> pageFindStudent(IPage<ResourceVO> page,
                                      @Param("id") Integer id,
                                      @Param("name") String name,
                                      @Param("courseId") Integer courseId,
                                      @Param("courseName") String courseName,
                                      @Param("type") Integer type,
                                      Integer studentId);
}
