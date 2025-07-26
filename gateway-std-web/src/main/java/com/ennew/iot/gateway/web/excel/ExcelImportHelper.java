package com.ennew.iot.gateway.web.excel;

import cn.enncloud.iot.gateway.exception.BizException;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.handler.context.SheetWriteHandlerContext;
import com.ennew.iot.gateway.core.bo.ExcelImportErrorBO;
import com.ennew.iot.gateway.web.excel.valid.ExcelInValid;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;


@Slf4j
public class ExcelImportHelper<T, R> extends AnalysisEventListener<T> {

//    public static final ConcurrentHashMap<String, SoftReference<Future<?>>> ERROR_REPORT_FUTURES = new ConcurrentHashMap<>();

    private static final int DEFAULT_MAX_ROW = 1000;

    private final List<String> errorMessageList = new ArrayList<>();

    private final List<R> validatedRecord = new ArrayList<>();

    private final ExcelImportResult importResult = new ExcelImportResult();

    private final ExcelImportErrorBO importError = new ExcelImportErrorBO();

    private int maxRow = DEFAULT_MAX_ROW;

    private Function<List<R>, ExcelImportErrorBO> recordFunction;

    private Consumer<ExcelImportResult> resultConsumer;

    private ExcelRowConverter<T, R> converter;

    private int totalCount = 0;

    private boolean needGenerateReport = false;

    private ExecutorService executorService;




    private String reportFilePrefix = "excel-import-report";


    private String reportFileSuffix = ".xlsx";


    private Integer column;


    private MultipartFile multipartFile;



    public ExcelImportHelper<T, R> generateReportFile(boolean flag) {
        this.needGenerateReport = flag;
        return this;
    }



//    public ExcelImportHelper<T, R> generateReportFile(boolean flag, ExecutorService executorService) {
//        this.needGenerateReport = flag;
//        this.executorService = executorService;
//        return this;
//    }


    /**
     * 设置导入行数限制
     *
     * @param maxRow 最大行数
     * @return excelImportHelper
     */
    public ExcelImportHelper<T, R> importRowLimit(int maxRow) {
        this.maxRow = maxRow;
        return this;
    }

    /**
     * 导入数据处理器
     *
     * @param function 导入数据函数
     * @return excelImportHelper
     */
    public ExcelImportHelper<T, R> recordHandler(Function<List<R>, ExcelImportErrorBO> function) {
        this.recordFunction = function;
        return this;
    }


    /**
     * 导入结果处理器
     *
     * @param consumer 导入结果消费者
     * @return excelImportHelper
     */
    public ExcelImportHelper<T, R> resultHandler(Consumer<ExcelImportResult> consumer) {
        this.resultConsumer = consumer;
        return this;
    }


    /**
     * 对象转换，将excel数据对象转换为entity对象
     *
     * @param converter 转换函数
     * @return entity
     */
    public ExcelImportHelper<T, R> converter(ExcelRowConverter<T, R> converter) {
        this.converter = converter;
        return this;
    }


    private void doRead(InputStream inputStream, Class<T> clazz) {
        EasyExcel.read(inputStream, clazz, this)
                .doReadAll();
    }


    public void doRead(MultipartFile file, Class<T> clazz) {
        try {
            multipartFile = file;
            InputStream inputStream = new BufferedInputStream(file.getInputStream());
            doRead(inputStream, clazz);
        } catch (IOException e) {
            throw new BizException("读取Excel文件失败，" + e.getMessage());
        }

    }


    @Override
    public void invoke(T data, AnalysisContext context) {
        totalCount++;
        verifyMaxRow(totalCount);
        Integer currentIndex = context.readRowHolder().getRowIndex();
        if (validateRowData(data, currentIndex)) {
            validatedRecord.add(converter.convert(currentIndex, data));
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        column = context.readSheetHolder().getTempCellData().getColumnIndex();
        if (recordFunction == null) {
            log.error("导入失败，recordHandler未设置");
            return;
        }
        importResult.setTotalCount(totalCount);
        ExcelImportErrorBO errorBO = recordFunction.apply(validatedRecord);
        if (errorBO.hasError() || importError.hasError()) {
            log.warn("导入数据有误");
            importResult.setErrorCount(errorBO.getErrors().size());
            importResult.setSuccess(false);
            importError.merge(errorBO);
            if (needGenerateReport) {
                File tempFile = generateErrorReportTempFile();
                writeErrorToReport(tempFile);
                importResult.setErrorReport(tempFile.getName());
            }
        }else{
            importResult.setSuccess(true);
        }
        resultConsumer.accept(importResult);
    }


    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        String message = exception.getMessage();
        String errorMsg = "导入异常，" + rowIndex + "行，" + message;
        log.error(errorMsg);
        errorMessageList.add(errorMsg);
        importError.addError(rowIndex, message);
    }


    private File generateErrorReportTempFile(){
        try {
            return File.createTempFile(reportFilePrefix, reportFileSuffix);
        } catch (IOException e) {
            throw  new RuntimeException("生成导入报告异常，" + e.getMessage());
        }
    }


    private void writeErrorToReport(File tempFile) {
//        Future<?> future = executorService.submit(() -> {
            try (InputStream inputStream = multipartFile.getInputStream();
                 Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet sheet = workbook.getSheetAt(0);
                int col = column + 1;
                Row row0 = sheet.getRow(0);
                row0.createCell(col).setCellValue("错误信息");
                importError.getErrors()
                        .forEach((i, msg) -> {
                            Row row = sheet.getRow(i) == null ? sheet.createRow(i) : sheet.getRow(i);
                            Cell cell = row.getCell(col) == null ? row.createCell(col) : row.getCell(col);
                            cell.setCellValue(msg);
                        });
                workbook.write(Files.newOutputStream(tempFile.toPath()));
            } catch (Exception e) {
                throw new RuntimeException("写入导入报告数据异常，" + e.getMessage());
            }
//        });
//        SoftReference<Future<?>> futureSoftReference = new SoftReference<>(future);
//        ERROR_REPORT_FUTURES.put(tempFile.getName(), futureSoftReference);
    }


    /**
     * 校验数据
     */
    private boolean validateRowData(T data, int rowIndex) {
        String validateMessage = ExcelInValid.valid(data);
        if (StringUtils.hasText(validateMessage)) {
            importError.addError(rowIndex, validateMessage);
            return false;
        }
        return true;
    }


    /**
     * 确认导入行数是否超限
     *
     * @param currentRowIndex 当前行数
     */
    private void verifyMaxRow(int currentRowIndex) {
        if (currentRowIndex > maxRow) {
            throw new RuntimeException("导入数据量超过限制 " + maxRow);
        }
    }
}
