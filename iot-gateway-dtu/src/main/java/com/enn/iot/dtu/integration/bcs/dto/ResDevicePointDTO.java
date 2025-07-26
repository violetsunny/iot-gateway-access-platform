package com.enn.iot.dtu.integration.bcs.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResDevicePointDTO {

    private int code;
    private List<PointInfoDTO> data;
    private String msg;



    public static class PointInfoDTO {

        /**
         * 精度
         */
        private int accuracy;
        /**
         * 加常数
         */
        private int additiveConstant;
        /**
         * 地址:功能码+寄存器地址，拼接方式: 功能码!寄存器地址
         */
        private String address;
        /**
         * 位偏移
         */
        private int bitOffset;
        /**
         * 字节长度。寄存器数量
         */
        private int byteLength;
        /**
         * 字节顺序
         */
        private String byteOrder;
        /**
         * 采集频率单位code
         */
        private String collectCycleUnitCode;

        private String dataType;
        /**
         * 功能码
         */
        private String functionCode;
        /**
         * 最大
         */
        private int max;
        /**
         * 测点编码
         */
        private String measureCode;
        /**
         * 属性单位
         */
        private String measureUnit;
        /**
         * 最小
         */
        private int min;
        /**
         * 乘系数
         */
        private int multiplyFactor;
        /**
         * 1:描述
         */
        private String one;
        /**
         * 解析方式,0-BIT位，1-16位无符号整型，2-16位有符号整型，3-32位无符号整型，4-32位有符号整型，5-32位IEEE浮点型，6-32位浮点型，7-64位长浮点型，8-64位Long型，99-其他
         */
        private String parsingMode;
        /**
         * 测点名称
         */
        private String measureName;
        /**
         * 读写权限
         */
        private String readWriteRight;
        /**
         * 上报周期单位code
         */
        private String reportCycleUnitCode;
        /**
         * 特定采集频率
         */
        private int specificCollectCycle;
        /**
         * 特定上报周期
         */
        private int specificReportCycle;
        /**
         * 精度
         */
        private List<UpdateProductMeasureEnumReqs> updateProductMeasureEnumReqs;
        /**
         * 描述
         */
        private String zero;

        public void setAccuracy(int accuracy) {
            this.accuracy = accuracy;
        }
        public int getAccuracy() {
            return accuracy;
        }

        public void setAdditiveConstant(int additiveConstant) {
            this.additiveConstant = additiveConstant;
        }
        public int getAdditiveConstant() {
            return additiveConstant;
        }

        public void setAddress(String address) {
            this.address = address;
        }
        public String getAddress() {
            return address;
        }

        public void setBitOffset(int bitOffset) {
            this.bitOffset = bitOffset;
        }
        public int getBitOffset() {
            return bitOffset;
        }

        public void setByteLength(int byteLength) {
            this.byteLength = byteLength;
        }
        public int getByteLength() {
            return byteLength;
        }

        public void setByteOrder(String byteOrder) {
            this.byteOrder = byteOrder;
        }
        public String getByteOrder() {
            return byteOrder;
        }

        public void setCollectCycleUnitCode(String collectCycleUnitCode) {
            this.collectCycleUnitCode = collectCycleUnitCode;
        }
        public String getCollectCycleUnitCode() {
            return collectCycleUnitCode;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
        public String getDataType() {
            return dataType;
        }

        public void setFunctionCode(String functionCode) {
            this.functionCode = functionCode;
        }
        public String getFunctionCode() {
            return functionCode;
        }

        public void setMax(int max) {
            this.max = max;
        }
        public int getMax() {
            return max;
        }

        public void setMeasureCode(String measureCode) {
            this.measureCode = measureCode;
        }
        public String getMeasureCode() {
            return measureCode;
        }

        public void setMeasureUnit(String measureUnit) {
            this.measureUnit = measureUnit;
        }
        public String getMeasureUnit() {
            return measureUnit;
        }

        public void setMin(int min) {
            this.min = min;
        }
        public int getMin() {
            return min;
        }

        public void setMultiplyFactor(int multiplyFactor) {
            this.multiplyFactor = multiplyFactor;
        }
        public int getMultiplyFactor() {
            return multiplyFactor;
        }

        public void setOne(String one) {
            this.one = one;
        }
        public String getOne() {
            return one;
        }

        public void setParsingMode(String parsingMode) {
            this.parsingMode = parsingMode;
        }
        public String getParsingMode() {
            return parsingMode;
        }

        public void setMeasureName(String pointName) {
            this.measureName = pointName;
        }
        public String getMeasureName() {
            return measureName;
        }

        public void setReadWriteRight(String readWriteRight) {
            this.readWriteRight = readWriteRight;
        }
        public String getReadWriteRight() {
            return readWriteRight;
        }

        public void setReportCycleUnitCode(String reportCycleUnitCode) {
            this.reportCycleUnitCode = reportCycleUnitCode;
        }
        public String getReportCycleUnitCode() {
            return reportCycleUnitCode;
        }

        public void setSpecificCollectCycle(int specificCollectCycle) {
            this.specificCollectCycle = specificCollectCycle;
        }
        public int getSpecificCollectCycle() {
            return specificCollectCycle;
        }

        public void setSpecificReportCycle(int specificReportCycle) {
            this.specificReportCycle = specificReportCycle;
        }
        public int getSpecificReportCycle() {
            return specificReportCycle;
        }

        public void setUpdateProductMeasureEnumReqs(List<UpdateProductMeasureEnumReqs> updateProductMeasureEnumReqs) {
            this.updateProductMeasureEnumReqs = updateProductMeasureEnumReqs;
        }
        public List<UpdateProductMeasureEnumReqs> getUpdateProductMeasureEnumReqs() {
            return updateProductMeasureEnumReqs;
        }

        public void setZero(String zero) {
            this.zero = zero;
        }
        public String getZero() {
            return zero;
        }

    }

    public static class UpdateProductMeasureEnumReqs {

        /**
         * 参数描述
         */
        private String description;
        /**
         * 参数值
         */
        private String value;
        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }

        public void setValue(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }

    }
}
