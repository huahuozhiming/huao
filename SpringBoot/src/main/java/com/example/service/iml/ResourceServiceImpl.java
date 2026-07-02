package com.example.service.iml;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.constant.BaseContext;
import com.example.constant.Constant;
import com.example.mapper.ResourceMapper;
import com.example.pojo.dto.SaveResourceDTO;
import com.example.pojo.dto.UpdateResourceDTO;
import com.example.pojo.entity.Resource;
import com.example.pojo.vo.PageResult;
import com.example.pojo.vo.ResourceVO;
import com.example.service.ResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Component
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    ResourceMapper resourceMapper;


    @Override
    //教师分页查询资源
    public PageResult pageFindTeacher(Integer current, Integer pageSize, Integer id, String name,
                               Integer courseId, String courseName,Integer type) {
        Page<ResourceVO> page=new Page<>(current,pageSize);
        IPage<ResourceVO> iPage = resourceMapper.pageFindTeacher(page, id, name, courseId, courseName,BaseContext.get(),type);
        return new PageResult(iPage.getCurrent(),iPage.getTotal(),iPage.getSize(),iPage.getRecords());
    }

    @Override
//    教师上传资源
    public Integer saveResource(SaveResourceDTO dto) {

        MultipartFile file = dto.getFile();
        if (file.isEmpty()) {
            return -1;
            //文件为空
        }

        Resource resource = new Resource();
        Path path = Paths.get(Constant.RESOURCE_PATH).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (dto.getName() != null) resource.setName(dto.getName());

        String name=System.currentTimeMillis()+file.getOriginalFilename();
        Path filepath = path.resolve(name).normalize();
        try {
            file.transferTo(filepath.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resource.setCourseId(dto.getCourseId());
        resource.setTeacherId(BaseContext.get());
        resource.setPosition(filepath.toString());
        resource.setType(dto.getType());
        resource.setSize(file.getSize());
        resource.setDocumentType(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1));
        if (dto.getPicture()!=null) {
            Path path2 = Paths.get(Constant.PICTURE_PATH).toAbsolutePath().normalize();
            if (!Files.exists(path2)) {
                try {
                    Files.createDirectories(path2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            String name2=System.currentTimeMillis()+dto.getPicture().getOriginalFilename();
            Path picturePath = path2.resolve(name2).normalize();
            try {
                dto.getPicture().transferTo(picturePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            resource.setPicture(picturePath.toString());
        }
        resource.setSize(dto.getFile().getSize());
        return resourceMapper.insert(resource);
    }


    @Override
//    教师修改资源
    public int updateResource(UpdateResourceDTO dto) {
        Resource resource = new Resource();
        if (dto.getFile()!=null){
            Path path = Paths.get(Constant.RESOURCE_PATH).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            String name = System.currentTimeMillis() + dto.getFile().getOriginalFilename();
            Path filepath = path.resolve(name).normalize();
            try {
                dto.getFile().transferTo(filepath.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            resource.setPosition(filepath.toString());
            resource.setSize(dto.getFile().getSize());
            resource.setDocumentType(dto.getFile().getOriginalFilename().substring(dto.getFile().getOriginalFilename().lastIndexOf(".")+1));
        }
        resource.setId(dto.getId());
        if (dto.getCourseId()!=null) resource.setCourseId(dto.getCourseId());
        if (dto.getName() != null) resource.setName(dto.getName());
        resource.setTeacherId(BaseContext.get());
        if (dto.getType()!=null) resource.setType(dto.getType());

        if (dto.getPicture()!=null) {
            Path path2 = Paths.get(Constant.PICTURE_PATH).toAbsolutePath().normalize();
            if (!Files.exists(path2)) {
                try {
                    Files.createDirectories(path2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            String name2=System.currentTimeMillis()+dto.getPicture().getOriginalFilename();
            Path picturePath = path2.resolve(name2).normalize();
            try {
                dto.getPicture().transferTo(picturePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            resource.setPicture(picturePath.toString());

        }
        return resourceMapper.updateById(resource);
    }

    @Override
//    教师删除资源
    public int deleteResource(Integer id) {
        return resourceMapper.deleteById(id);
    }

    @Override
    //资源下载
    public ResponseEntity<org.springframework.core.io.Resource> downloadResource(Integer id) {
        Resource resource1 = resourceMapper.selectById(id);
        if (Objects.isNull(resource1)) {
            return ResponseEntity.notFound().build();
        }
        String filePath = resource1.getPosition();
        if (org.apache.commons.lang3.StringUtils.isBlank(filePath)) {
            return ResponseEntity.notFound().build();
        }
        Path path = Paths.get(filePath).normalize();
        try {
            org.springframework.core.io.Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                // 【改动1】安全获取原始文件名（含后缀）
                String originalFilename = resource.getFilename();
                if (originalFilename == null) {
                    originalFilename = "resource_" + id;
                }

                // 【改动2】URL编码并修正空格，防止前端解码截断导致后缀丢失
                String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8)
                        .replace("+", "%20");

                // 【改动3】根据文件后缀动态推断 Content-Type
                MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
                String lowerName = originalFilename.toLowerCase();
                if (lowerName.endsWith(".pdf")) {
                    contentType = MediaType.APPLICATION_PDF;
                } else if (lowerName.endsWith(".docx")) {
                    contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                } else if (lowerName.endsWith(".doc")) {
                    contentType = MediaType.parseMediaType("application/msword");
                } else if (lowerName.endsWith(".xlsx")) {
                    contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                } else if (lowerName.endsWith(".xls")) {
                    contentType = MediaType.parseMediaType("application/vnd.ms-excel");
                } else if (lowerName.endsWith(".pptx")) {
                    contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
                } else if (lowerName.endsWith(".txt")) {
                    contentType = MediaType.TEXT_PLAIN;
                } else if (lowerName.endsWith(".zip")) {
                    contentType = MediaType.parseMediaType("application/zip");
                } else if (lowerName.matches(".*\\.(png|jpg|jpeg|gif|bmp|webp)")) {
                    contentType = MediaType.IMAGE_JPEG; // 或根据具体后缀精确匹配
                }

                // 【改动4】同时提供 filename 和 filename*，兼容所有浏览器及前端下载库
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

    @Override
//    学生分页查询资源
    public PageResult pageFindStudent(Integer current, Integer pageSize, Integer id, String name,
                                      Integer courseId, String courseName,Integer type) {
        Page<ResourceVO> page=new Page<>(current,pageSize);
        IPage<ResourceVO> iPage = resourceMapper.pageFindStudent(page, id, name, courseId, courseName, type,BaseContext.get());
        return new PageResult(iPage.getCurrent(),iPage.getTotal(),iPage.getSize(),iPage.getRecords());
    }

}
