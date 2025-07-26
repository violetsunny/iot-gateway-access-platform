package com.ennew.iot.gateway.web.excel;

import cn.enncloud.iot.gateway.exception.BizException;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Slf4j
public class ExcelExportUtil {

    private ExcelExportUtil() {
    }

    public static final String XLS_SUFFIX = ".xls";
    public static final String XLSX_SUFFIX = ".xlsx";
    public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";

    public static void excelExport(HttpServletResponse response, InputStream fileInputStream, String name) throws Exception {
        response.setContentType(XLS_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + XLSX_SUFFIX);
        IOUtils.copy(fileInputStream, response.getOutputStream());
    }


    public static void excelExport(HttpServletResponse response, String name, String sheetName, Class<?> t, List<?> data) throws Exception {
        response.setContentType(XLS_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String fileName = URLEncoder.encode(name, StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + XLSX_SUFFIX);
        EasyExcel.write(response.getOutputStream(), t).registerWriteHandler(new TemplateCellWriteHandler(t, 1, 500)).sheet(sheetName).doWrite(data);
    }


    public static void downloadImportErrorReport(HttpServletResponse response, String fileName){
        String tmpPath = System.getProperty("java.io.tmpdir");
        try {
            String path = tmpPath + File.separator+ fileName;
            File file = new File(path);
            if(!file.exists()){
                log.warn("文件不存在， {}", path);
                throw new BizException("文件不存在：" + path);
            }
            response.setContentType(XLS_CONTENT_TYPE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String outputName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment;filename=" + outputName);
            ServletOutputStream outputStream = response.getOutputStream();
            Files.copy(file.toPath(), outputStream);
            if(!file.delete()){
                log.warn("临时文件：" + fileName + " 删除失败");
            }
        } catch (Exception e) {
            throw new BizException("文件导出异常，" + e.getMessage());
        }
    }

}
