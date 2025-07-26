package com.ennew.iot.gateway.web.excel;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.ennew.iot.gateway.biz.trd.IDeviceServiceClient;
import com.ennew.iot.gateway.biz.trd.TrdPlatformInfoService;
import com.ennew.iot.gateway.biz.trd.TrdPlatformMeasureRefService;
import com.ennew.iot.gateway.biz.trd.TrdPlatformModelRefService;
import com.ennew.iot.gateway.dal.entity.TrdPlatformInfoEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformMeasureRefEntity;
import com.ennew.iot.gateway.dal.entity.TrdPlatformModelRefEntity;
import com.ennew.iot.gateway.dal.enums.ModelSourceEnum;
import com.ennew.iot.gateway.web.excel.valid.ExcelInValid;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.pulsar.shade.org.eclipse.util.StringUtil;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * UserImportListener
 *
 * @author Chill
 */
@Data
@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ModelRefImportListener extends AnalysisEventListener<ModelRefExcel> {

    private static final int IMPORT_LIMIT = 100;

    //----------------------------------------------
    private Integer totalCount = 0;

    private Integer errorCount = 0;

    private Set<String> errorMessage = new LinkedHashSet<>();

    private List<ModelRefExcel> list = new ArrayList<>();

    private List<TrdPlatformModelRefEntity> modelRefList = new ArrayList<>();

    private List<TrdPlatformMeasureRefEntity> measureRefList = new ArrayList<>();

    private ModelRefExportResult modelRefExportResult = new ModelRefExportResult();

    //----------------------------------------------
    private final TrdPlatformInfoService trdPlatformInfoService;
    private final TrdPlatformModelRefService trdPlatformModelRefService;
    private final TrdPlatformMeasureRefService trdPlatformMeasureRefService;
    private final IDeviceServiceClient deviceServiceClient;
    private final String platformCode;
    private final String tenantId;

    @Override
    public void invoke(ModelRefExcel data, AnalysisContext context) {
        totalCount++;
        if (totalCount > IMPORT_LIMIT) {
            errorCount = totalCount;
            throw new RuntimeException("导入数据量超过限制 " + IMPORT_LIMIT);
        }
        Integer currentRowNum = context.getCurrentRowNum() + 1;
        data.setCurrentRowNum("行号: " + currentRowNum + " ");
        String validated = ExcelInValid.valid(data);
        if (StringUtil.isNotBlank(validated)) {
            errorCount++;
            errorMessage.add(data.getCurrentRowNum() + validated);
        } else {
            list.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        if (totalCount > IMPORT_LIMIT) {
            log.warn("导入数据量超过限制");
            errorCount = totalCount;
            return;
        }
        convertData();
        if (errorCount > 0) {
            log.warn("导入数据有误");
            return;
        }
        // 调用saveBatch方法
        trdPlatformModelRefService.saveBatch(modelRefList);
        trdPlatformMeasureRefService.saveBatch(measureRefList);
        // 存储完成清理
        list.clear();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("导入异常" + exception.getMessage());
        errorMessage.add("导入异常" + exception.getMessage());
    }

    public ModelRefExportResult getResult() {
        modelRefExportResult.setTotalCount(totalCount);
        modelRefExportResult.setErrorCount(errorCount);
        modelRefExportResult.setErrorMessage(errorMessage);
        String message = errorCount > 0 || !errorMessage.isEmpty() ? "导入失败" : "导入成功";
        modelRefExportResult.setExportResult(message);
        return modelRefExportResult;
    }

    private void convertData() {
        TrdPlatformInfoEntity trdPlatformInfo = trdPlatformInfoService.getByPCode(getPlatformCode());
        if(trdPlatformInfo==null){
            errorMessage.add(getPlatformCode()+" 没有平台信息，请确认信息是否正确");
            return;
        }
        String pSource = StringUtils.isNotBlank(trdPlatformInfo.getPSource())?trdPlatformInfo.getPSource():ModelSourceEnum.CUSTOM.getCode();
        List<String> addedModelCodeList = new ArrayList<>();
        //按 PlatformModelCode 分组
        Map<String, List<ModelRefExcel>> groupMap = list.stream().collect(Collectors.groupingBy(ModelRefExcel::getPlatformModelCode));
        for (Map.Entry<String, List<ModelRefExcel>> entry : groupMap.entrySet()) {
            String platformModelCode = entry.getKey();
            List<ModelRefExcel> groupList = entry.getValue();
            List<String> addedMeasureCodeList = new ArrayList<>();
            List<String> addedMeasureNameList = new ArrayList<>();
            Long id = getUUID();
            for (ModelRefExcel modelRefExcel : groupList) {
//                if (ModelSourceEnum.getCodeName(modelRefExcel.getEnnModelSource()) == null) {
//                    errorCount++;
//                    errorMessage.add(modelRefExcel.getCurrentRowNum() + "来源不合法，请换一个试试");
//                    continue;
//                }
//                modelRefExcel.setEnnModelSource(ModelSourceEnum.getCodeName(modelRefExcel.getEnnModelSource()));
                // 校验补充物模型信息
                JSONObject ennModel = getInfoByCode(modelRefExcel.getEnnModelCode(), pSource);
                if (ennModel == null) {
                    errorCount++;
                    errorMessage.add(modelRefExcel.getCurrentRowNum() + "物模型标识不存在，请检查是否维护正确");
                    continue;
                }
                //补充产品信息
                String productName = "";
                if (StringUtil.isBlank(modelRefExcel.getEnnProductId())) {
                    String result = deviceServiceClient.getDefaultProductId(ennModel.getString("id"));
                    if (StringUtil.isBlank(result)) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "获取默认产品编码异常");
                        continue;
                    }
                    JSONObject jsonResult = JSON.parseObject(result);
                    if (jsonResult.getInteger("code") != 200 || Boolean.TRUE.equals(!jsonResult.getBoolean("success"))) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "获取默认产品编码失败");
                        continue;
                    }
                    if (StringUtil.isBlank(jsonResult.getString("data"))) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "物模型未发布，导致无默认产品，请发布物模型后，在尝试导入");
                        continue;
                    }
                    String productId = jsonResult.getString("data");
                    modelRefExcel.setEnnProductId(productId);
                    String result2 = deviceServiceClient.getProduct(productId);
                    if (StringUtil.isBlank(result2)) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "获取产品名称异常");
                        continue;
                    }
                    JSONObject jsonResult2 = JSON.parseObject(result2);
                    if (jsonResult2.getInteger("code") != 200 || Boolean.TRUE.equals(!jsonResult2.getBoolean("success")) || StringUtil.isBlank(jsonResult2.getString("data"))) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "获取产品名称失败");
                        continue;
                    }
                    productName = JSON.parseObject(jsonResult2.getString("data")).getString("name");
                } else {
                    Map<String, String> productMap = getProductInfoByEntityTypeCode(modelRefExcel.getEnnModelCode(), trdPlatformInfo.getPSource());
                    if (CollectionUtils.isEmpty(productMap)) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "获取产品编码失败");
                        continue;
                    }
                    if (!productMap.containsKey(modelRefExcel.getEnnProductId())) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "产品编码维护错误，非当前物模型下产品编码 或 产品编码不存在");
                        continue;
                    }
                    productName = productMap.get(modelRefExcel.getEnnProductId());
                }
                //
                Date current = new Date();
                if (!addedModelCodeList.contains(modelRefExcel.getPlatformModelCode())) {
                    addedModelCodeList.add(modelRefExcel.getPlatformModelCode());
                    TrdPlatformModelRefEntity modelRef = BeanUtil.copyProperties(modelRefExcel, TrdPlatformModelRefEntity.class);
                    modelRef.setId(id);
                    modelRef.setPlatformCode(platformCode);
                    modelRef.setEnnModelId(ennModel.getString("id"));
                    modelRef.setEnnModelName(ennModel.getString("name"));
                    modelRef.setEnnModelSource(pSource);
                    modelRef.setEnnProductName(productName);
                    modelRef.setCreateTime(current);
                    modelRef.setUpdateTime(current);
                    modelRef.setStatus(1);
                    modelRef.setIsDelete(0);
                    for (TrdPlatformModelRefEntity m : modelRefList) {
                        if (m.getPlatformModelCode().equals(modelRef.getPlatformModelCode())) {
                            if (m.getEnnProductId().equals(modelRef.getEnnProductId())) {
                                errorCount++;
                                errorMessage.add(modelRefExcel.getCurrentRowNum() + "当前映射关系已存在，请勿重复映射");
                                continue;
                            }
                            if (!m.getPlatformModelName().equals(modelRef.getPlatformModelName())) {
                                errorCount++;
                                errorMessage.add(modelRefExcel.getCurrentRowNum() + "当前三方模型名称与同一个三方模型编码的名称不同，请保持名称一致!");
                                continue;
                            }
                        }
                        if (m.getPlatformModelName().equals(modelRef.getPlatformModelName())) {
                            if (m.getEnnProductId().equals(modelRef.getEnnProductId())) {
                                errorCount++;
                                errorMessage.add(modelRefExcel.getCurrentRowNum() + "当前映射关系已存在，请勿重复映射");
                                continue;
                            }
                            if (!m.getPlatformModelCode().equals(modelRef.getPlatformModelCode())) {
                                errorCount++;
                                errorMessage.add(modelRefExcel.getCurrentRowNum() + "当前三方模型编码与同一个三方模型名称的编码不同，请保持编码一致!");
                                continue;
                            }
                        }
                    }
                    String checkResult = trdPlatformModelRefService.entityParamCheck(modelRef);
                    if (StringUtil.isNotBlank(checkResult)) {
                        errorCount++;
                        errorMessage.add(platformModelCode + checkResult);
                        continue;
                    }
                    modelRefList.add(modelRef);
                }
                if (addedMeasureCodeList.contains(modelRefExcel.getPlatformMeasureCode())) {
                    errorCount++;
                    errorMessage.add(modelRefExcel.getCurrentRowNum() + "测点Code重复，请换一个试试");
                    continue;
                }
                if (addedMeasureNameList.contains(modelRefExcel.getPlatformMeasureName())) {
                    errorCount++;
                    errorMessage.add(modelRefExcel.getCurrentRowNum() + "测点名称重复，请换一个试试");
                    continue;
                }
                Map<String, JSONObject> measureInfoMap = getMeasureInfoById(ennModel.getString("id"));
                if (CollectionUtils.isEmpty(measureInfoMap) || !measureInfoMap.containsKey(modelRefExcel.getEnnMeasureCode())) {
                    errorCount++;
                    errorMessage.add(modelRefExcel.getCurrentRowNum() + "物模型量测属性标识维护错误，在当前物模型中不存在");
                    continue;
                }
                TrdPlatformMeasureRefEntity measureRef = BeanUtil.copyProperties(modelRefExcel, TrdPlatformMeasureRefEntity.class);
                measureRef.setModelRefId(id);
                measureRef.setEnnMeasureId(measureInfoMap.get(modelRefExcel.getEnnMeasureCode()).getString("id"));
                measureRef.setEnnMeasureName(measureInfoMap.get(modelRefExcel.getEnnMeasureCode()).getString("name"));
                measureRef.setPlatformCode(platformCode);
                measureRef.setCreateTime(current);
                measureRef.setUpdateTime(current);
                measureRef.setStatus(1);
                measureRef.setIsDelete(0);
                for (TrdPlatformMeasureRefEntity m : measureRefList) {
                    if (m.getEnnModelCode().equals(measureRef.getEnnModelCode()) && m.getEnnMeasureCode().equals(measureRef.getEnnMeasureCode())) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "当前物模型量测属性已被映射");
                        continue;
                    }
                    if (m.getPlatformMeasureCode().equals(measureRef.getPlatformMeasureCode()) && !m.getPlatformMeasureName().equals(measureRef.getPlatformMeasureName())) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "当前三方平台测点名称与同一个三方测点编码的名称不同，请保持名称一致!");
                        continue;
                    }
                    if (m.getPlatformMeasureName().equals(measureRef.getPlatformMeasureName()) && (!m.getPlatformMeasureCode().equals(measureRef.getPlatformMeasureCode()))) {
                        errorCount++;
                        errorMessage.add(modelRefExcel.getCurrentRowNum() + "当前三方平台测点编码与同一个三方测点名称的编码不同，请保持编码一致!");

                    }
                }
                String checkResult = trdPlatformMeasureRefService.entityParamCheck(measureRef);
                if (StringUtil.isNotBlank(checkResult)) {
                    errorCount++;
                    errorMessage.add(modelRefExcel.getCurrentRowNum() + checkResult);
                    continue;
                }
                measureRefList.add(measureRef);
                addedMeasureCodeList.add(measureRef.getPlatformMeasureCode());
                addedMeasureNameList.add(measureRef.getPlatformMeasureName());
            }
        }
    }

    JSONObject getInfoByCode(String entityTypeCode, String source) {
        String result = deviceServiceClient.getInfoByCode(entityTypeCode, source);
        if (StringUtil.isBlank(result)) {
            return null;
        }
        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") != 200 || Boolean.TRUE.equals(!jsonResult.getBoolean("success"))) {
            return null;
        }
        return jsonResult.getJSONObject("data");
    }

    Map<String, JSONObject> getMeasureInfoById(String entityTypeId) {
        String result = deviceServiceClient.getMeasureInfoByEntityTypeId(entityTypeId);
        if (StringUtil.isBlank(result)) {
            return null;
        }
        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") != 200 || Boolean.TRUE.equals(!jsonResult.getBoolean("success"))) {
            return null;
        }
        JSONArray parse = jsonResult.getJSONArray("data");
        Map<String, JSONObject> map = new HashMap<>();
        for (Object object : parse) {
            JSONObject obj = (JSONObject) object;
            String key = obj.getString("code");
            map.put(key, obj);
        }
        return map;
    }

    Map<String, String> getProductInfoByEntityTypeCode(String entityTypeCode, String source) {
        Map<String, Object> body = new HashMap<>();
        body.put("current", 1);
        body.put("size", 100);
        body.put("source", source);
        body.put("tenantId", tenantId);
        String result = deviceServiceClient.getProductInfoByEntityTypeCode(entityTypeCode, body);
        if (StringUtil.isBlank(result)) {
            return null;
        }
        JSONObject jsonResult = JSON.parseObject(result);
        if (jsonResult.getInteger("code") != 200 || Boolean.TRUE.equals(!jsonResult.getBoolean("success"))) {
            return null;
        }
        JSONObject parse = jsonResult.getJSONObject("data");
        JSONArray jsonArray = parse.getJSONArray("list");
        Map<String, String> map = new HashMap<>();
        for (Object object : jsonArray) {
            JSONObject obj = (JSONObject) object;
            map.put(obj.getString("id"), obj.getString("name"));
        }
        return map;
    }

    DefaultIdentifierGenerator generator = new DefaultIdentifierGenerator();

    public Long getUUID() throws RuntimeException {
        return generator.nextId(this);
    }

}
