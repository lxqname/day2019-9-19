package com.example.demo.service.impl;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;


import com.example.demo.util.Constant;
import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.entity.Task;
import com.example.demo.service.ITaskService;
import com.example.demo.util.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;


/**
 * @author 86151
 */
@Service
public class TaskServiceImpl implements ITaskService, CommandLineRunner {

    /**
     * 定义全局变量cache
     */
    public static BlockingQueue<Task> cache = new PriorityBlockingQueue<>(100000, Comparator.comparingLong(t -> t.getExecuteTime().getTime()));


    @Autowired
    private ITaskDao taskDao;

    @Autowired
    RetryService retryService;

    @Override
    public Task saveTask(Task task) {
        if (task.getTimeLimited() < Constant.MEMORY_MIN) {
            cache.add(task);
        }
        return taskDao.saveTask(task);
    }

    @Override
    public Task getTaskDoById(Integer id) {
        return taskDao.getTaskById(id);
    }

    @Override
    public void run(String... args) throws Exception {
        //启动项目
        sendTask();
    }


    public void sendTask() {
        while (true) {
            //从内存中取得第一个元素并移除
            Task task = null;
            int status=0;
            try {
                task = cache.take();
                System.out.println(task);
                System.out.println("----");
                //判断现在时间是否到达任务执行时间
                if (task.getExecuteTime().before(new Date())) {
                    //得到请求
                    HttpRequest httpRequest = getRequestByUrl.apply(task);
                    //获取状态码
                    status = retryService.getStatus(httpRequest);
                    System.out.println("====" + status);
                } else {
                    cache.add(task);
                }
                Thread.sleep(Constant.SLEEP_MINE);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //根据返回的状态码设置任务的状态码
                task.setStatus(status == Constant.STATUS_MIN ? TaskStatus.CONTROLLER_STATUS : TaskStatus.CONTROLLER_FAILED_STATUS);
                taskDao.saveTask(task);
            }
        }
//        List<Task> collect = cache.stream()
//                //取得现在时间到达任务执行时间的任务的集合
//                .filter(t -> t.getExecuteTime().before(new Date()))
//                .map(t -> {
//                    //将任务传入executeRequest方法返回状态码
//                    Integer status = executeRequest(t);
//                    if (status >= Constant.STATUS_MIN && status <= Constant.STATUS_MAX) {
//                        //成功后，设置任务状态值为1
//                        t.setStatus(TaskStatus.CONTROLLER_STATUS);
//                    }
//                    return t;
//                    //取得状态值为1的任务的集合并存入数据库中
//                }).filter(t -> t.getStatus() == TaskStatus.CONTROLLER_STATUS)
//                .map(t -> taskDao.saveTask(t))
//                .collect(Collectors.toList());
//        //移除这个状态值为1的集合
//        cache.removeAll(collect);
    }

    /**
     * 根据传入的任务，返回相应类型的请求
     */
    Function<Task, HttpRequest> getRequestByUrl = t -> {
        if (t == null) {
            return null;
        }
        String taskOption = Optional.ofNullable(t.getMethod()).orElse("").toUpperCase();
        HttpRequest request = null;
        switch (taskOption) {
            case Constant.GET:
                request = HttpRequest.get(t.getUrl());
                break;
            case Constant.POST:
                request = HttpRequest.post(t.getUrl()).body(t.getData());
                break;
            default:
        }
        System.out.println("===========" + request);
        return request;
    };


    /**
     * 循环获取数据库中的任务，并将在120分钟内执行的任务存入内存中
     */
    @Scheduled(cron = "0/5 * * * * ? ")
    public void storeInMemory() {
        System.out.println("定时循环数据库，把符合条件的存入内存");
        //获取状态值为0的任务且在120分钟内执行的任务
        taskDao.getAllByExecuteTimeBeforeAndStatus(new Date(System.currentTimeMillis() + Constant.LIMITED_TIME), TaskStatus.NOT_CONTROLLER_STATUS)
                .forEach(task -> {
                    //判断内存中是否已存入
                    if (!cache.stream().filter(t->!t.getId().equals(task.getId())).findAny().isPresent()) {
                        System.out.println("存入内存");
                        System.out.println("llllllllll"+task.getId());
                        cache.add(task);
                    }
                });
    }


}
