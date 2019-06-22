package org.openea.eapboot.common.exception;

import org.openea.eapboot.common.utils.ResultUtil;
import org.openea.eapboot.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 */
@Slf4j
@RestControllerAdvice
public class RestCtrlExceptionHandler {

    @ExceptionHandler(EapbootException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleXCloudException(EapbootException e) {

        String errorMsg = "Eap exception";
        if (e!=null){
            errorMsg = e.getMsg();
            log.error(e.toString());
        }
        return new ResultUtil<>().setErrorMsg(500, errorMsg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleException(Exception e) {

        String errorMsg = "Exception";
        if (e!=null){
            errorMsg = e.getMessage();
            log.error(e.toString());
        }
        return new ResultUtil<>().setErrorMsg(500, errorMsg);
    }
}
