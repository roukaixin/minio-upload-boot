package com.roukaixin.controller;

import com.roukaixin.minio.pojo.Minio;
import com.roukaixin.pojo.UploadTask;
import com.roukaixin.pojo.dto.FileInfoDTO;
import com.roukaixin.pojo.dto.UploadPart;
import com.roukaixin.service.UploadTaskService;
import com.roukaixin.pojo.R;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author pankx
 * @date 2023/9/10 23:25
 */
@RestController
@RequestMapping("/v1/minio")
@Tag(name = "上传管理模块")
@RequiredArgsConstructor
public class UploadTaskController {

    private final MinioClient minioClient;

    private final Minio minio;

    private final UploadTaskService uploadTaskService;

    @Operation(description = "创建分片上传")
    @PostMapping("/createMultipartUploadId")
    @CrossOrigin
    public R<UploadTask> createMultipartUploadId(@RequestBody FileInfoDTO fileInfoDto){
        return uploadTaskService.createMultipartUploadId(fileInfoDto);
    }

    @Operation(description = "上传分片")
    @PutMapping("/uploadPartAsync")
    @CrossOrigin
    public R<Object> uploadPartAsync(UploadPart uploadPart){
        return uploadTaskService.uploadPartAsync(uploadPart);
    }


    @Operation(description = "合并分片")
    @PostMapping("/completeMultipartUploadAsync")
    @CrossOrigin
    public R<String> completeMultipartUploadAsync(@RequestBody FileInfoDTO fileInfoDto){
        return uploadTaskService.completeMultipartUploadAsync(fileInfoDto);
    }

    @SneakyThrows
    @PutMapping("upload")
    public void upload(MultipartFile file){
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minio.getBucket())
                        .object(file.getOriginalFilename())
                        .stream(file.getInputStream(),file.getSize(),-1)
                        .contentType(file.getContentType())
                .build());
    }
}