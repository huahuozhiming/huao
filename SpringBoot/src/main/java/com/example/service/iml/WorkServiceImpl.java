package com.example.service.iml;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.BaseContext;
import com.example.constant.Constant;
import com.example.constant.Status;
import com.example.mapper.*;
import com.example.pojo.entity.Score;
import com.example.pojo.entity.Work;
import com.example.pojo.vo.*;
import com.example.service.WorkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class WorkServiceImpl implements WorkService {

    @Autowired
    WorkMapper workMapper;
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private CreditMapper creditMapper;


    @Override
    //    教师端分页查询学生作业
    public PageResult pageFind(Long current, Long pageSize, Integer id, String name,
                               Integer workStatus,Integer courseId, Integer studentId,Integer teacherId) {
        Page<WorkVO> page=new Page<>(current,pageSize);
        IPage<WorkVO> iPage= workMapper.pageFind(page, id, name, workStatus, courseId, studentId, teacherId);
        return new PageResult(iPage.getCurrent(), iPage.getTotal(), iPage.getSize(), iPage.getRecords());
    }

    @Override
    //    教师端分页查询作业
    public PageResult workSearch(Long current, Long pageSize, Integer id, String name, Integer courseId) {
        Page<WorkVO> page=new Page<>(current,pageSize);
        IPage<WorkVO> iPage = workMapper.searchWork(page, id, name, courseId, BaseContext.get());
        int pageNum=0;
        if (current<=iPage.getTotal()/iPage.getSize()) pageNum= Math.toIntExact(iPage.getSize());
        else pageNum= Math.toIntExact(iPage.getTotal() % iPage.getSize());
        List<TeacherWorkVO> vos=new ArrayList<>();
        List<WorkVO> records = iPage.getRecords();
        for (int i=0;i<pageNum;i++){
            TeacherWorkVO vo=new TeacherWorkVO();
            BeanUtils.copyProperties(records.get(i),vo);
            vo.setCompleteWorkNum(scoreMapper.completeWorkNum(vo.getId()));
            vo.setNotCompleteWorkNum((int) (creditMapper.studentNumByCourseId(vo.getCourseId()) - vo.getCompleteWorkNum()));
            vos.add(vo);
            }
        return new PageResult(iPage.getCurrent(),iPage.getTotal(),iPage.getSize(),vos);
    }


    @Override
    //教师发布作业
    public Integer setWork(String name,Integer courseId) {

        Work work=new Work();
        work.setName(name);
        work.setCourseId(courseId);
        return workMapper.insert(work);
    }


    @Override
    //教师修改作业名称
    public Integer changeWorkName(Integer id, String name) {
        Work work = new Work();
        work.setId(id);
        work.setName(name);
        return workMapper.updateById(work);
    }


    @Override
    //    教师批阅作业
    public Integer changeWorkScore(Integer id, Integer studentId, String score, Integer workStatus) {

        Score score1=new Score();
        score1.setWorkId(id);
        score1.setStudentId(studentId);
        if (workStatus.equals(Status.reviewing)){
            score1.setScore(null);
            score1.setWorkStatus(Status.reviewing);
        }else {
            score1.setScore(score);
            score1.setWorkStatus(Status.reviewed);
        }
        return scoreMapper.updateScore(score1);
    }


    @Override
    //教师删除作业   删除对应成绩表在controller层写了
    public Integer deleteWork(Integer id) {
        return workMapper.deleteById(id);
    }

    @Override
    //    学生查询作业
    public PageResult searchWorkStudent(Long current, Long pageSize, Integer id, String name, Integer courseId, String courseName) {
        Page<StudentWorkVO> page=new Page<>(current,pageSize);
        IPage<StudentWorkVO> iPage = workMapper.searchWorkStudent(page, id, name, courseId, courseName,BaseContext.get());
        return new PageResult(iPage.getCurrent(),iPage.getTotal(),iPage.getSize(),iPage.getRecords());
    }

    @Override
//    学生查询自己的作业
    public PageResult findWorkStudent(Long current, Long pageSize, Integer id, String name, Integer courseId, String courseName, Integer courseStatus) {
        Page<StudentWorkVO2> page=new Page<>(current,pageSize);
        IPage<StudentWorkVO2> iPage=workMapper.findWorkStudent(page,id,name,courseId,courseName,courseStatus,BaseContext.get());
        return new PageResult(iPage.getCurrent(),iPage.getTotal(),iPage.getSize(),iPage.getRecords());
    }

    @Override
    //学生提交作业
    public Integer saveWork(MultipartFile file,Integer workId, Integer courseId,Integer studentId) {

        //判断文件是否为空
        if (file.isEmpty()) return -1;
        //判断文件是否存在
        LambdaQueryWrapper<Score> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(Score::getStudentId,studentId).eq(Score::getWorkId,workId);
        Long result = scoreMapper.selectCount(wrapper);
        if (result>=1) return -2;
        //添加作业
        Score score=new Score();
        score.setWorkId(workId);
        score.setStudentId(studentId);
        Path uploadPath = Paths.get(Constant.UPLOAD_PATH).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)){
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String originalFilename = file.getOriginalFilename();
        String filename = System.currentTimeMillis()+"_"+ originalFilename;
        Path path=uploadPath.resolve(filename).normalize();
        try {
            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        score.setPosition(path.toString());
        return scoreMapper.insert(score);
    }

    @Override
    //学生修改作业
    public Integer updateWork(MultipartFile file, Integer workId) {

        if (file.isEmpty()) return -1;
        Score score0 = scoreMapper.selectOneByTwoId(workId,BaseContext.get());
        Score score=new Score();
        //作业批阅前可以修改已批阅则无法修改
        if (score0.getWorkStatus()!=0) return -2;
        Path uploadPath = Paths.get(Constant.UPLOAD_PATH).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)){
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String originalFilename = file.getOriginalFilename();
        String filename = System.currentTimeMillis()+"_"+ originalFilename;
        Path path=uploadPath.resolve(filename).normalize();

        try {
            file.transferTo(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        score.setWorkId(workId);
        score.setStudentId(BaseContext.get());
        score.setPosition(path.toString());
        return scoreMapper.updateOneByTwoId(score);
    }

    @Override
    public ResponseEntity<Resource> download(Integer workId, Integer studentId) {
        Score score = scoreMapper.selectOneByTwoId(workId, studentId);
        if (Objects.isNull(score)) {
            return ResponseEntity.notFound().build();
        }
        String filePath = score.getPosition();
        if (org.apache.commons.lang3.StringUtils.isBlank(filePath)) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(filePath).normalize();
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                // 【改动1】从文件路径中提取原始文件名（含后缀）
                String originalFilename = resource.getFilename();
                if (originalFilename == null) {
                    originalFilename = "download";
                }

                // 【改动2】同时提供 filename 和 filename*，兼容所有浏览器/前端
                String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8)
                        .replace("+", "%20");

                // 【改动3】根据后缀动态设置 Content-Type（可选但推荐）
                MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
                String lowerName = originalFilename.toLowerCase();
                if (lowerName.endsWith(".pdf")) {
                    contentType = MediaType.APPLICATION_PDF;
                } else if (lowerName.endsWith(".docx")) {
                    contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                } else if (lowerName.endsWith(".doc")) {
                    contentType = MediaType.parseMediaType("application/msword");
                } else if (lowerName.endsWith(".txt")) {
                    contentType = MediaType.TEXT_PLAIN;
                } else if (lowerName.endsWith(".zip")) {
                    contentType = MediaType.parseMediaType("application/zip");
                }
                // 其他类型保持 APPLICATION_OCTET_STREAM 即可

                return ResponseEntity.ok()
                        .contentType(contentType)
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + encodedFilename + "\"; " +
                                        "filename*=UTF-8''" + encodedFilename)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Download file exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
