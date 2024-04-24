package com.ylli.api.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class GenericExceptionHandler {

    @Value("${debugMsg.enable:false}")
    boolean debug;

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> exceptionHandler(GenericException ex) {
        log.error("GenericException exceptionHandler", ex);
        if (debug) {
            return ResponseEntity.status(ex.getCode()).body(new ResponseBody(ex.getCode(), ex.getMessage(), getSysError(ex)));
        }
        return ResponseEntity.status(ex.getCode()).body(new ResponseBody(ex.getCode(), ex.getMessage()));
    }


    @ExceptionHandler
    public ResponseEntity<?> exceptionHandler(Exception ex) {
        log.error("Exception exceptionHandler", ex);
        //默认500.
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), getSysError(ex)));
    }

    private String getSysError(Exception ex) {
        try (StringWriter sw = new StringWriter();
             PrintWriter pw = new PrintWriter(sw)) {
            ex.printStackTrace(pw);
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static class ResponseBody {
        public int code;
        public String message;
        public String debugMsg;

        public ResponseBody(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public ResponseBody(int code, String message, String debugMsg) {
            this.code = code;
            this.message = message;
            this.debugMsg = debugMsg;
        }
    }
}
