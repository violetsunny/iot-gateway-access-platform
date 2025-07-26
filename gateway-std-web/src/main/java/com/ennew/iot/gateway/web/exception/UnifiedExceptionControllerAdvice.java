package com.ennew.iot.gateway.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;
import top.kdla.framework.dto.Response;
import top.kdla.framework.dto.exception.ErrorCode;
import top.kdla.framework.exception.BizException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UnifiedExceptionControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public Response validExceptionHandler(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.info("The request [{}] MethodArgumentNotValidException: {}", request.getRequestURI(), ExceptionUtils.getStackTrace(e));
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> errors = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return Response.buildFailure(ErrorCode.PARAMETER_ERROR.getCode(), errors.toString());
    }

    @ExceptionHandler({BizException.class})
    @ResponseBody
    public Response handleBizException(HttpServletRequest req, Throwable exception) {
        log.info("handleBizException, req: {},BizException:{}", req.getRequestURI(), ExceptionUtils.getStackTrace(exception));
        BizException bizException = (BizException)exception;
        String errorCode = bizException.getCode();
        errorCode = ErrorCode.BIZ_ERROR.getCode().equalsIgnoreCase(errorCode) ? ErrorCode.BAD_REQUEST.getCode() : errorCode;
        String errorMessage = bizException.getMessage();
        return Response.buildFailure(errorCode, errorMessage);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) throws Exception {
        String requestBody = "";
        if (request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
        }
        log.error("req: {},body: {},HttpMessageNotReadableException:{}", request.getRequestURI(), requestBody, ExceptionUtils.getStackTrace(ex));
        return Response.buildFailure(ErrorCode.BAD_REQUEST.getCode(), ex.getMessage());
    }
}
