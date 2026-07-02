package com.example.controller;

import com.example.pojo.vo.Result;
import com.example.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    AiService aiService;

    /**
     * ai批阅作业
     * Integer id
     *
     */
    @Operation(summary = "分析作业")
    @GetMapping("/analysis")
    public Result analysisWork(Integer workId,Integer studentId) throws Exception {
        return Result.success(aiService.analysisWork(workId,studentId));
    }
}
