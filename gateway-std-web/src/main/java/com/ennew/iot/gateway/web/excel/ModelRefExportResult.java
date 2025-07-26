package com.ennew.iot.gateway.web.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * @Description:
 * @Author: qinkun
 * @Date: 2024/4/09 15:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "导入结果")
public class ModelRefExportResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "结果")
    private String exportResult;

    @Schema(description = "总条数")
    private Integer totalCount;

    @Schema(description = "异常数据条数")
    private Integer errorCount;

    @Schema(description = "异常信息")
    private Set<String> errorMessage;

}
