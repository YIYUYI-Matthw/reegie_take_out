package com.yapbukeji.reggie.controller;

import com.yapbukeji.reggie.common.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件上传/下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class FileUDLoadController {
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * 接收到的文件会放在一个临时目录中，所以需要转存（操作完成临时文件删除）
     *
     * @param file MF类型是spring-web下的类，变量名和前端传回来的name要一致
     * @return 保存文件名称并回传
     */
    @PostMapping("/upload")
    public ResData<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传：{}", file.getSize());
        // 判定basePath是否存在
        if (!new File(basePath).exists())
            new File(basePath).mkdirs();
        // 使用UUID重新生成，防止重名
        String originalName = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + originalName.substring(originalName.lastIndexOf("."));
        log.info("文件上传：{}", file.getOriginalFilename());
        file.transferTo(new File(basePath + fileName));
        return ResData.success(fileName);
    }

    @GetMapping("/download")
    public void download(@RequestParam("name") String fileName, HttpServletResponse response) throws IOException {
        log.info("图片下载");
        // 输入流根据文件名找到文件
        File file = new File(basePath + fileName);
        if (!file.exists())
            return; // 不存在就啥也没有
        FileInputStream inputStream = new FileInputStream(file);
        // 写回去
        response.setContentType("image/jpeg"); // HTTP报文中的ContentType
        ServletOutputStream outputStream = response.getOutputStream();
        int length;
        byte[] buffer = new byte[1024];
        while (inputStream.read(buffer) != -1) {
            outputStream.write(buffer);
            outputStream.flush();
        }
        inputStream.close();
        outputStream.close();
        log.info("传输完毕");
    }
}
