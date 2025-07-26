package com.ennew.iot.gateway.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.ByteOrder;

/**
 * Created by DongXl on 2015/12/4.
 */
@Slf4j
public class HexUtils {
    public static String getDomain(String param) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < param.length() / 2; i++) {
            Integer integer = Integer.parseInt(param.substring(i * 2, (i + 1) * 2), 16);
            char temp = (char) integer.intValue();
            sb.append(temp);
        }
        return sb.toString();
    }

    public static String lowBitBeforeTOHighBitBefore(String param) {
        String ret = "";
        if (StringUtils.isEmpty(param)) {
            return null;
        }
        if (param.length() % 2 != 0) {
            return null;
        }
        String temp = "";
        for (int i = 0; i < param.length() / 2; i++) {
            temp = param.substring(param.length() - 2 * (i + 1), param.length() - 2 * i);
            ret = ret + temp;
        }
        return ret;
    }

    public static String getPART(String part) {
        String ret = "";
        ret = String.valueOf(Integer.parseInt(part.substring(2, 4), 16)) + Integer.parseInt(part.substring(0, 2), 16);
        return ret;
    }

    public static String getIP(String ip) {
        String ret = "";
        String temp = "";
        for (int i = 0; i < 4; i++) {
            temp = ip.substring(i * 2, i * 2 + 2);
            ret = ret + Integer.parseInt(temp, 16);
            if (i < 3) {
                ret = ret + ".";
            }
        }
        return ret;
    }

    public static String int2hex(int val) {
        return int2hex(val, 4, ByteOrder.LITTLE_ENDIAN);
    }

    public static String int2hex(int val, int len) {
        return int2hex(val, len, ByteOrder.LITTLE_ENDIAN);
    }

    public static String int2hex(int val, int len, ByteOrder bo) {
        ByteBuf buf = Unpooled.buffer(len).order(bo);
        switch (len) {
            case 1:
                buf.writeByte(val);
                break;
            case 2:
                buf.writeShort(val);
                break;
            case 4:
                buf.writeInt(val);
                break;
            case 8:
                buf.writeLong(val);

            default:
                break;
        }
        return ByteBufUtil.hexDump(buf);
    }

    /**
     * @param val 十六进制字符串
     * @method 将十六进制字符串(2字节)转化为int, 大端格式
     */
    public static int hex2int2big(String val) {
        return hex2int(val, 2, ByteOrder.BIG_ENDIAN);
    }

    public static int hex2int(String val) {
        return hex2int(val, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * 十六进制的字符串转为long类型
     *
     * @param val
     * @return
     */
    public static long hex2long(String val) {
        return Long.parseLong(val, 16);
    }

    public static int hex2int(String val, ByteOrder bo) {
        if (val.length() % 2 > 0) {
            val = "0" + val;
        }
        return hex2int(val, val.length() / 2, bo);
    }

    /**
     * 转换为时间字符串
     */
    public static String getTimeValue(String fieldData) {
        return fieldData.substring(0, 4) + "-" + fieldData.substring(4, 6) + "-" + fieldData.substring(6, 8) + " " + fieldData.substring(8, 10) +
                ":" + fieldData.substring(10, 12) + ":" + fieldData.substring(12);
    }

    /**
     * 转换为时间字符串,并补世纪20
     */
    public static String getTimeValue2(String fieldData) {
        return "20" + fieldData.substring(0, 2) + "-" + fieldData.substring(2, 4) + "-" + fieldData.substring(4, 6) + " " + fieldData.substring(6, 8) +
                ":" + fieldData.substring(8, 10) + ":" + fieldData.substring(10);
    }

    public static int hex2int(String val, int len) {
        return hex2int(val, len, ByteOrder.LITTLE_ENDIAN);
    }

    public static int hex2int(String val, int len, ByteOrder bo) {
        ByteBuf buf = null;
        try {
            buf = Unpooled.buffer(len).order(bo);
            buf.writeBytes(Hex.decodeHex(val.toCharArray()));

            switch (len) {
                case 1:
                    return buf.readByte();
                case 2:
                    return buf.readShort();
                case 4:
                    return buf.readInt();
                case 8:
                    Long v = buf.readLong();
                    return v.intValue();
                default:
                    throw new Exception(
                            "Length field value " + len + " (expected: 1, 2, 3, 4, or 8)");
            }
        } catch (Exception e) {
            assert buf != null;
            buf.clear();
            log.error("inputParams:{} and errorMessage:{}", val, e.getMessage(), e);
        }
        return 0;
    }


    public static byte[] str2hex(String ss) {
        byte[] digest = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }
        return digest;
    }

    public static String hex2str(byte[] b) {
        StringBuilder hexString = new StringBuilder();
        for (byte aB : b) {
            String plainText = Integer.toHexString(0xff & aB);
            if (plainText.length() < 2)//$ANALYSIS-IGNORE
            {
                plainText = "0" + plainText;
            }
            hexString.append(plainText);
        }
        return hexString.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static float hexStr2float(String hexStr, ByteOrder byteOrder) {
        return Float.intBitsToFloat(HexUtils.hex2int(hexStr, byteOrder));
    }

    public static float hexStr2floatDivide(String hexStr, ByteOrder byteOrder, int num) {
        return Float.intBitsToFloat(HexUtils.hex2int(hexStr, byteOrder)) / num;
    }

    public static double hex2IntDivide(String hexStr, ByteOrder byteOrder, int num) {
        return (double) HexUtils.hex2int(hexStr, byteOrder) / num;
    }

    public static String float2hexStr(float f, ByteOrder byteOrder) {
        String value = Integer.toHexString(Float.floatToIntBits(f));
        if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            value = value.substring(6) + value.substring(4, 6) + value.substring(2, 4) + value.substring(0, 2);
        }
        return value;
    }

    public static String int2hexStrIfNullFillF(Integer num, int len) {
        if (num == null) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < len; i++) {
                result.append("FF");
            }
            return result.toString();
        }

        return int2hex(num, len);
    }

    public static int checksum(String val) {
        int chkValue = 0;
        try {
            byte[] hex = Hex.decodeHex(val.toCharArray());
            for (Byte t : hex) {
                chkValue += t.intValue();
            }
        } catch (DecoderException e) {
            log.error("inputParams:{} and errorMessage:{}", val, e.getMessage(), e);
            chkValue = 0;
        }
        return chkValue & 0xFF;
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC).toUpperCase();
    }


    /**
     * 16进制(8字节)转double
     */
    public static double hex2double(String hexStr, Integer scale) {
        return Double.longBitsToDouble(Long.parseLong(hexStr, 16));
    }

    /**
     * 16进制(8字节)转double 设置精度
     */
    public static BigDecimal hex2doubleAccuracy(String hexStr, Integer scale, ByteOrder byteOrder) throws Exception {
        ByteBuf buf = null;
        long v = 0L;
        buf = Unpooled.buffer(8).order(byteOrder);
        buf.writeBytes(Hex.decodeHex(hexStr.toCharArray()));
        v = buf.readLong();
        Double d = Double.longBitsToDouble(v);
        BigDecimal bd2 = BigDecimal.valueOf(d);
        BigDecimal setScale = bd2.setScale(scale, BigDecimal.ROUND_HALF_UP);//四舍五入
        return setScale;
    }

    /**
     * 除法
     *
     * @return
     */
    public static float int2float(Integer int1, Integer int2) {
        return int1 / int2;
    }

    /**
     * 16进制字符串转2进制字符串
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return null;
        }

        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    /**
     * @param src 16进制字符串
     * @return 字节数组
     * @throws
     * @Title:hexString2String
     * @Description:16进制字符串转字符串
     */
    public static String hexString2String(String src) {
        String temp = "";
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp
                    + (char) Integer.valueOf(src.substring(i * 2, i * 2 + 2),
                    16).byteValue();
        }
        return temp;
    }


    public static String toBinary(String str) {

        char[] strChar = str.toCharArray();
        String result = "";
        for (int i = 0; i < strChar.length; i++) {
            result += Integer.toBinaryString(strChar[i]) + "";
        }
        return result;
    }

    public static String strTo16(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }


    /*
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        //将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2) {
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        }
        return baos.toString("gbk");
    }

    static String hexString = "0123456789ABCDEF";

    public static String asciiToString(String value) {
        String temp = value;
        StringBuffer sbu = new StringBuffer();
        String str = "";
        boolean f = false;
        for (int j = 0; j < value.length() / 2; j++) {
            if (!temp.startsWith("30") || f) {
                f = true;
                str = str + temp.substring(0, 2);
                if (j != (value.length() / 2) - 1) {
                    str = str + ",";
                }
            }
            temp = temp.substring(2);
        }
        String[] chars = str.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i], 16));
        }
        String temp2 = sbu.toString();
        return sbu.toString();
    }

    public static String parseStringAscii(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] strs = str.toCharArray();
        for (int i = 0; i + 1 < str.length(); i += 2) {
            if (strs[i] == '0' && strs[i + 1] == 48) {
                break;
            }
            String t = "" + strs[i] + strs[i + 1];
            char c = (char) Integer.valueOf(t, 16).intValue();
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static int checki(String s) {
        if ("A".equals(s)) {
            return 10;
        } else if ("B".equals(s)) {
            return 11;
        } else if ("C".equals(s)) {
            return 12;
        } else if ("D".equals(s)) {
            return 13;
        } else if ("E".equals(s)) {
            return 14;
        } else if ("F".equals(s)) {
            return 15;
        } else {
            return Integer.parseInt(s);
        }
    }

    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcd2Str(String hexString) {
        byte[] bytes = str2hex(hexString);
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    public static String StringToAsciiString(String content) {
        String result = "";
        int max = content.length();
        for (int i = 0; i < max; i++) {
            char c = content.charAt(i);
            String b = Integer.toHexString(c);
            result = result + b;
        }
        return result;
    }


}
