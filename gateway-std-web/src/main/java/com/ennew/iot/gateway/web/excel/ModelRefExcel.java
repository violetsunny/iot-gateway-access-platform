package com.ennew.iot.gateway.web.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.ennew.iot.gateway.common.constants.RegexConstant;
import com.ennew.iot.gateway.web.excel.valid.ExcelNotNullValid;
import com.ennew.iot.gateway.web.excel.valid.ExcelPatternValid;
import lombok.Data;
import org.apache.poi.hssf.usermodel.HSSFFont;

import java.io.Serializable;

@Data
@HeadRowHeight(20)
@HeadFontStyle(color = HSSFFont.COLOR_RED)
@ContentRowHeight(18)
public class ModelRefExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    @ColumnWidth(15)
    @ExcelProperty("行号")
    private String currentRowNum;

    @ExcelNotNullValid(message = "物模型标识不能为空")
    @ColumnWidth(15)
    @ExcelProperty("物模型标识")
    private String ennModelCode;

//    @ExcelNotNullValid(message = "来源不能为空")
//    @ColumnWidth(15)
//    @ExcelProperty("来源")
//    private String ennModelSource;

    @HeadFontStyle(color = HSSFFont.COLOR_NORMAL)
    @ColumnWidth(15)
    @ExcelProperty("产品ID")
    private String ennProductId;

    @ExcelNotNullValid(message = "三方模型名称不能为空")
    @ExcelPatternValid(regexp = RegexConstant.NAME_PATTER, message = "三方模型名称 " + RegexConstant.NAME_ILLEGAL_MESSAGE)
    @ColumnWidth(18)
    @ExcelProperty("三方模型名称")
    private String platformModelName;

    @ExcelNotNullValid(message = "三方模型编码不能为空")
    @ExcelPatternValid(regexp = RegexConstant.CODE_PATTER, message = "三方模型编码 " + RegexConstant.CODE_ILLEGAL_MESSAGE)
    @ColumnWidth(18)
    @ExcelProperty("三方模型编码")
    private String platformModelCode;

    @HeadFontStyle(color = HSSFFont.COLOR_NORMAL)
    @ExcelPatternValid(regexp = RegexConstant.NAME_PATTER, message = "三方品牌 " + RegexConstant.NAME_ILLEGAL_MESSAGE)
    @ColumnWidth(15)
    @ExcelProperty("三方品牌")
    private String platformBrand;

    @HeadFontStyle(color = HSSFFont.COLOR_NORMAL)
    @ExcelPatternValid(regexp = RegexConstant.CODE_PATTER, message = "三方型号 " + RegexConstant.CODE_ILLEGAL_MESSAGE)
    @ColumnWidth(15)
    @ExcelProperty("三方型号")
    private String platformSpec;

    @ExcelNotNullValid(message = "物模型量测属性标识不能为空")
    @ColumnWidth(26)
    @ExcelProperty("物模型量测属性标识")
    private String ennMeasureCode;

    @ExcelNotNullValid(message = "三方测点名称不能为空")
    @ExcelPatternValid(regexp = RegexConstant.NAME_PATTER, message = "三方测点名称 "+ RegexConstant.NAME_ILLEGAL_MESSAGE)
    @ColumnWidth(18)
    @ExcelProperty("三方测点名称")
    private String platformMeasureName;

    @ExcelNotNullValid(message = "三方测点编码不能为空")
    @ExcelPatternValid(regexp = RegexConstant.CODE_PATTER, message = "三方测点编码 " + RegexConstant.CODE_ILLEGAL_MESSAGE)
    @ColumnWidth(18)
    @ExcelProperty("三方测点编码")
    private String platformMeasureCode;

    @HeadFontStyle(color = HSSFFont.COLOR_NORMAL)
    @ColumnWidth(18)
    @ExcelProperty("三方测点单位")
    private String platformMeasureUnit;

}
