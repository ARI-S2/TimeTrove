package com.timetrove.Project.common.exception;

import com.timetrove.Project.common.enumType.ErrorCode;
import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends CustomException {

    public EntityNotFoundException(HttpStatus status, ErrorCode errorCode) {
        super(status, errorCode);
    }

    public EntityNotFoundException(HttpStatus status, ErrorCode errorCode, String detail) {
        super(status, errorCode, detail);
    }
}

