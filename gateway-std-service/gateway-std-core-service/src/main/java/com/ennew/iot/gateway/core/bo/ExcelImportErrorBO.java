package com.ennew.iot.gateway.core.bo;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class ExcelImportErrorBO {

    private LinkedHashMap<Integer, String> errors = null;



    public void addError(Integer row, String msg){
        if(errors == null){
            errors = new LinkedHashMap<>();
        }
        errors.merge(row, msg, (a, b) -> a + "\n" + b);
    }

    public boolean hasError(){
        if(errors == null){
            return false;
        }
        return !errors.isEmpty();
    }


    public void merge(ExcelImportErrorBO errorBO){
        errorBO.getErrors()
                .forEach(this::addError);
    }

}
