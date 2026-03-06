package com.example.milkteasystem.handler;

import cn.dev33.satoken.context.SaHolder;
import com.example.milkteasystem.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.NoPermissionException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 权限不足异常
     */
    @ExceptionHandler(NoPermissionException.class)
    public Result noPermissionException(NoPermissionException e) {
        SaHolder.getResponse().setStatus(403);
        return Result.error(e.getMessage());
    }
}
