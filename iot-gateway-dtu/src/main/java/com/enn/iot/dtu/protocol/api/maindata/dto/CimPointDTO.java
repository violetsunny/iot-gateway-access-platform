package com.enn.iot.dtu.protocol.api.maindata.dto;

import lombok.Data;

@Data
public class CimPointDTO {
    /**
     * 自增主键
     */
    private String id;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 更新者
     */
    private String updator;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;
    /**
     * 逻辑删除
     */
    private Boolean delFlag;
    /**
     * 产品编码
     */
    private String productCode;
    /**
     * 关联的端点标识
     */
    private String terminalId;
    /**
     * 关联的设备标识
     */
    private String deviceId;
    /**
     * 测点名称
     */
    private String name;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 点类型
     */
    private String pointType;
    /**
     * 状态值示意
     */
    private String statusExplain;
    /**
     * 单位
     */
    private String unit;
    /**
     * IO类型，PLC专用
     */
    private String ioType;
    /**
     * 仪表类型，PLC专用
     */
    private String meterType;
    /**
     * 数据对象-id（如果是设备的话，存设备的业务id）
     */
    private String dataObjectId;
    /**
     * 数据对象-类型
     */
    private String dateObjectType;
    /**
     * 归属站id
     */
    private String stationId;
    /**
     * 站点别名
     */
    private String aliasCode;
    /**
     * 是否是计量点：是/否
     */
    private String ifMeasurePoint;
    /**
     * value = "父级计量点"
     */
    private String pid;
    /**
     * value = "测点描述"
     */
    private String description;
    /**
     * 控制属性
     */
    private String control;
    /**
     * 控制属性名称
     */
    private String controlName;
    /**
     * 数值乘数
     */
    private String valueMultiplier;
    /**
     * 数值乘数
     */
    /**
     * 数值类型
     */
    private String valueType;
    /**
     * 数值范围
     */
    private String valueRange;
    /**
     * 数据编码
     */
    private String dataCode;
    /**
     * 采集周期
     */
    private Integer collectPeriod;
    /**
     * 采集周期(分、秒)
     */
    private String collectPeriodStr;
    /**
     * 采集模式
     */
    private String collectType;
    /**
     * 采集模式名称
     */
    private String collectTypeName;
    /**
     * 是否交付
     */
    private Boolean isDeliver;
    /**
     * 存储周期
     */
    private Integer dataStoragePeriod;
    /**
     * 上报周期
     */
    private Integer reportPeriod;
    /**
     * 上报模式
     */
    private String reportType;
    /**
     * 周期单位
     */
    private String reportPeriodUnit;
    private Boolean available;
    private String brand;
    private String changeDZlimit;
    private String changeRateLimit;
    private String collectPeriodKey;
    private String collectPeriodName;
    private String constant;
    private String dataTypeName;
    private String deliverStatusName;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String dimension;
    private String emgHigh;
    private String emgLow;
    private String factor;
    private String jumpAlarmLimit;
    private String livePointCode;
    private String oprtHigh;
    private String oprtLow;
    private String productsSeries;
    private String readWriteRole;
    private String readWriteRoleName;
    private String reportTypeName;
    private String specificationModel;
    private String systemCode;
    /**
     * 三方标识
     */
    private String trdPtyCode;
    private String unitName;
    private String valueHL;
    private String valueLL;
    private String valueMultiplierBigdataKey;
    private String valueTypeName;
    private String warHigh;
    private String warLow;
    private String zeroClamp;
    /**
     * 数据地址，从0开始
     */
    private String modBus;
    /**
     * 数据类型
     */
    private String modBusDataType;
    /**
     * 功能吗
     */
    private String functionCode;
    /**
     * 字节序
     */
    private String byteOrder;
    /**
     * 测点标识
     */
    private String measureCat;
    private String ppiDataAddress;
    private String ppiMemoryArea;
    private String ppiDataType;
}
