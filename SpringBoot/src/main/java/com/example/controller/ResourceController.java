package com.example.controller;


import com.example.pojo.dto.FindResourceDTO;
import com.example.pojo.dto.SaveResourceDTO;
import com.example.pojo.dto.UpdateResourceDTO;
import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.Result;
import com.example.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resource")
@Slf4j
public class ResourceController {

    @Autowired
    ResourceService resourceService;

    /**
     *老师分页查询资源
     * FindResourceDTO
     * Integer current,Integer pageSize,Integer id,String name,
     * Integer courseId,String courseName,Integer type
     * ResourceVO
     * Integer id,String name,Long courseId,String courseName,Long teacherId,
     * String teacherName,LocalDateTime updateTime,String position,String type,Long size,
     * String picture,String documentType
     * 采用了卡片的样式增加了图片
     */
    @Operation(summary = "老师分页查询资源")
    @GetMapping("/find/teacher")
    public PageResult findResourceTeacher(FindResourceDTO dto){
        log.info("教师分页资源查询：当前页={}，每页大小={}，资源id={}，资源名称={},所属课程号={}，" +
                        "所属课程名称={}，资源类型{}",
                dto.getCurrent(), dto.getPageSize(),dto.getId(),dto.getName(),
                dto.getCourseId(),dto.getCourseName(), dto.getType());
        PageResult pageResult = resourceService.pageFindTeacher(dto.getCurrent(), dto.getPageSize(),
                dto.getId(), dto.getName(), dto.getCourseId(), dto.getCourseName(), dto.getType());
        log.info("教师分页查询资源完成：总记录数={}，当前页记录={}", pageResult.getTotal(), pageResult.getData());
        return pageResult;
    }

    /**
     * 添加资源
     * SaveResourceDTO
     * MultipartFile file，Integer type，String name，Integer courseId，
     * MultipartFile picture;
     * Integer
     */
    @Operation(summary = "添加资源")
    @PostMapping("/save")
    public Result saveResource(SaveResourceDTO dto){
        log.info("教师新增资源：名称={}，类型={},课程号={},文件={}，图片={}", dto.getName(), dto.getType(), dto.getCourseId(),dto.getFile(),dto.getPicture());
        int result=resourceService.saveResource(dto);
        if(result>0) return Result.success();
        else if (result==-1)return Result.fail("资料文件为空");
        else return Result.fail("未知错误");

    }

    /**
     * 修改资源
     * UpdateResourceDTO
     * Integer id,String name,Integer courseId,MultipartFile file,Integer type;
     * MultipartFile picture;
     * Result
     */
    @Operation(summary = "修改资源")
    @PostMapping("/update")
    public Result updateResource(@ModelAttribute UpdateResourceDTO dto) {
        log.info("教师更新资源：id={}，名称={}，类型={},课程号={},文件={}，图片={}",dto.getId(), dto.getName(), dto.getType(), dto.getCourseId(),dto.getFile(),dto.getPicture());
        int result = resourceService.updateResource(dto);
        if (result > 0) return Result.success();
        return null;
    }

    /**
     * 删除资源
     * Integer id
     * Result
     */
    @Operation(summary = "删除资源")
    @DeleteMapping("/delete")
    public Result deleteResource(Integer id){
        log.info("删除资源其id为{}",id);
        int result=resourceService.deleteResource(id);
        if (result>0) return Result.success();
        else return Result.fail("删除错误");
    }


    /**
     * 资源下载
     * Integer id
     * Result
     */
    @Operation(summary = "下载资源")
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadResource(Integer id){
        log.info("下载资源其id为{}",id);
        return resourceService.downloadResource(id);
    }


    /**
     *学生分页查询资源
     * FindResourceDTO
     * Integer current,Integer pageSize,Integer id,String name,
     * Integer courseId,String courseName,Integer type
     * ResourceVO
     * Integer id,String name,Long courseId,String courseName,Long teacherId,
     * String teacherName,LocalDateTime updateTime,String position,String type,Long size,
     * String picture,String documentType
     */
    @Operation(summary = "学生分页查询资源")
    @GetMapping("/find/student")
    public PageResult findResourceStudent(FindResourceDTO dto){
        log.info("学生分页资源查询：当前页={}，每页大小={}，资源id={}，资源名称={},所属课程号={}，" +
                        "所属课程名称={}，资源类型{}",
                dto.getCurrent(), dto.getPageSize(),dto.getId(),dto.getName(),
                dto.getCourseId(),dto.getCourseName(), dto.getType());
        PageResult pageResult = resourceService.pageFindStudent(dto.getCurrent(), dto.getPageSize(),
                dto.getId(), dto.getName(), dto.getCourseId(), dto.getCourseName(),dto.getType());
        log.info("学生分页查询资源完成：总记录数={}，当前页记录={}", pageResult.getTotal(), pageResult.getData());
        return pageResult;
    }
}
