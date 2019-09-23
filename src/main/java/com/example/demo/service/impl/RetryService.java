package com.example.demo.service.impl;

import cn.hutool.http.HttpRequest;
import com.example.demo.util.Constant;
import com.example.demo.util.TaskStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import java.util.Objects;

/**
 * @author 86151
 */
@Service
public class RetryService {

    @Retryable(value = Exception.class, maxAttempts = Constant.MAX_ATTEMPTS, backoff = @Backoff(delay = 2000L, multiplier = 1.5))
    public int getStatus(HttpRequest request) throws Exception {
        System.out.println("-dsadsad");
        //判断请求是否为空
        if (Objects.isNull(request)) {
            throw new Exception(TaskStatus.REQUEST_NOT_FOUND);
        }
        System.out.println("121212");
        //判断响应是否为空
        if (ObjectUtils.isEmpty(request.execute())) {
            System.out.println("响应为空");
            throw new Exception(TaskStatus.RESPONSE_NOT_FOUND);
        }
        return request.execute().getStatus();
    }
}
