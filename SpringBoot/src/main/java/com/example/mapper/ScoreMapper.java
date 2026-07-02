package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.pojo.entity.Score;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

//    教师端分页查询作业显示已完成作业总数
    @Select("select count(*) from score where work_id=#{workId} and work_status=1")
    Integer completeWorkNum(Integer workId);

//    教师批改作业
    @Update("UPDATE score SET score = #{score}, work_status = #{workStatus} " +
            "WHERE work_id = #{workId} AND student_id = #{studentId}")
    Integer updateScore(Score score);

//    教师删除作业对应的学分表也要删除
    @Delete("delete from score where work_id=#{workId}")
    Integer deleteScore(Integer workId);

    //学生端更新作业需要查询作业是否已经提交
    @Select("select * from score where work_id=#{workId} and student_id=#{studentId}")
    Score selectOneByTwoId(Integer workId,Integer studentId);

    //学生端更新作业
    Integer updateOneByTwoId(Score score);

    //老师未批阅作业总数
    Long findWorkReviewing(Integer teacherId);

    //老师已批阅作业总数
    Long findWorkReviewed(Integer teacherId);

    //查询作业分数为0-59的总数
    Long findWork059(Integer teacherId);

    //查询作业分数为60-69的总数
    Long findWork6069(Integer teacherId);

    //查询作业分数为70-79的总数
    Long findWork7079(Integer teacherId);

    //查询作业分数为80-89的总数
    Long findWork8089(Integer teacherId);

    //查询作业分数为90-100的总数
    Long findWork90100(Integer teacherId);
}
