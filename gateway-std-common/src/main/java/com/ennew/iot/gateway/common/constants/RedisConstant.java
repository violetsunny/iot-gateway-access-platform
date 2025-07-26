package com.ennew.iot.gateway.common.constants;

/**
 * redis常量
 */
public class RedisConstant {

    private RedisConstant() {
    }

    public static final String DEVICE_KEY = "";

    public static final String DEVICE_SESSION_KEY = "enn-iot-session";

    public static final String USER_COUNT = "userCount";

    public static final String STATE = "state";
    public static final String IS_GATEWAY_DEVICE = "isGatewayDevice";
    public static final String DEVICE_CODE = "deviceCode";
    public static final String TAGS = "tags";
    public static final String DOMAIN = "domain";
    public static final String DEVICE_TYPE = "deviceType";//设备类型：子设备、网关、DTU等
    public static final String TEST_FLAG = "testFlag";
    public static final String TENANT_ID = "tenantId";
    public static final String PRODUCT_ID = "productId";
    public static final String ENTITY_TYPE_CODE = "entityTypeCode";   //填充devType 物模型标识
    public static final String ENTITY_TYPE_NAME = "entityTypeName";   //原名填充 物模型名称
    public static final String THIRD_CODE = "thirdCode";        //填充deviceCode 设备编码
    public static final String PERIOD = "period";           //原名填充 频率
    public static final String DEVICE_NAME = "deviceName";       //原名填充 设备名称
    public static final String SN = "sn";               //原名填充 设备标识
    public static final String DEPT_ID = "deptId";           //原名填充 企业id
    public static final String PROJECT_CODE = "projectCode";      //填充staId 场站id
    public static final String SOURCE = "source";           //原名填充 数据源
    public static final String PARENT_ID = "parentId";         //原名填充 父设备
    public static final String AREA_CODE = "areaCode";          //填充county 地区编码
}
