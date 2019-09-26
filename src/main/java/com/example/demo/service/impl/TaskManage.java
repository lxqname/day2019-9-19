package com.example.demo.service.impl;

import cn.hutool.http.HttpRequest;
import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.entity.Task;
import com.example.demo.util.Constant;
import com.example.demo.util.TaskStatus;
import com.example.demo.util.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author 86151
 */
@Service
public class TaskManage {

    /**
     * 打印日志
     */
    private static Logger logger = LoggerFactory.getLogger(TaskManage.class);


    @Autowired
    ITaskDao taskDao;

    @Autowired
    RetryHttpManage retryHttpManage;


    @Retryable(value = Exception.class, maxAttempts = Constant.MAX_ATTEMPTS, backoff = @Backoff(delay = 1000L, multiplier = 1.2))
    public int run(Task task) throws Exception {
        int status =0;
        logger.info(Thread.currentThread().getName() + "执行" + task.getId() + "号任务");
        //得到请求
        HttpRequest httpRequest = getRequestByUrl.apply(task);
        //获取状态码
        status = retryHttpManage.getStatus(httpRequest);
        logger.info("状态码为" + status);
        return status;
    }

    @Recover
    public int recover(){
        logger.info("任务重试结束");
        return 0;
    }

    /**
     * 根据传入的任务，返回相应类型的请求
     */
    Function<Task, HttpRequest> getRequestByUrl = t -> {
        HttpRequest request = null;
        String taskOption = Optional.ofNullable(t.getMethod()).orElse("").toUpperCase();
        switch (taskOption) {
            case Constant.GET:
                request = HttpRequest.get(t.getUrl());
                break;
            case Constant.POST:
                request = HttpRequest.post(t.getUrl()).body(t.getData());
                break;
            default:
        }
        return request;
    };


}
