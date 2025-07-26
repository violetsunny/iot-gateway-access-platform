package com.ennew.iot.gateway.core.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.InputStream;

@Data
@AllArgsConstructor
public class FileBo {
    private String contentType;
    private String filename;
    private InputStream inputStream;
}
