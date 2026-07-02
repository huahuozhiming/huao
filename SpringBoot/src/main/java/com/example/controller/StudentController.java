package com.example.controller;


import com.example.constant.BaseContext;
import com.example.pojo.dto.*;
import com.example.pojo.entity.Student;
import com.example.pojo.vo.LoginVO;
import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.Result;
import com.example.service.CourseService;
import com.example.service.CreditService;
import com.example.service.StudentService;
import com.example.service.WorkService;
import com.example.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/student")
@Slf4j
@Tag(name = "ai辅助教学平台学生端")
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private CreditService creditService;
    @Autowired
    private WorkService workService;
    @Autowired
    private CourseService courseService;

    /**
     *学生登录
     * LoginDTO
     * Integer id，String password
     * LoginVO
     * id，String name，Integer sex，String clazz，String token
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {

        log.info("学号为{}，密码为{}的学生登录", dto.getId(), dto.getPassword());
        Student student = studentService.login(dto);
        String token = jwtUtil.generateToken(student.getId().toString());
        log.info("生成jwtToken{}", token);
        LoginVO vo = new LoginVO();
        BeanUtils.copyProperties(student, vo);
        vo.setToken(token);
        return Result.success(vo);
    }

    /**
     *学生注册
     * Student
     * Integer id，String password，String name，String clazz，Integer sex;
     * Result
     */
    @Operation(summary = "注册")
    @PostMapping("/register")
    public Result register(@RequestBody Student student) {

        log.info("正在注册工号为{}，密码为{}的账户", student.getId(), student.getPassword());
        if (studentService.register(student)==0) return Result.fail("注册失败");
        return Result.success();
    }

    /**
     *分页查询学生课程
     * FindCourseDTO
     * Long current，Long pageSize，Integer id，String name，Integer courseStatus，boolean isAll
     * pageResult
     * isAll控制了是否查询全部课程
     * 查询全部课程为了学生可以选课
     */
    @Operation(summary = "学生分页查询课程")
    @GetMapping("/course/find")
    public Result<PageResult> findCourse(FindCourseDTO dto){

        log.info("分页查询课程：当前页={}，每页大小={}，课程号={}，课程名称={}," +
                        "课程状态={}，课程状态={}，是否查看所有{}",
                dto.getCurrent(), dto.getPageSize(),dto.getId(),dto.getName(),
                dto.getCourseStatus(),dto.getCourseStatus(),dto.isAll());
        PageResult pageResult=new PageResult();
        if (dto.isAll()) {
            pageResult=courseService.findCourse(
                    dto.getCurrent(), dto.getPageSize(), dto.getId(),
                    dto.getName(), dto.getCourseStatus());
        }
        else {
            pageResult = creditService.pageFind(
                    dto.getName(), dto.getCurrent(), dto.getPageSize(),
                    BaseContext.get(), dto.getId(), dto.getCourseStatus());
        }
        log.info("分页查询课程完成：总记录数={}，当前页记录={}",
                pageResult.getTotal(), pageResult.getData());
        return Result.success(pageResult);

    }

    /**
     *学生退课
     * Integer courseId
     * Result
     *无特殊功能
     */
    @Operation(summary = "退课")
    @GetMapping("/course/delete")
    public Result deleteCourse(Integer courseId){

        Integer studentId = BaseContext.get();
        log.info("id为{}的学生退课其id为{}",courseId, studentId);
        Integer result=creditService.deleteCourse(courseId, studentId);
        if (result<=0) return Result.fail("退课失败");
        return Result.success();
    }

    /**
     * 学生选课
     *Integer courseId
     * Result
     * 会查询credit确保这个课不会被选两次
     */
    @Operation(summary = "选课")
    @GetMapping("/course/save")
    public Result saveCourse(Integer courseId){

        Integer studentId = BaseContext.get();
        log.info("id为{}的学生选课其id为{}",studentId,courseId);
        Integer result=creditService.saveCourse(courseId, studentId);
        if (result==-1) return Result.fail("该课程已经选过了");
        else if (result>0) return Result.success();
        else return Result.fail("选课失败");
    }


    /**
     * 学生分页查看作业
     * FindWorkStudentDTO
     * Long current，Long pageSize，Integer id，String name，Integer courseId，String courseName
     * StudentWorkVO
     * Integer id,String name,Integer courseId,String courseName,String teacherName;
     */
    @Operation(summary = "学生分页查看作业")
    @GetMapping("/work/search")
    public Result<PageResult> searchWorkStudent(FindWorkStudentDTO dto){

        log.info("分页查询学生的作业：当前页={}，每页大小={}，作业id={}，作业名称={},课程号={}，课程名={}",
                dto.getCurrent(), dto.getPageSize(),dto.getId(),dto.getName(),
                dto.getCourseId(),dto.getCourseName());
          PageResult  pageResult = workService.searchWorkStudent(dto.getCurrent(), dto.getPageSize(), dto.getId(),
                    dto.getName(), dto.getCourseId(), dto.getCourseName());
        log.info("分页查询课程完成：总记录数={}，当前页记录={}", pageResult.getTotal(), pageResult.getData());
        return Result.success(pageResult);
    }

    /**
     * 学生分页查看自己作业
     * FindWorkStudentDTO2
     * Long current,Long pageSize,Integer id,String name,Integer courseId,String courseName,Integer workStatus
     * StudentWorkVO2
     * Integer id，String name，Integer courseId，String courseName，
     * String teacherName，Integer workStatus，String score
     */
    @Operation(summary = "学生分页查看自己作业")
    @GetMapping("/work/find")
    public Result<PageResult> findWorkStudent(FindWorkStudentDTO2 dto){

        log.info("分页查询学生自己作业：当前页={}，每页大小={}，作业id={}，作业名称={},课程号={}，课程名={},课程状态={}",
                dto.getCurrent(), dto.getPageSize(),dto.getId(),dto.getName(),
                dto.getCourseId(),dto.getCourseName(),dto.getWorkStatus());
        PageResult  pageResult = workService.findWorkStudent(dto.getCurrent(), dto.getPageSize(), dto.getId(),
                dto.getName(), dto.getCourseId(), dto.getCourseName(),dto.getWorkStatus());
        log.info("分页查询课程完成：总记录数={}，当前页记录={}", pageResult.getTotal(), pageResult.getData());
        return Result.success(pageResult);
    }

    /**
     * 学生提交作业
     * MultipartFile file,Integer workId,Integer courseId
     * Result
     */
    @Operation(summary = "提交作业")
    @PostMapping("/work/save")
    public Result saveWork(MultipartFile file,Integer workId,Integer courseId){

        log.info("学号为{}的同学提交作业号为{}的作业",BaseContext.get(),workId);
        Integer studentId=BaseContext.get();
        Integer result=workService.saveWork(file,workId,courseId,studentId);
        if (result==0) return Result.fail("作业上传失败");
        else if (result==-1) return Result.fail("文件为空");
        else if (result==-2) return Result.fail("作业已经提交，请去修改");
        else if (result<-2) return Result.fail("未知错误");
        return Result.success();
    }

    /**
     * 学生修改作业
     * MultipartFile file,Integer workId
     * Result
     * 当作业已经批改完的时候学生无法修改作业
     */
    @Operation(summary = "修改作业")
    @PostMapping("/work/change")
    public Result updateWork(MultipartFile file,Integer workId){

        log.info("学号为{}的同学修改作业号为{}的作业",BaseContext.get(),workId);
        Integer result=workService.updateWork(file,workId);
        if (result==0) return Result.fail("作业修改失败");
        else if (result==-1) return Result.fail("文件为空");
        else if (result==-2) return Result.fail("您无权限更改当前作业了");
        return Result.success();
    }
}