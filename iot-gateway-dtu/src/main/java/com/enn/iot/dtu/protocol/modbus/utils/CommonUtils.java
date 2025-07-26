package com.enn.iot.dtu.protocol.modbus.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mr.Jia
 * @date 2021/11/11 17:35
 **/
public class CommonUtils {

    /**
     * 校验列表 为空
     */
    public static boolean listIsEmpty(List list) {
        return list == null || list.size() <= 0;
    }

    public static String nullToString(Object obj) {
        if (obj == null || "".equals(obj.toString()) || "null".equals(obj.toString())) {
            obj = "";
        }
        return String.valueOf(obj).trim();
    }

    public static <T> List<List<T>> subList(List<T> list, int size) {
        if (list == null || list.size() == 0) {
            return null;
        }
        // 获得数据总量
        int count = list.size();
        // 计算出要分成几个批次
        int pageCount = (count / size) + (count % size == 0 ? 0 : 1);
        List<List<T>> temp = new ArrayList<>(pageCount);
        for (int i = 0, from = 0, to = 0; i < pageCount; i++) {
            from = i * size;
            to = from + size;
            // 如果超过总数量，则取到最后一个数的位置
            to = Math.min(to, count);
            // 对list 进行拆分
            List<T> list1 = list.subList(from, to);
            // 将拆分后的list放入大List返回
            temp.add(list1);
            // 也可以改造本方法，直接在此处做操作
        }
        return temp;
    }
}
