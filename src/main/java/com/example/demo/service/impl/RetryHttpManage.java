package com.example.demo.service.impl;

import cn.hutool.http.HttpRequest;
import com.example.demo.util.Constant;
import com.example.demo.util.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

/**
 * @author 86151
 */
@Service
public class RetryHttpManage {

    /**
     * 打印日志
     */
    private static Logger logger = LoggerFactory.getLogger(RetryHttpManage.class);

    /**
     * 达到响应状态码
     * @param request
     * @return
     * @throws Exception
     */
    @Retryable(value = Exception.class, maxAttempts = Constant.MAX_ATTEMPTS, backoff = @Backoff(delay = 1000L, multiplier = 1.2))
    public int getStatus(HttpRequest request) throws Exception {
        logger.info("进行http请求重试");
        //判断请求是否为空
        if (Objects.isNull(request)) {
            throw new Exception(TaskStatus.REQUEST_NOT_FOUND);
        }
        //判断响应是否为空
        if (ObjectUtils.isEmpty(request.execute())) {
            throw new Exception(TaskStatus.RESPONSE_NOT_FOUND);
        }
        return request.execute().getStatus();
    }

//    @Recover
//    public int recover(){
//        logger.info("http请求重试结束");
//        return 0;
//    }

}
