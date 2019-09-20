package com.example.demo.service.impl;

import cn.hutool.http.HttpRequest;
import com.example.demo.util.Constant;
import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.entity.Task;
import com.example.demo.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author 86151
 */
@Service
public class TaskServiceImpl implements ITaskService {

    /**
     * 定义全局变量cache
     */
    public static final Queue<Task> cache = new PriorityBlockingQueue<>(100000, Comparator.comparingLong(t -> t.getExecuteTime().getTime()));


    @Autowired
    private ITaskDao taskDao;

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

    /**
     * 循环执行内存中的任务并移除任务
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void sendTask() {
        //遍历内存中的任务
        cache.forEach(task -> {
            //比较任务执行时间与现在时间
            if (task.getExecuteTime().before(new Date())) {
                int status;
                //判断是否为get请求
                if (Constant.GET.equals(task.getMethod())) {
                    //执行get请求,返回状态码
                    status = HttpRequest.get(task.getUrl()).execute().getStatus();
                    System.out.println(status);
                } else {
                    //执行post请求,返回状态码
                    status = HttpRequest.post(task.getUrl()).body(task.getData()).execute().getStatus();
                }
                //判断请求返回的状态码
                if (status >= Constant.STATUS_MIN && status <= Constant.STATUS_MAX) {
                    //成功后，设置状态值为1
                    task.setStatus(1);
                    //改变数据库中的状态
                    taskDao.saveTask(task);
                }
                //将内存中的任务移除
                cache.remove(task);
            }
        });
    }


    /**
     * 循环获取数据库中的任务，并将在120分钟内执行的任务存入内存中
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void storeInMemory() {
        //获取状态值为0的任务且在120分钟内执行的任务
        taskDao.getAllByExecuteTimeBeforeAndStatus(new Date(System.currentTimeMillis()+Constant.LIMITED_TIME),0)
                .forEach(task -> {
                //判断内存中是否已存入
                if (!cache.contains(task)) {
                    cache.add(task);
                }
        });
    }


}
