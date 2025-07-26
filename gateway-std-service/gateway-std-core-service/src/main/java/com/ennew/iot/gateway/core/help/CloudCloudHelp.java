/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.core.help;

/**
 * @author kanglele
 * @version $Id: CtwingCloudServer, v 0.1 2023/11/15 15:55 kanglele Exp $
 */
public interface CloudCloudHelp {

    boolean signature();

    Object transcoding(String msg);

    byte[] decode(String data);

    String decrypt(byte[] data);
}
