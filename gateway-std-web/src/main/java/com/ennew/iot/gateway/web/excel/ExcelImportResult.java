package com.ennew.iot.gateway.web.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "导入结果")
public class ExcelImportResult {

    @Schema(description = "导入结果")
    private boolean success;

    @Schema(description = "总条数")
    private Integer totalCount;

    @Schema(description = "异常数据条数")
    private Integer errorCount;

    @Schema(description = "错误报告")
    private String errorReport;

}
