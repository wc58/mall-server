package top.chao58.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.chao58.util.R;

@ControllerAdvice
@Slf4j
public class GlobalExceptionController {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R exception(Exception e) {
        log.warn("发生错误：" + e.getMessage());
        return R.error().setMessage(e.getMessage());
    }


}
