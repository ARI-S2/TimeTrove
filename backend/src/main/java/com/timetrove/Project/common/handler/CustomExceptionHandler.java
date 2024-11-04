package com.timetrove.Project.common.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.timetrove.Project.common.exception.CustomException;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDto> handleCustomException(CustomException ex) {
        log.info("핸들러 실행: 커스텀 에러 발생 CustomException: {}", ex.getErrorCode());
        return ErrorDto.toResponseEntity(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.info("핸들러 실행: EntityNotFoundException 발생 - {}", ex.getErrorCode());
        return ErrorDto.toResponseEntity(ex);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorDto> handleExpiredJwtException() {
        log.info("핸들러 실행: 토큰 만료 에러 발생");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto("EXPIRED_TOKEN", "토큰이 만료됐습니다.", ""));
    }

    @ExceptionHandler({JWTVerificationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorDto> handleJWTVerificationException() {
        log.info("핸들러 실행: 토큰 유효성 에러 발생");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto("INVALID_TOKEN", "유효하지 않은 토큰입니다.", ""));
    }
}
