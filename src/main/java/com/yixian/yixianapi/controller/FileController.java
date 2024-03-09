package com.yixian.yixianapi.controller;

import com.yixian.yixianapi.common.Result;
import com.yixian.yixianapi.constant.MessageConstant;
import com.yixian.yixianapi.utils.AliOssUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {


    @Resource
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(@RequestPart("file") MultipartFile file) {
        log.info("文件上传：{}", file);
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构建新名称
            String objectName = UUID.randomUUID().toString() + extension;
            // 文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
