package com.ennew.iot.gateway.web.controller;

import cn.hutool.core.io.FileUtil;
import com.ennew.iot.gateway.core.bo.FileBo;
import com.ennew.iot.gateway.core.repository.FileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.kdla.framework.dto.SingleResponse;
import top.kdla.framework.exception.BizException;

import java.util.Locale;

@RestController
@RequestMapping("/file")
@Tag(name = "文件上传")
public class FileController {

    @Autowired
    private FileRepository fileRepository;

    @PostMapping("/static")
    @SneakyThrows
    @Operation(summary = "上传文件")
    public SingleResponse<String> uploadStatic(@RequestPart("file")
                                     @Parameter(name = "file", description = "文件", required = true, style = ParameterStyle.FORM) MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            throw new BizException("filename is null");
        }
         String suffix = FileUtil.extName(filename).toLowerCase(Locale.ROOT);
        if (!"jar".equalsIgnoreCase(suffix)) {
            throw new BizException("Illegal file format : must be jar files");
        }
        FileBo bo = new FileBo(file.getContentType(), filename, file.getInputStream());
        return SingleResponse.buildSuccess(fileRepository.uploadFile(bo));

    }
}
