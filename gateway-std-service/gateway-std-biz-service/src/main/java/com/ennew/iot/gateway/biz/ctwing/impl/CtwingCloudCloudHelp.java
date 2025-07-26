/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.biz.ctwing.impl;

import com.alibaba.fastjson.JSON;
import com.ennew.iot.gateway.biz.ctwing.CtwingMessage;
import com.ennew.iot.gateway.common.utils.HexUtils;
import com.ennew.iot.gateway.core.help.CloudCloudHelp;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author kanglele
 * @version $Id: CtwingCloudCloudHelp, v 0.1 2023/11/21 15:33 kanglele Exp $
 */
@Service
@Slf4j
@ToString(callSuper = true)
public class CtwingCloudCloudHelp implements CloudCloudHelp {


    @Override
    public boolean signature() {
        return true;
    }

    @Override
    public Object transcoding(String msg) {
        return JSON.parseObject(msg, CtwingMessage.class);
    }

    @Override
    public byte[] decode(String data) {
        return Base64.getDecoder().decode(data);// 解码为字节数组
    }

    @Override
    public String decrypt(byte[] bytes) {
        return HexUtils.hex2str(bytes);
    }

}
