package com.ennew.iot.gateway.client.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final Pattern p = Pattern.compile("(\\$\\{)([\\w]+)(\\})");

    public static String replace(String url, Map<String,Object> map) {
        Matcher m = p.matcher(url);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String group = m.group(2);//规则中${值}中的 值 一样 的数据不
            m.appendReplacement(sb, String.valueOf( map.get(group)));
        }
        //把符合的数据追加到sb尾
        m.appendTail(sb);
        return sb.toString();
    }
}
