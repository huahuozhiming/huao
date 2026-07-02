package com.example.service;

import com.example.pojo.dto.SaveResourceDTO;
import com.example.pojo.dto.UpdateResourceDTO;
import com.example.pojo.vo.PageResult;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ResourceService {
    //教师分页查询资源
    PageResult pageFindTeacher(Integer current, Integer pageSize, Integer id, String name,
                               Integer courseId, String courseName,Integer type);

    PageResult pageFindStudent(Integer current, Integer pageSize, Integer id, String name,
                               Integer courseId, String courseName, Integer type);

//    教师添加资源
    Integer saveResource(SaveResourceDTO dto);

//    教师更新资源
    int updateResource(UpdateResourceDTO dto);

//    教师删除资源
    int deleteResource(Integer id);

//    下载资源
    ResponseEntity<Resource> downloadResource(Integer id);


}
