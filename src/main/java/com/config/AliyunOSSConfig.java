package com.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 阿里云OSS上传文件
 *
 * @Author: hongxiaobin
 * @Time: 2024/3/13 15:15
 */
@Slf4j
@Component
public class AliyunOSSConfig {

    /**
     * 生成唯一图片名称
     *
     * @param fileName 真实文件名
     * @return 文件名称
     */
    private static String getFileName(String fileName) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss");
        int index = fileName.lastIndexOf(".");
        if (fileName.isEmpty() || index == -1) {
            throw new RuntimeException("文件上传失败");
        }
        // 获取文件后缀
        String suffix = fileName.substring(index).toLowerCase();
        String date = simpleDateFormat.format(new Date());
        return date + suffix;
    }

    /**
     * 文件上传
     *
     * @param file 要上传的文件
     * @param path 路径 不包含文件名和 bucketName
     * @return 文件访问路径
     */
    public String upload(MultipartFile file, String path, String accessKeyId,String accessKeySecret,String endpoint,String bucketName) {
        String pathPrefix = removeSlash(path);
        String fileName = getFileName(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            InputStream inputStream = file.getInputStream();
            //获取文件名称
            //1 在文件名称里面添加随机唯一值
            if (!Objects.equals(pathPrefix, "")) {
                fileName = bucketName + "/" + pathPrefix + "/" + fileName;
            } else {
                fileName = bucketName + "/" + fileName;
            }
            //oss方法实现上传
            //第一个参数 bucket名称
            //第二个参数 上传到oss文件路径和名称 fileName
            //第三个参数 上传文件输入流
            ossClient.putObject(bucketName, fileName, inputStream);
            ossClient.shutdown();

            //把上传之后文件路径返回
            //需要把上传到阿里云oss路径手动拼接出来
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;

            return url;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件上传失败：{}", fileName);
            return null;
        }
    }

    private String removeSlash(String input) {
        if (input.startsWith("/")) {
            input = input.substring(1);
        }
        if (input.endsWith("/")) {
            input = input.substring(0, input.length() - 1);
        }
        return input;
    }
}
