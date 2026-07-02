package com.example.controller;

import com.example.constant.BaseContext;
import com.example.mapper.*;
import com.example.pojo.vo.InformationVO;
import com.example.service.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Autowired
    private WorkService workService;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private WorkMapper workMapper;
    @Autowired
    private CreditMapper creditMapper;
    @Autowired
    private ScoreMapper scoreMapper;

    /**
     *下载作业文件
     * Integer workId, Integer studentId
     * ResponseEntity<Resource>
     */
    @Operation(summary = "下载作业文件")
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadWork(Integer workId, Integer studentId){
        return workService.download(workId,studentId);
    }

    /**
     * 教师端数据统计
     * void
     * InformationVO
     * Long courseTotal，Long workTotal，Long resourceTotal，Long studentTotal;
     * Long cCompleted，Long cInGrading，Long cInProgress;
     * Long wReviewing，Long wReviewed;
     * Long w059。Long w6069，Long w7079，Long w8089，Long w90100;
     * Long courseWare， Long examPaper，Long materials，Long textbook;
     */
    @Operation(summary = "统计管理")
    @GetMapping("/information")
    public InformationVO information(){
        Integer teacherId= BaseContext.get();
        InformationVO vo=new InformationVO();
        vo.setCourseTotal(courseMapper.countCourseTotal(teacherId));
        vo.setWorkTotal(workMapper.workTotal(teacherId));
        vo.setResourceTotal(resourceMapper.countResourceTotal(teacherId));
        vo.setStudentTotal(creditMapper.findStudentTotal(teacherId));
        vo.setCCompleted(courseMapper.countByCourseCompleted(teacherId));
        vo.setCInGrading(courseMapper.countByCourseReviewing(teacherId));
        vo.setCInProgress(courseMapper.countByCourseOngoing(teacherId));
        vo.setWReviewing(scoreMapper.findWorkReviewing(teacherId));
        vo.setWReviewed(scoreMapper.findWorkReviewed(teacherId));
        vo.setW90100(scoreMapper.findWork90100(teacherId));
        vo.setW8089(scoreMapper.findWork8089(teacherId));
        vo.setW7079(scoreMapper.findWork7079(teacherId));
        vo.setW6069(scoreMapper.findWork6069(teacherId));
        vo.setW059(scoreMapper.findWork059(teacherId));
        vo.setMaterials(resourceMapper.countMaterials(teacherId));
        vo.setTextbook(resourceMapper.countTextbook(teacherId));
        vo.setCourseWare(resourceMapper.countCourseWare(teacherId));
        vo.setExamPaper(resourceMapper.countExamPaper(teacherId));
        log.info("{}",vo);
        return vo;
    }
}
