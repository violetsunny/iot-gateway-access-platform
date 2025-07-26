package com.ennew.iot.gateway.common.constants;

/**
 * @Description: 通用正则表达式
 */
public interface RegexConstant {

    /**
     * 名称校验正则
     */
    String NAME_PATTER = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\W_]{0,30}$";

    String NAME_ILLEGAL_MESSAGE = "支持中文字符、大小写字母、数字和特殊字符,0~30个字符";

    /**
     * Code校验正则
     */
    String CODE_PATTER = "^[A-Za-z0-9@#$%^&*()\\[\\]{}<>~|+=:;!,.?/'\\\\_-]{0,100}$";

    String CODE_ILLEGAL_MESSAGE = "支持大小写字母、数字和特殊字符,0~100个字符";

    /**
     * 内容校验正则
     */
    String CONTENT_PATTER = "[\\u4e00-\\u9fa5a-zA-Z0-9\\W_]*";

    String CONTENT_ILLEGAL_MESSAGE = "支持中文字符、大小写字母、数字和特殊字符";

    /**
     * url校验正则
     */
    String URL_PATTER = "((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";

    String URL_ILLEGAL_MESSAGE = "请输入正确格式的URL";


    String IP_AND_HOSTNAME_PATTERN = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)+([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$";

    String IP_AND_HOSTNAME_PATTERN_MESSAGE = "请输入正确的IP或域名";
}
