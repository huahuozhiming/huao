package com.example.constant;

import org.springframework.stereotype.Component;

@Component
public class Constant {

    //作业上传路径
    public static String UPLOAD_PATH="./work/";
    //女
    public final static Integer girl=0;
    //男
    public final static Integer boy=1;
    //课件
    public final static Integer courseWare=1;
    //试卷
    public final static Integer examPaper=2;
    //资料
    public final static Integer materials=3;
    //课本
    public final static Integer textbook=4;
    //资源上传路径
    public static String RESOURCE_PATH="./resource/";
    //资源图片上传路径
    public static String PICTURE_PATH="./picture/";
}
