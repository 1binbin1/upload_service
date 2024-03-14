package com.controller;

import com.common.BaseResponse;
import com.common.ErrorCode;
import com.common.ResultUtils;
import com.config.AliyunOSSConfig;
import com.exception.BusinessException;
import com.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: hongxiaobin
 * @Time: 2024/3/13 21:13
 */
@RestController
@RequestMapping
public class UploadController {
    @Resource
    private AliyunOSSConfig aliyunOSSConfig;

    /**
     * 阿里云OSS
     * @param file
     * @param path
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @param bucketName
     * @return
     */
    @PostMapping("/aliyun")
    public BaseResponse<String> upload(@RequestBody MultipartFile file,
                                       @RequestParam String path,
                                       @RequestHeader("accessKeyId") String accessKeyId,
                                       @RequestHeader("accessKeySecret") String accessKeySecret,
                                       @RequestHeader("endpoint") String endpoint,
                                       @RequestHeader("bucketName") String bucketName) {
        if (file == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!FileUtils.checkFileSize(file.getSize(), 1, "G")) {
            throw new BusinessException(ErrorCode.FILE_SIZE_ERROR);
        }
        if (StringUtils.isAnyBlank(accessKeyId,accessKeySecret,endpoint,bucketName)) {
            throw new BusinessException(ErrorCode.HEAR_ERROR);
        }
        String url = aliyunOSSConfig.upload(file, path, accessKeyId, accessKeySecret, endpoint, bucketName);
        return ResultUtils.success(url);
    }
}
