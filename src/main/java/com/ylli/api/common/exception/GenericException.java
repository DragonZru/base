package com.ylli.api.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class GenericException extends RuntimeException {

    public int code;

    public GenericException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GenericException(HttpStatus code, String message) {
        super(message);
        this.code = code.value();
    }

    public GenericException(int code) {
        super(HttpStatus.valueOf(code).getReasonPhrase());
        this.code = code;
    }

    /**
     * If true do nothing,  otherwise throws GenericException.
     */
    public static GenericThrowable isTrue(boolean expression) {
        return (httpStatus, message) -> {
            if (!expression) {
                throw new GenericException(httpStatus, message);
            }
        };
    }

    @FunctionalInterface
    public interface GenericThrowable {
        void orElseThrow(HttpStatus httpStatus, String message);
    }
}
