package com.example.service.iml;

import com.example.pojo.entity.Message;
import com.example.pojo.entity.Prompt;
import com.example.service.AiService;
import com.example.service.WorkService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Value("${custom.key}")
    String key;
    @Autowired
    WorkService workService;

    @Override
    public String analysisWork(Integer workId,Integer studentId) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
        httpPost.addHeader("Authorization", "Bearer " + key);
        httpPost.addHeader("Content-Type", "application/json");
        ResponseEntity<Resource> download = workService.download(workId,studentId);
        String file = fileToString(download);
        Prompt prompt = new Prompt();
        prompt.setModel("deepseek-r1");
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", "You are a helpful assistant."));
        messages.add(new Message("user", file+"上面是我的作业请帮我批改最后返回题目及正确答案，建议，分数"));
        prompt.setMessages(messages);
        prompt.setStream(false);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(prompt);
        System.out.println(s);
        httpPost.setEntity(new StringEntity(s, StandardCharsets.UTF_8));
        try (CloseableHttpResponse response = client.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            System.out.println("状态码: " + statusCode);
            System.out.println("响应体: " + result);
            if (statusCode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(result);
                String assistantContent = root.path("choices").get(0).path("message").path("content").asText();

                System.out.println(assistantContent);
                client.close();
                return assistantContent;
            } else {
                return "出现错误，状态码: " + statusCode + "，响应: " + result;
            }
        }
    }

    public String fileToString(ResponseEntity<Resource> responseEntity) throws IOException {
        Resource resource = responseEntity.getBody();
        if (resource == null) {
            throw new IllegalArgumentException("ResponseEntity body Resource is null");
        }

        // 1. 获取文件名（优先从 Content-Disposition 头获取，其次从 Resource 获取）
        String filename = null;
        ContentDisposition contentDisposition = responseEntity.getHeaders().getContentDisposition();
        if (contentDisposition != null && contentDisposition.getFilename() != null) {
            filename = contentDisposition.getFilename();
        } else {
            filename = resource.getFilename();
        }
        if (filename == null) {
            throw new IllegalArgumentException("无法确定文件名，请确保 Content-Disposition 头或 Resource 提供了文件名");
        }

        // 2. 创建临时文件（保留原始扩展名）
        String tempFileName = "temp_" + System.currentTimeMillis() + "_" + new File(filename).getName();
        Path tempPath = Files.createTempFile(null, "_" + tempFileName);
        File tempFile = tempPath.toFile();
        tempFile.deleteOnExit(); // JVM 退出时自动删除

        // 3. 将 Resource 内容复制到临时文件
        try (InputStream inputStream = resource.getInputStream()) {
            Files.copy(inputStream, tempPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 4. 根据文件扩展名调用相应的解析方法
        String lowerCaseName = filename.toLowerCase();
        String text;
        try {
            if (lowerCaseName.endsWith(".txt")) {
                text = readTextFile(tempFile);
            } else if (lowerCaseName.endsWith(".pdf")) {
                text = readPdfFile(tempFile);
            } else if (lowerCaseName.endsWith(".docx")) {
                text = readDocxFile(tempFile);
            } else {
                throw new UnsupportedOperationException("不支持的文件类型：" + filename);
            }
        } finally {
            // 5. 清理临时文件（可选，deleteOnExit 已保证最终删除，但主动删除可立即释放磁盘）
            Files.deleteIfExists(tempPath);
        }
        return text;
    }
    private static String readTextFile(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), "UTF-8");
    }

    // 2. 读取 PDF 文件
    private static String readPdfFile(File file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file); ) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    // 3. 读取 Word (.docx) 文件
    private static String readDocxFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument doc = new XWPFDocument(fis)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            return extractor.getText();
        }
    }

}