package com.enn.iot.dtu.integration.bcs.dto;

import com.enn.iot.dtu.protocol.api.maindata.dto.DtuPointInfoDTO;
import lombok.Data;

import java.util.List;

@Data
public class ResListPointInfoDTO {
    private String code;
    private String message;
    private List<DtuPointInfoDTO> data;
}
