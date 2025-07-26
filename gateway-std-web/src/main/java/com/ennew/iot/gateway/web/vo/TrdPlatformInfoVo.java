package com.ennew.iot.gateway.web.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Schema(description = "三方云平台信息")
public class TrdPlatformInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 平台类别
     */
    @Schema(description = "平台类别")
    private Integer pType;

    /**
     * 平台code
     */
    @Schema(description = "平台code")
    private String pCode;

    /**
     * 平台名字
     */
    @Schema(description = "平台名字")
    private String pName;

    @Schema(description = "来源")
    private String pSource;

    /**
     * 配置参数json(appkey、域名等)
     */
    @Schema(description = "json化配置，根据platformType：" +
            "1 -- {\"baseUrl\":\"\",\"appKey\":\"\",\"appSecret\":\"\"} " +
            "2 -- {\"baseUrl\":\"\"}" +
            "3 -- {\"host\":\"\",\"port\":0,\"salveAddress\":0}")
    private String configJson;

    /**
     * 协议Id
     */
    @Schema(description = "协议Id")
    private String protocolId;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateUser;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private String updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;


}
