package com.ennew.iot.gateway.web.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.ennew.iot.gateway.web.excel.valid.ExcelCellConstraint;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TemplateCellWriteHandler implements CellWriteHandler {

    private final int firstRow;

    private final int lastRow;

    private final Map<Integer, String[]> cellConstraint = new HashMap<>();

    public TemplateCellWriteHandler(Class<?> excelModel, int firstRow, int lastRow) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        Field[] declaredFields = excelModel.getDeclaredFields();
        for(int i = 0; i < declaredFields.length; i++){
            Field field = declaredFields[i];
            ExcelCellConstraint excelCellConstraint = field.getAnnotation(ExcelCellConstraint.class);
            if(excelCellConstraint == null){
                continue;
            }
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            int col = excelProperty.index() == -1 ? i : excelProperty.index();
            cellConstraint.put(col, excelCellConstraint.constant());
        }
    }


    @Override
    public void afterCellDispose(CellWriteHandlerContext context) {
        Sheet sheet = context.getWriteSheetHolder().getSheet();
        cellConstraint.forEach((index, constant) -> {
            CellRangeAddressList rangeAddressList = new CellRangeAddressList(firstRow, lastRow, index, index);
            DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
            DataValidationConstraint explicitListConstraint = dataValidationHelper.createExplicitListConstraint(constant);
            DataValidation validation = dataValidationHelper.createValidation(explicitListConstraint, rangeAddressList);
            sheet.addValidationData(validation);
        });
    }
}
