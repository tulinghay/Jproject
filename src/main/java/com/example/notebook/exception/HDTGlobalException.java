package com.example.notebook.exception;


import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice(annotations = {RestController.class})
@ResponseBody
public class HDTGlobalException {
    // 拦截指定特定异常类
    @ExceptionHandler(ArithmeticException.class)
    public String exceptionHandler(ArithmeticException exception){
        System.out.println("拦截ArithmeticException成功");
        return exception.getMessage();
    }

    @ExceptionHandler({MultipartException.class})
    public String exceptionHandlerSize(ArithmeticException exception) {
        System.out.println("拦截FileSizeLimitExceededException成功");
        return "文件太大了";
    }

}
