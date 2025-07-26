package com.ennew.iot.gateway.client.protocol;


import com.ennew.iot.gateway.client.utils.SpringContextUtil;

import java.io.Closeable;

public interface ProtocolSupportProvider extends Closeable {
    ProtocolSupport create(SpringContextUtil context);
}
