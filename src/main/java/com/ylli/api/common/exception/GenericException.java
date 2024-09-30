package com.ylli.api.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

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

    public static GenericThrowable isTrueOrElseThrow(boolean expression) {
        return isTrueOrElseThrow(() -> expression);
    }

    public static GenericThrowable isTrueOrElseThrow(boolean expression, HttpStatus httpStatus) {
        return isTrueOrElseThrow(() -> expression, httpStatus);
    }

    public static GenericThrowable isTrueOrElseThrow(Supplier<Boolean> supplier) {
        return isTrueOrElseThrow(supplier, HttpStatus.BAD_REQUEST);
    }

    public static GenericThrowable isTrueOrElseThrow(Supplier<Boolean> supplier, HttpStatus httpStatus) {
        return (message) -> {
            if (!supplier.get()) {
                throw new GenericException(httpStatus, message);
            }
        };
    }

    @FunctionalInterface
    public interface GenericThrowable {
        void message(String message);
    }
}
