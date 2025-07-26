package com.enn.iot.dtu.integration.bcs.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResListDeviceDTO {
    private int code;
    private Data data;
    private String msg;
    private boolean success;

    public static class Data {

        /**
         * 页码
         */
        private int current;
        private List<DeviceInfoDTO> list;
        /**
         * 每页大小
         */
        private int size;
        private int total;

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getCurrent() {
            return current;
        }

        public void setList(List<DeviceInfoDTO> list) {
            this.list = list;
        }

        public List<DeviceInfoDTO> getList() {
            return list;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotal() {
            return total;
        }

    }

    public static class DeviceInfoDTO {

        /**
         * 品牌编码
         */
        private String brandCode;
        /**
         * 品牌名称
         */
        private String brandName;
        /**
         * 连接状态(1建立连接、2断开连接、3禁用)
         */
        private int connectState;
        /**
         * 物模型编码
         */
        private String entityTypeCode;
        /**
         * 物模型名称
         */
        private String entityTypeName;
        /**
         * 物模型来源
         */
        private String entityTypeSource;
        /**
         * 设备ID
         */
        private String id;
        /**
         * 设备名称
         */
        private String name;
        /**
         * 产品ID
         */
        private String productId;
        /**
         * 产品名称
         */
        private String productName;
        /**
         * ModbusTCP从站地址
         */
        private String slaveAddress;
        /**
         * 设备编码
         */
        private String sn;
        /**
         * 在线状态(-1:离线,1:在线)
         */
        private int state;
        /**
         * 设备三方编码
         */
        private String thirdCode;

        private String period;

        private String tenantId;

        private String deptId;

        private Integer testFlag;


        public void setBrandCode(String brandCode) {
            this.brandCode = brandCode;
        }

        public String getBrandCode() {
            return brandCode;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setConnectState(int connectState) {
            this.connectState = connectState;
        }

        public int getConnectState() {
            return connectState;
        }

        public void setEntityTypeCode(String entityTypeCode) {
            this.entityTypeCode = entityTypeCode;
        }

        public String getEntityTypeCode() {
            return entityTypeCode;
        }

        public void setEntityTypeName(String entityTypeName) {
            this.entityTypeName = entityTypeName;
        }

        public String getEntityTypeName() {
            return entityTypeName;
        }

        public void setEntityTypeSource(String entityTypeSource) {
            this.entityTypeSource = entityTypeSource;
        }

        public String getEntityTypeSource() {
            return entityTypeSource;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductName() {
            return productName;
        }

        public void setSlaveAddress(String slaveAddress) {
            this.slaveAddress = slaveAddress;
        }

        public String getSlaveAddress() {
            return slaveAddress;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getSn() {
            return sn;
        }

        public void setState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public void setThirdCode(String thirdCode) {
            this.thirdCode = thirdCode;
        }

        public String getThirdCode() {
            return thirdCode;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public Integer getTestFlag() {
            return testFlag;
        }

        public void setTestFlag(Integer testFlag) {
            this.testFlag = testFlag;
        }
    }


}
