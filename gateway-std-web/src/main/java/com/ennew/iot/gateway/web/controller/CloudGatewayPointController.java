package com.ennew.iot.gateway.web.controller;


import cn.hutool.core.bean.BeanUtil;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayPointMappingService;
import com.ennew.iot.gateway.biz.cloudgateway.CloudGatewayPointService;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusPointBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayModbusPointImportBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointBO;
import com.ennew.iot.gateway.core.bo.CloudGatewayPointPageQueryBO;
import com.ennew.iot.gateway.dal.entity.CloudGatewayPointEntity;
import com.ennew.iot.gateway.dal.enums.ModbusByteOrderEnum;
import com.ennew.iot.gateway.dal.enums.ModbusDataTypeEnum;
import com.ennew.iot.gateway.web.converter.CloudGatewayPointVoConverter;
import com.ennew.iot.gateway.web.excel.ExcelExportUtil;
import com.ennew.iot.gateway.web.excel.ExcelImportHelper;
import com.ennew.iot.gateway.web.excel.ExcelImportResult;
import com.ennew.iot.gateway.web.excel.ModbusPointExcel;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusPointAddVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusPointPageQueryVo;
import com.ennew.iot.gateway.web.vo.CloudGatewayModbusPointVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import top.kdla.framework.dto.MultiResponse;
import top.kdla.framework.dto.PageResponse;
import top.kdla.framework.dto.SingleResponse;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Tag(name = "云网关点位管理")
@Slf4j
@Validated
@RestController
@RequestMapping("/cloud-gateway")
public class CloudGatewayPointController {


    @Resource
    private CloudGatewayPointService cloudGatewayPointService;


    @Resource
    private CloudGatewayPointMappingService cloudGatewayPointMappingService;


    @Resource
    private CloudGatewayPointVoConverter cloudGatewayPointVoConverter;


    @Resource
    private ExecutorService executorService;



    @Operation(summary = "原始点位数量查询")
    @GetMapping("/{gatewayCode}/point/count")
    public SingleResponse<Long> pointCount(@PathVariable String gatewayCode){
        return SingleResponse.buildSuccess(cloudGatewayPointService.queryPointCount(gatewayCode));
    }



    @Operation(summary = "点位删除")
    @DeleteMapping("/point/{pointId}")
    public SingleResponse<?> deletePoints(@PathVariable String pointId,
                                          @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        return SingleResponse.buildSuccess(cloudGatewayPointService.removeById(pointId));
    }



    @Operation(summary = "Modbus原始点表查询-分页")
    @GetMapping("/{gatewayCode}/modbus/point/page")
    public PageResponse<CloudGatewayModbusPointVo> getModbusPointPage(@PathVariable String gatewayCode,
                                                                     CloudGatewayModbusPointPageQueryVo pageQueryVo){
        CloudGatewayPointPageQueryBO pageQueryBO = cloudGatewayPointVoConverter.toCloudGatewayPointPageQueryBO(pageQueryVo);
        PageResponse<CloudGatewayPointBO> response = cloudGatewayPointService.queryPage(gatewayCode, pageQueryBO);
        List<CloudGatewayModbusPointVo> pointVoCollection = cloudGatewayPointVoConverter.toCloudGatewayModbusPointVoCollection(response.getData());
        return PageResponse.of(pointVoCollection, response.getTotalCount(), response.getPageSize(), response.getPageNum());
    }


    @Operation(summary = "Modbus原始点表模板下载")
    @GetMapping("/modbus/template")
    public void getModbusImportTemplate(HttpServletResponse response) throws Exception {
        ExcelExportUtil.excelExport(response, "Modbus点表模板", "原始点表", ModbusPointExcel.class, new ArrayList<>());
    }


