package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommentController {

    @Autowired
    private AliOssUtil aliOssUtil;


    /**
     * 定义文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {//这里的MultipartFile是spring提供的一个接口，表示上传的文件，
    log.info("文件上传:{}", file);

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //获取文件后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成新的文件名，使用UUID随机生成一个字符串，避免文件名重复导致文件被覆盖
            String fileName = UUID.randomUUID().toString() + suffix;
            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败:{}", e.getMessage());
        }
        return Result.error("文件上传失败");
    }
}
