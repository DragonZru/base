package com.ylli.base.configuration.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.ylli.common.exception.AbstractExceptionHandler;
import com.ylli.common.exception.GenericException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author ylli
 */
@RestControllerAdvice
public class GenericExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler({UndeclaredThrowableException.class})
    public ResponseEntity<?> undeclaredThrowableException(UndeclaredThrowableException ex) {
        Throwable cause = ex.getUndeclaredThrowable();
        if (cause instanceof BlockException) {
            return flowExceptionHandler((BlockException) cause);
        }
        return exceptionHandler(ex);
    }

    @ExceptionHandler({BlockException.class, FlowException.class})
    public ResponseEntity<?> flowExceptionHandler(BlockException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(new ResponseBody(HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(), ex.getRule().toString()));
    }
}

