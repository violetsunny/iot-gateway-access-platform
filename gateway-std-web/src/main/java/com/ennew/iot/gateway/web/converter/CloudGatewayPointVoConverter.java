package com.ennew.iot.gateway.web.converter;


import com.ennew.iot.gateway.core.bo.CloudGatewayPointBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointPageQueryBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import com.ennew.iot.gateway.web.excel.ModbusPointExcel;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusPointPageQueryVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusPointVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CloudGatewayPointVoConverter {

//
//    @Mappings({
//            @Mapping(source = "realDeviceName", target = "realDeviceName"),
//            @Mapping(source = "pointName", target = "name"),
//            @Mapping(source = "configJson", target = "")
//    })
//    CloudGatewayPointEntity toCloudGatewayPointEntity(ModbusPointExcel modbusPointExcel, String bladeAuth);
//
//
//
//    default String getConfigJson(ModbusPointExcel modbusPointExcel){
//
//    }




    @Mappings({
            @Mapping(target = "functionCode", expression = "java(pointBO.getConfigJson().getInteger(\"functionCode\"))"),
            @Mapping(target = "registerAddress", expression = "java(pointBO.getConfigJson().getInteger(\"registerAddress\"))"),
            @Mapping(target = "dataType", expression = "java(pointBO.getConfigJson().getString(\"dataType\"))"),
            @Mapping(target = "byteOrder", expression = "java(pointBO.getConfigJson().getString(\"byteOrder\"))"),
            @Mapping(target = "rw", expression = "java(pointBO.getConfigJson().getString(\"rw\"))")
    })
    CloudGatewayModbusPointVo toCloudGatewayModbusPointVo(CloudGatewayPointBO pointBO);


    List<CloudGatewayModbusPointVo> toCloudGatewayModbusPointVoCollection(List<CloudGatewayPointBO> pointBOList);


    @Mappings({
            @Mapping(target = "orderDirection", source = "orderDirection"),
            @Mapping(target = "orderBy", source = "orderBy"),
            @Mapping(target = "pageNum", source = "pageNum"),
            @Mapping(target = "pageSize", source = "pageSize"),
            @Mapping(target = "pointName", source = "pointName"),
            @Mapping(target = "realDeviceName", source = "realDeviceName")
    })
    CloudGatewayPointPageQueryBO toCloudGatewayPointPageQueryBO(CloudGatewayModbusPointPageQueryVo pageQueryVo);


}
