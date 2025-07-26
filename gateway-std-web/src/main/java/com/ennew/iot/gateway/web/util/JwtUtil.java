package com.ennew.iot.gateway.web.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@Slf4j
public class JwtUtil {

    /**
     * 获取token中的属性，用于获取用户名的不可篡改的信息
     *
     * @param auth blade-auth头内容
     * @param prop 要获取的属性（token中的头信息）
     * @return 属性值
     */
    public static String getInfoFromToken(String auth, String prop) {
        try {
            //获取第二段jwt字符串
            String jwt = auth.split(" ")[1];
            //拆分
            String[] parts = jwt.split("\\.");

            //头部
            String header = new String(Base64.getUrlDecoder().decode(parts[0]));
            //载荷
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            //签名
            String signature = parts[2];
            log.info("header {} signature {}", header, signature);
            JSONObject jsonObject = JSONUtil.parseObj(payload);
            return jsonObject.getStr(prop);
        } catch (Exception e) {
            log.warn("Token解析失败 {}", e.getMessage());
            return "";
        }
    }

}
