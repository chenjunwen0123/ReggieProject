package com.example.reggie.controller;

import com.example.reggie.common.Res;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @PostMapping("/upload")
    public Res<String> uploadFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String classPath = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX).getPath();
        String basePath = classPath + "backend/page/common/resources/";
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        String suffix = originalName.substring(originalName.lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString() + suffix;
        log.info("upload: \n originalName={},\n newFilePath={} \n",originalName,basePath + fileName);
        file.transferTo(new File(basePath, fileName));

        return Res.success(fileName);
    }

    @GetMapping("/download")
    public void downloadFile(String name, HttpServletResponse response) throws IOException {
        log.info("filename:{}",name);
        String classPath = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX).getPath();
        String basePath = classPath + "backend/page/common/resources/";
        name = basePath + name;

        FileInputStream inputStream = null;
        ServletOutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(name);
            outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] buffer = new byte[1024];
            // 输入流：读取文件内容
            while((len = inputStream.read(buffer)) != -1){
                // 输出流：将文件内容写回浏览器
                outputStream.write(buffer,0,len);
                outputStream.flush();
            }
        } finally {
            assert outputStream != null;
            outputStream.close();
            inputStream.close();
        }
    }
}
