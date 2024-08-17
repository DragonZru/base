package com.ylli.api.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Value("${debugMsg.enable:false}")
    boolean debug;

    @org.springframework.web.bind.annotation.ExceptionHandler(GenericException.class)
    public ResponseEntity<?> exceptionHandler(GenericException ex) {
        String printStackTrace = printStackTrace(ex);
        logger.error(printStackTrace);
        return ResponseEntity.status(ex.getCode()).body(new ResponseBody(ex.getCode(), ex.getMessage(), debug ? printStackTrace : null));
    }


    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<?> exceptionHandler(ErrorResponseException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        return ResponseEntity
                .status(statusCode)
                .body(new ResponseBody(statusCode.value(), ex.getMessage(), null));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<?> exceptionHandler(Exception ex) {
        HttpStatusCode statusCode = getStatusCode(ex);
        return ResponseEntity
                .status(statusCode)
                .body(new ResponseBody(statusCode.value(), ex.getMessage(), null));
    }

    /**
     * 通用异常 return 500.有特殊需要自己添加
     * 业务异常统一使用GenericException，这里主要针对接入的各种插件
     */
    public HttpStatusCode getStatusCode(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof NullPointerException) {
            return HttpStatus.NOT_FOUND;
        }
        logger.error("unexpected exception: ", ex);
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String printStackTrace(Exception ex) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            ex.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class ResponseBody {
        public int code;
        public String message;
        public String debugMsg;

        public ResponseBody(int code, String message, String debugMsg) {
            this.code = code;
            this.message = message;
            this.debugMsg = debugMsg;
        }

        public ResponseBody(HttpStatus httpStatus, String message, String debugMsg) {
            this.code = httpStatus.value();
            this.message = message;
            this.debugMsg = debugMsg;
        }

        /*
         * debugMsg excluded in json response when debugMsg is null
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String getDebugMsg() {
            return debugMsg;
        }
    }
}
