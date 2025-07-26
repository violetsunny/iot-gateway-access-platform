package com.ennew.iot.gateway.web.excel;

public interface ExcelRowConverter<T, R> {
    R convert(int rowIndex, T data);
}
