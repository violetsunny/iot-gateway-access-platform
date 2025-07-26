package com.ennew.iot.gateway.web.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.excel.enums.poi.FillPatternTypeEnum;
import com.alibaba.fastjson2.JSONObject;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusPointImportBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import com.ennew.iot.gateway.web.excel.valid.ExcelCellConstraint;
import com.ennew.iot.gateway.web.excel.valid.ExcelNotNullValid;
import com.ennew.iot.gateway.web.util.JwtUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFont;

import java.io.Serializable;
import java.util.Date;

@Data
@HeadRowHeight(20)
@ContentRowHeight(18)
public class ModbusPointExcel implements Serializable {

    @ExcelNotNullValid(message = "设备名称不能为空")
    @ColumnWidth(24)
    @ExcelProperty("设备名称（必填）")
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    private String realDeviceName;


    @ExcelNotNullValid(message = "测点名称不能为空")
    @ColumnWidth(24)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    @ExcelProperty("测点名称（必填）")
    private String pointName;


    @ExcelNotNullValid(message = "功能码不能为空")
    @ExcelCellConstraint(constant = {"01", "02", "03", "04", "05", "06", "0F", "10"})
    @ColumnWidth(24)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    @ExcelProperty("功能码（必填）")
    private String functionCode;


    @ExcelNotNullValid(message = "寄存器地址不能为空")
    @ColumnWidth(24)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    @ExcelProperty("寄存器地址（必填）")
    private String registerAddress;


    @ExcelNotNullValid(message = "数据类型不能为空")
    @ColumnWidth(24)
    @ExcelCellConstraint(constant ={ "bit0","bit1","bit2","bit3","bit4","bit5","bit6","bit7","bit8","bit9","bit10","bit11","bit12","bit13","bit14","bit15",
            "int8","uint8","int16","uint16","int24","uint24","int32","uint32","int48","uint48","int64","uint64","float32","float64","time48","time64"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    @ExcelProperty("数据类型（必填）")
    private String dataType;

    @ExcelNotNullValid(message = "字节序不能为空")
    @ColumnWidth(24)
    @ExcelCellConstraint(constant = {
            "1234",
            "2143",
            "3412",
            "4321",
            "12345678",
            "21436587",
            "78563412",
            "87654321"
    })
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    @ExcelProperty("字节序（必填）")
    private String byteOrder;


    @ExcelNotNullValid(message = "读写模式不能为空")
    @ColumnWidth(24)
    @ExcelCellConstraint(constant = {"r", "w", "rw"})
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 10)
    @ExcelProperty("读写模式（必填）")
    private String rw;


    @ExcelProperty("备注")
    @ColumnWidth(28)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    private String remark;


    @ExcelProperty("产品ID")
    @ColumnWidth(24)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    private String productId;


    @ExcelProperty("平台设备ID")
    @ColumnWidth(24)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    private String deviceId;


    @ExcelProperty("平台设备属性")
    @ColumnWidth(24)
    @HeadStyle(fillPatternType = FillPatternTypeEnum.SOLID_FOREGROUND, fillForegroundColor = 40)
    private String deviceMetric;



    public CloudGatewayModbusPointImportBO newCloudGatewayModbusPointImportBO(String gatewayCode, String bladeAuth, Integer row){
        CloudGatewayModbusPointImportBO importBO = new CloudGatewayModbusPointImportBO();
        importBO.setByteOrder(this.byteOrder);
        importBO.setPointName(this.pointName);
        importBO.setRw(this.rw);
        importBO.setDeviceId(this.deviceId);
        importBO.setRemark(this.remark);
        importBO.setSort(row);
        importBO.setPointName(this.pointName);
        importBO.setProductId(this.productId);
        importBO.setGatewayCode(gatewayCode);
        importBO.setDataType(this.dataType);
        importBO.setDeviceMetric(this.deviceMetric);
        importBO.setRegisterAddress(this.registerAddress);
        importBO.setFunctionCode(this.functionCode);
        importBO.setRealDeviceName(this.realDeviceName);
        importBO.setRow(row);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            importBO.setUser(account);

        }
        return  importBO;
    }



    public CloudGatewayPointEntity createEntity(String gatewayCode, String bladeAuth, Integer rowIndex){
        CloudGatewayPointEntity entity = new CloudGatewayPointEntity();
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        if (StringUtils.isNotEmpty(bladeAuth)) {
            String account = JwtUtil.getInfoFromToken(bladeAuth, "account");
            entity.setCreateUser(account);
            entity.setUpdateUser(account);
        }
        entity.setRealDeviceName(this.realDeviceName);
        entity.setSort(rowIndex);
        entity.setName(this.pointName);
        entity.setRemark(this.remark);
        entity.setCloudGatewayCode(gatewayCode);
        JSONObject config = new JSONObject();
        config.put("functionCode", this.functionCode);
        config.put("registerAddress", this.registerAddress);
        config.put("dataType", this.dataType);
        config.put("byteOrder", this.byteOrder);
        config.put("rw", this.rw);
        entity.setConfigJson(config.toJSONString());
        return entity;
    }
}
