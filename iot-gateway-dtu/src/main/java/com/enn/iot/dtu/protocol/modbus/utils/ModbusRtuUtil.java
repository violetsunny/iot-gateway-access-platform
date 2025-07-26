package com.enn.iot.dtu.protocol.modbus.utils;

/**
 * Modbus协议工具类
 *
 * @author Mr.Jia
 * @date 2021/11/6 18:43
 */
public class ModbusRtuUtil {

    /**
     * 把字符串转成16进制 modbus 的报文
     *
     * @param strings 字符串
     * @return byte[]
     * @author Mr.Jia
     * @date 2021/11/24 13:42
     */
    public static byte[] encodeMessage(String... strings) {
        byte[] data = new byte[] {};
        for (int i = 0; i < strings.length; i++) {
            int x = Integer.parseInt(strings[i], 16);
            byte n = (byte)x;
            byte[] buffer = new byte[data.length + 1];
            byte[] aa = {n};
            System.arraycopy(data, 0, buffer, 0, data.length);
            System.arraycopy(aa, 0, buffer, data.length, aa.length);
            data = buffer;
        }
        return encodeMessage(data);
    }

    /**
     * 获取源数据和验证码的组合byte数组
     */
    private static byte[] encodeMessage(byte[] aa) {
        byte[] bb = getCrc16ToByte(aa);
        byte[] cc = new byte[aa.length + bb.length];
        System.arraycopy(aa, 0, cc, 0, aa.length);
        System.arraycopy(bb, 0, cc, aa.length, bb.length);
        return cc;
    }

    /**
     * 获取验证码byte数组，基于Modbus CRC16的校验算法
     */
    public static byte[] getCrc16ToByte(byte[] bytes) {
        return intToBytes(getCrc16ToInt(bytes));
    }

    /**
     * 获取验证码byte数组，基于Modbus CRC16的校验算法
     */
    public static int getCrc16ToInt(byte[] bytes) {
        int len = bytes.length;
        // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
        int crc = 0xFFFF;
        int i, j;
        for (i = 0; i < len; i++) {
            // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
            crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (bytes[i] & 0xFF));
            for (j = 0; j < 8; j++) {
                // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
                if ((crc & 0x0001) > 0) {
                    // 如果移出位为 1, CRC寄存器与多项式A001进行异或
                    crc = crc >> 1;
                    crc = crc ^ 0xA001;
                } else {
                    // 如果移出位为 0,再次右移一位
                    crc = crc >> 1;
                }
            }
        }
        return crc;
    }

    /**
     * 将int转换成byte数组，低位在前，高位在后 改变高低位顺序只需调换数组序号
     */
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[1] = (byte)((value >> 8) & 0xFF);
        src[0] = (byte)(value & 0xFF);
        return src;
    }
}
