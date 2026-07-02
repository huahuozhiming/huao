package com.example.service;

import com.example.pojo.vo.PageResult;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface WorkService {
//    教师端分页查询学生作业
    PageResult pageFind(Long current, Long pageSize, Integer id, String name, Integer workStatus, Integer courseId,Integer studentId,Integer teacherId);

//    教师端分页查询作业
    PageResult workSearch(Long current, Long pageSize, Integer id, String name, Integer courseId);

//    教师发布作业
    Integer setWork(String name,Integer courseId);

//    教师修改作业名称
    Integer changeWorkName(Integer id,String name);

//    教师批阅作业
    Integer changeWorkScore(Integer id,Integer studentId,String score,Integer workStatus);

//    教师删除作业
    Integer deleteWork(Integer id);

//    学生查询作业
    PageResult searchWorkStudent(Long current, Long pageSize, Integer id, String name, Integer courseId,String courseName);

    //学生查询作业
    PageResult findWorkStudent(Long current, Long pageSize, Integer id, String name, Integer courseId,
                               String courseName,Integer courseStatus);

//    学生上传作业
    Integer saveWork(MultipartFile file,Integer workId, Integer courseId,Integer studentId);

//    学生修改作业
    Integer updateWork(MultipartFile file, Integer workId);

//    教师下载作业
    ResponseEntity<Resource> download(Integer workId,Integer studentId);
}
