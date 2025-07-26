package com.ennew.iot.gateway.web.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

public class ModbusPointOutputExcel extends ModbusPointExcel{

    @ExcelProperty("错误")
    @ColumnWidth(24)
    private String error;
}
