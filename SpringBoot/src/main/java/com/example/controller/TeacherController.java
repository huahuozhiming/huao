package com.example.controller;

import com.example.constant.BaseContext;
import com.example.mapper.ScoreMapper;
import com.example.pojo.dto.*;
import com.example.pojo.entity.Teacher;
import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.Result;
import com.example.pojo.vo.LoginVO;
import com.example.service.CourseService;
import com.example.service.CreditService;
import com.example.service.TeacherService;
import com.example.service.WorkService;
import com.example.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teacher")
@Slf4j
@Tag(name = "ai辅助教学平台教师端")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    CourseService courseService;
    @Autowired
    private WorkService workService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CreditService creditService;
    @Autowired
    private ScoreMapper scoreMapper;

    /**
     * 教师登录
     * LoginDTO
     * Integer id,String password
     * LoginVO
     * Integer id，String name，Integer sex，String clazz，String token
     * 先通过id查询teacher表验证教师账号是否存在
     * 然后对密码进行md5解密进行验证
     * 然后在controller层用jwt工具类生成jwt
     * 封装LoginVO 没有密码属性和增加了jwt属性
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {

        log.info("工号为{}，密码为{}的老师登录", dto.getId(), dto.getPassword());
        Teacher teacher=teacherService.login(dto);
        String token = jwtUtil.generateToken(teacher.getId().toString());
        log.info("生成jwt:{}",token);
        LoginVO vo = new LoginVO();
        BeanUtils.copyProperties(teacher, vo);
        vo.setToken(token);
        return Result.success(vo);
    }

    /**
     * 教师注册
     * Teacher
     * Integer id,String password,String name,Integer sex
     * Result
     * 先通过id查询teacher表验证教师账号是否存在
     * 然后对密码进行md5加密添加teacher对象
     */
    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result register(@RequestBody Teacher teacher){

        log.info("正在注册工号为{}，密码为{}的账户", teacher.getId(), teacher.getPassword());
        if (teacherService.register(teacher)==0) return Result.fail("注册失败");
        return Result.success();
    }

    /**
     * 分页查询课程
     * FindCourseDTO
     * Long current,Long pageSize,Integer id,String name,Integer courseStatus
     * CourseVO
     * Integer id，String name，Integer courseStatus，String description;
     *String termPeriod，Long studentTotal，Long workTotal;
     * 查询条件为课程号，课程名称（模糊查询），课程状态
     * 。
     * 返回信息包含课程和学生总数和作业总数设计多表查询应该是分别调用各个mapper,
     * 然后对结果集进行拼装以提供项目可维护性,但本项目比较简单采用了xml子查询
     */
    @Operation(summary = "分页查询课程")
    @GetMapping("/course/find")
    public Result<PageResult> pageFind(FindCourseDTO dto){

        log.info("分页查询课程：当前页={}，每页大小={}，课程号={}，课程名称={},课程状态={}",
                dto.getCurrent(), dto.getPageSize(),dto.getId(),
                dto.getName(),dto.getCourseStatus());
        PageResult pageResult = courseService.teacherFind(dto.getCurrent(), dto.getPageSize(),
                dto.getId(), dto.getName(), dto.getCourseStatus());
        log.info("分页查询课程完成：总记录数={}，当前页记录={}", pageResult.getTotal(), pageResult.getData());
        return Result.success(pageResult);
    }

    /**
     * 增添课程
     * SaveCourseDTO
     * String name,String description,termPeriod;
     * Result
     * 填写课程名描述和学期进行添加
     * 其他属性如create_time采用了mybatis-plus的自动填充属性
     * 增添的主键策略默认为自增
     */
    @Operation(summary = "增填课程")
    @PostMapping("/course/save")
    public Result saveCourse(@RequestBody SaveCourseDTO dto){

        log.info("新增课程：名称={}，学期={},描述={}", dto.getName(), dto.getTermPeriod(),
                dto.getDescription());
        int result = courseService.saveCourse(dto);
        if (result <= 0) return Result.fail("添加失败");
        return Result.success();
    }

    /**
     * 更改课程
     * UpdateCourseDTO
     * Integer id，String name,String courseStatue，String description,String termPeriod;
     * Result
     * 原本设计当状态为已完成后无法进行修改
     * 后发现一旦教师误操作后无法处理删除了该功能
     */
    @Operation(summary = "更改课程")
    @PostMapping("/course/update")
    public Result updateCourse(@RequestBody UpdateCourseDTO dto){
        log.info("更新课程号为{}的课程为：名称={}，状态={}，描述={}，学期={}",
                dto.getId(), dto.getName(), dto.getCourseStatus(),
                dto.getDescription(), dto.getTermPeriod()
        );
        int result = courseService.updateCourse(dto);
        if (result<=0) return Result.fail("更新失败");
        return Result.success();

    }

    /**
     * 删除课程
     * Integer id
     * Result
     * 当状态为已完成才能删除防止误删
     * 还要删除对应的学分表
     */
    @Operation(summary = "删除课程")
    @DeleteMapping("/course/delete")
    public Result deleteCourse(@RequestParam Integer id){
        log.info("删除课程号为{}的课程", id);
        int result = courseService.deleteCourse(id);
        if (result<=0) return Result.fail("删除失败");
        return Result.success();
    }

    /**
     *分页学生查询作业
     * FindWorkDTO
     * Long current,Long pageSize,Integer id,String name,Integer courseId,
     * Integer studentId,Integer workStatus
     * 。
     * WorkVO
     * Integer id;String name;Integer studentId;String studentName;String clazz;Integer courseId;
     * String courseName;Integer workStatus;String score;String position;
     * 查询条件为作业号，作业名称（模糊查询），课程号，学生号，作业状态
     * 作业状态为0或者1代表是否批阅
     * 点击课程管理的操作里的作业按钮条件会自动输入到作业管理的条件框中跳转到作业设置界面
     * 作业设置界面的操作的查看作业按钮会将条件传入学生作业的条件框中（默认查询未批阅的作业)
     * 这些都是由前端完成的
     */
    @Operation(summary = "分页查询学生作业")
    @GetMapping("/work/find")
    public Result workFind(FindWorkDTO dto){

        log.info("分页查询学生作业：当前页={}，每页大小={}，作业id={}，作业名称={},作业状态={}，课程id={}，学生id={}",
                dto.getCurrent(), dto.getPageSize(),dto.getId(),dto.getName(),
                dto.getWorkStatus(),dto.getCourseId(),dto.getStudentId());
        PageResult pageResult = workService.pageFind(dto.getCurrent(), dto.getPageSize(),
                dto.getId(), dto.getName(), dto.getWorkStatus(), dto.getCourseId(),
                dto.getStudentId(), BaseContext.get());
        log.info("分页查询学生作业完成：总记录数={}，当前页记录={}", pageResult.getTotal(), pageResult.getData());
        return Result.success(pageResult);
    }

    /**
     * 分页查询作业
     * SearchWorkDTO
     *Long current，Long pageSize，Integer id，String name，Integer courseId;
     * TeacherWorkVO
     * Integer id，String name，Integer courseId，String courseName
     * Integer NotCompleteWorkNum，Integer CompleteWorkNum;
     * 可以查看未完成的人数和完成的人数，两者之和为学生人数所以不再渲染学生总数
     */
    @Operation(summary = "分页查询作业")
    @GetMapping("/work/search")
    public Result workSearch(SearchWorkDTO dto){

        log.info("分页查询作业：当前页={}，每页大小={}，作业id={}，作业名称={},课程id={}",
                dto.getCurrent(), dto.getPageSize(), dto.getId(), dto.getName(),
                dto.getCourseId());
        PageResult pageResult = workService.workSearch(dto.getCurrent(), dto.getPageSize(), dto.getId(),
                dto.getName(), dto.getCourseId());
        log.info("分页查询作业完成：总记录数={}，当前页记录={}", pageResult.getTotal(), pageResult.getData());
        return Result.success(pageResult);
    }

    /**
     * 发布作业
     * String name，Integer courseId
     * Result
     * 无特殊功能
     */
    @Operation(summary = "发布作业")
    @PostMapping("/work/set")
    public Result setWork(String name,Integer courseId){

        log.info("老师在课程号为{}的课程里发布名为{}的作业",courseId,name);
        Integer result=workService.setWork(name,courseId);
        if (result==0) return Result.fail("作业发布失败");
        return Result.success();
    }

    /**
     * ChangeWorkNameDTO
     * Integer id，String name;
     * Result
     * 无特殊功能
     */
    @Operation(summary = "修改作业")
    @PostMapping("/work/update/name")
    public Result changeWork(@RequestBody ChangeWorkNameDTO dto){

        log.info("对id为{}的作业进行修改其名字为{}",dto.getId(),dto.getName());
        Integer result=workService.changeWorkName(dto.getId(),dto.getName());
        if (result==0) return Result.fail("修改失败");
        return Result.success();
    }

    /**
     *批改作业
     * ChangeWorkScoreDTO
     * Integer id，Integer studentId，Integer workStatus，String score;
     * Result
     * 设置状态为已批阅 可以添加分数
     * 设置状态为未批阅 不可以添加分数
     * 允许已批阅转换到未批阅为了教师的失误操作可以挽回
     */
    @Operation(summary = "批改作业")
    @PostMapping("/work/update")
    public Result changeWork(@RequestBody ChangeWorkScoreDTO dto) {

        log.info("对id为{}的作业进行修改其状态为{}，分数为{}", dto.getId(), dto.getWorkStatus(), dto.getScore());
        Integer result = workService.changeWorkScore(dto.getId(),dto.getStudentId(), dto.getScore(),dto.getWorkStatus());
        if (result == 0) return Result.fail("修改失败");
        return Result.success();
    }


    /**
     *删除作业
     * Integer id
     * Result
     * 删除作业后对应的score表里的相关数据也要删除
     */
    @Operation(summary = "删除作业")
    @PostMapping("/work/delete")
    public Result deleteWork(Integer id){

        log.info("老师删除作业id为{}",id);
        Integer result=workService.deleteWork(id);
        Integer result2=scoreMapper.deleteScore(id);
        if (result<=0) return Result.fail("删除失败");
        return Result.success();
    }


    /**
     *查询学分
     * FindCreditDTO
     * Long current,Long pageSize,Integer courseId,Integer studentId,Integer courseStatus
     * CreditVO
     * Integer studentId，String name，String clazz，Integer sex，String credit
     * String courseName，Integer courseId，Integer courseStatus;
     * 无特殊功能
     */
    @Operation(summary = "查询课程内的学生及其学分")
    @GetMapping("/credit/find")
    public Result<PageResult> FindCredit(FindCreditDTO dto){

        log.info("分页查询学分：当前页={}，每页大小={},课程id={}，学号={}，课程状态={}",
                 dto.getCurrent(), dto.getPageSize(),dto.getCourseId()
                ,dto.getStudentId(),dto.getCourseStatus());
        PageResult result = creditService.FindCredit(dto.getCurrent(), dto.getPageSize(),
                dto.getCourseId(), BaseContext.get(), dto.getStudentId(),dto.getCourseStatus());
        log.info("分页查询学分完成：总记录数={}，当前页记录={}", result.getTotal(), result.getData());
        return Result.success(result);
    }

    /**
     * 修改学分
     * Integer courseId,Integer studentId,String credit
     * Result
     * 只有课程状态为已完成才可以修改学分
     */
    @Operation(summary = "修改学分")
    @PostMapping("/credit/update")
    public Result changeCredit(Integer courseId,Integer studentId,String credit){

        log.info("正在修改学号为{}同学的{}课程号学分为{}",studentId,courseId,credit);
        Integer result=creditService.changeCredit(studentId,courseId,credit);
        if (result==0) return Result.fail("修改失败");
        return Result.success();
    }

    /**
     * 老师给学生退课
     * Integer studentId,Integer courseId
     * Result
     * 无特殊功能
     */
    @Operation(summary = "退课")
    @PostMapping("/credit/delete")
    public Result deleteCredit(Integer studentId,Integer courseId){
        log.info("老师让id为{}的学生退课其id为{}",studentId,courseId);
        Integer result=creditService.deleteCourse(courseId,studentId);
        if (result<=0) return Result.fail("退课失败");
        return Result.success();
    }
}
