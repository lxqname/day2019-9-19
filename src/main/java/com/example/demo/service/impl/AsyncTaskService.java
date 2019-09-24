package com.example.demo.service.impl;

import cn.hutool.http.HttpRequest;
import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.entity.Task;
import com.example.demo.service.impl.RetryService;
import com.example.demo.service.impl.TaskServiceImpl;
import com.example.demo.util.Constant;
import com.example.demo.util.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author 86151
 */
@Service
public class AsyncTaskService {

    @Autowired
    private ITaskDao taskDao;

    @Autowired
    RetryService retryService;

    @Async
    public void sendTask() {
        System.out.println(Thread.currentThread().getName()+"开启新线程执行" );
        while (true) {
            Task task = null;
            int status = 0;
            try {
                //从内存中取得第一个元素并移除
                task = TaskServiceImpl.cache.take();
                //判断现在时间是否到达任务执行时间
                if (task.getExecuteTime().before(new Date())) {
                    System.out.println("执行" + task.getId() + "号任务");
                    //得到请求
                    HttpRequest httpRequest = getRequestByUrl.apply(task);
                    //获取状态码
                    status = retryService.getStatus(httpRequest);
                    System.out.println("状态码为" + status);
                    //根据返回的状态码设置任务的状态码
                    task.setStatus(status == Constant.STATUS_MIN ? TaskStatus.CONTROLLER_STATUS : TaskStatus.CONTROLLER_FAILED_STATUS);
                    taskDao.saveTask(task);
                } else {
                    TaskServiceImpl.cache.add(task);
                }
                //Thread.sleep(Constant.SLEEP_MINE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
