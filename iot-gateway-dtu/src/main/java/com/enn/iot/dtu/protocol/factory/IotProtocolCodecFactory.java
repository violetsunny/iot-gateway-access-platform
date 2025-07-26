package com.enn.iot.dtu.protocol.factory;

import com.enn.iot.dtu.protocol.api.codec.IotProtocolCodec;
import com.enn.iot.dtu.protocol.api.enums.ProtocolTypeEnum;
import com.enn.iot.dtu.protocol.modbus.ModbusRtuCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IotProtocolCodecFactory {

    public static final Map<ProtocolTypeEnum, IotProtocolCodec> GLOBAL_INSTANCES = new HashMap<>();

    /**
     * 获取单例
     *
     * @param protocolType
     * @return
     */
    public static IotProtocolCodec getInstance(ProtocolTypeEnum protocolType) {
        if (GLOBAL_INSTANCES.containsKey(protocolType)) {
            return GLOBAL_INSTANCES.get(protocolType);
        }
        synchronized (IotProtocolCodecFactory.class) {
            if (GLOBAL_INSTANCES.containsKey(protocolType)) {
                return GLOBAL_INSTANCES.get(protocolType);
            }
            IotProtocolCodec instance = newInstance(protocolType);
            if (instance != null){
                GLOBAL_INSTANCES.put(protocolType, instance);
            }
            return instance;
        }
    }

    static IotProtocolCodec newInstance(ProtocolTypeEnum protocolType) {
        switch (protocolType) {
            case MODBUS_RTU:
                return new ModbusRtuCodec();
//            case DLT_645_97:
//                return new Dlt645V97Codec();
//            case DLT_645_07:
//                return new Dlt645V07Codec();
            default:
                log.warn("不支持该协议类型:{}", protocolType.getValue());
                return null;
        }
    }
}