    @Operation(summary = "Modbus原始点位导入", responses = {
            @ApiResponse(content = {
                    @Content(schema =
                        @Schema(implementation = ExcelImportResult.class)
                    )
            })
    })
    @PostMapping("/{gatewayCode}/modbus/point/import")
    public ResponseBodyEmitter importModbusPoints(@PathVariable String gatewayCode,
                                   MultipartFile file,
                                   @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        if(file == null){
            try {
                emitter.send(SingleResponse.buildFailure("10001", "请选择导入文件"));
                emitter.complete();
            } catch (IOException e) {
                log.error("导入Excel异常", e);
                emitter.completeWithError(e);
            }
            return emitter;
        }
        new ExcelImportHelper<ModbusPointExcel, CloudGatewayModbusPointImportBO>()
                .generateReportFile(true)
                .converter((row, m) -> m.newCloudGatewayModbusPointImportBO(gatewayCode, bladeAuth, row))
                .recordHandler(entities -> cloudGatewayPointService.importModbusPoints(gatewayCode, entities))
                .resultHandler(result -> {
                    try {
                        emitter.send(result);
                        emitter.complete();
                    } catch (IOException e) {
                        log.error("导入Excel异常", e);
                        emitter.completeWithError(e);
                    }
                })
                .doRead(file, ModbusPointExcel.class);
        return emitter;
    }


    @Operation(summary = "Modbus原始点位导入错误报告下载")
    @GetMapping("/import-error-report/{fileName}")
    public void downloadImportErrorReport(@PathVariable String fileName, HttpServletResponse response) throws Exception{
//        SoftReference<Future<?>> futureSoftReference = ExcelImportHelper.ERROR_REPORT_FUTURES.remove(fileName);
//        Future<?> future = futureSoftReference.get();
//        if(future != null && !future.isDone()){
//            future.get(5, TimeUnit.SECONDS);
//        }
        ExcelExportUtil.downloadImportErrorReport(response,fileName);
    }




    @Operation(summary = "Modbus点位保存")
    @PostMapping("/{gatewayCode}/modbus/point/save")
    public SingleResponse<?> saveModbusPoint(@PathVariable String gatewayCode,
                                             @RequestBody CloudGatewayModbusPointAddVo pointAddVo,
                                             @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        CloudGatewayPointEntity entity = pointAddVo.createEntity(gatewayCode, bladeAuth);
        if(cloudGatewayPointService.exists(entity)){
            return SingleResponse.buildFailure("10001", "测点已存在，请换一个试试");
        }
        return SingleResponse.buildSuccess(cloudGatewayPointService.savePoint(entity));
    }


    @Operation(summary = "Modbus点位编辑")
    @PutMapping("/{gatewayCode}/modbus/point/{id}/update")
    public SingleResponse<?> updateModbusPoint(@PathVariable String gatewayCode,
                                             @PathVariable Long id,
                                             @RequestBody CloudGatewayModbusPointAddVo pointAddVo,
                                             @RequestHeader(value = "blade-auth", required = false) String bladeAuth){
        return SingleResponse.buildSuccess(cloudGatewayPointService.updateById(pointAddVo.createUpdateEntity(id, gatewayCode, bladeAuth)));
    }


    @Operation(summary = "Modbus点表查询-网关获取")
    @GetMapping("/{gatewayCode}/modbus/points")
    public MultiResponse<CloudGatewayModbusPointVo> getModbusPoints(@PathVariable String gatewayCode){
        List<CloudGatewayModbusPointBO> points = cloudGatewayPointService.getModbusPoints(gatewayCode);
        return MultiResponse.buildSuccess(BeanUtil.copyToList(points, CloudGatewayModbusPointVo.class));
    }



    @GetMapping("/enum/{type}")
    @Operation(summary = "展示枚举", description = "type=[ALL、ModbusPointDataType(Modbus点位数据类型)、ModbusPointByteOrder(Modbus点位字节序)]")
    public SingleResponse<Map<String, Object>> getEnum(@PathVariable String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("ModbusPointDataType", Arrays.stream(ModbusDataTypeEnum.values()).map(ModbusDataTypeEnum::getValue).collect(Collectors.toList()));
        map.put("ModbusPointByteOrder", Arrays.stream(ModbusByteOrderEnum.values()).map(ModbusByteOrderEnum::getValue).collect(Collectors.toList()));
        if ("ALL".equalsIgnoreCase(type)) {
            return SingleResponse.buildSuccess(map);
        }
        return SingleResponse.buildSuccess(Collections.singletonMap(type, map.get(type)));
    }
}
