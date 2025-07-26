package com.enn.iot.dtu.integration.open;

import com.enn.iot.dtu.protocol.api.dto.DtuCmdDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName ResCmdDTO
 * @Description
 * @Author nixiaolin
 * @Date 2022/2/15 10:36
 **/

@Data
@ToString
@NoArgsConstructor
public class ResControlCmdDTO {
    private String code;
    private String message;
    private DtuCmdDTO data;
}
