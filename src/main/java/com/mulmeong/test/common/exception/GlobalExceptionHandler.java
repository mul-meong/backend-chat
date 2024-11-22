package com.mulmeong.test.common.exception;

import com.mulmeong.test.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.mulmeong.test.common.response.BaseResponseStatus.INTERNAL_SERVER_ERROR;
import static com.mulmeong.test.common.response.BaseResponseStatus.INVALID_INPUT_VALUE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Controller 에서 발생한 예외 처리 클래스
     * (Serivce에서 발생한 예외 또한 Controller에서 처리)
     */

    /**
     * BaseException 예외 처리.
     */
    @ExceptionHandler(BaseException.class)
    protected BaseResponse<Void> handleBaseException(BaseException e) {

        // 예외 메시지를 로그에 기록
        log.error("BaseException 발생 : {}", e.getStatus());

        // 예외 스택을 로그에 기록
        for (StackTraceElement s : e.getStackTrace()) {
            System.out.println(s);
        }

        return new BaseResponse<>(e.getStatus(), e);
    }

    /**
     * 의도치않게 발생한 RuntimeException 예외 처리
     * 예외객체를 BaseResponse에 넘겨 응답에서 디버깅할 수 있도록 처리.
     */
    @ExceptionHandler(RuntimeException.class)
    protected BaseResponse<Void> handleRuntimeException(RuntimeException e) {

        log.error("런타임 예외: {}", e.getMessage());

        return new BaseResponse<>(INTERNAL_SERVER_ERROR, e);
    }

    /**
     * "@Valid" 유효성 검사 실패 예외 처리.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected BaseResponse<String> handleValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException 발생 : {}", e.getMessage());

        // 모든 validation 오류 메시지를 가져옴
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage()) // 기본 메시지 가져오기
                .findFirst() // 첫 번째 메시지만 사용
                .orElse("유효성 검증 실패");

        // 응답에 구체적인 에러 메시지를 포함
        return new BaseResponse<>(INVALID_INPUT_VALUE, errorMessage);
    }
}