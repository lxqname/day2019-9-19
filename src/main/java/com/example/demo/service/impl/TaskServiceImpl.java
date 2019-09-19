package com.example.demo.service.impl;

import cn.hutool.http.HttpRequest;
import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.entity.Task;
import com.example.demo.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @author 86151
 */
@Service
public class TaskServiceImpl implements ITaskService {

    public static final Map<Object,Task> cache=new HashMap<Object, Task>();

    @Autowired
    private ITaskDao taskDao;

    @Override
    public Task saveTask(Task task) {
        return taskDao.saveTask(task);
    }

    @Override
    public Task getTaskDoById(Integer id) {
        return taskDao.getTaskDoById(id);
    }

    @Scheduled(cron = "0/10 * * * * ? ")
    public void sendTask() {
        //从内存中获取任务
        Task task = cache.get("task");
        //创建Calendar
        Calendar cal = Calendar.getInstance();
        //将任务执行时间转为long型
        cal.setTime(task.getExecuteTime());
        long time1 = cal.getTimeInMillis();
        //求现在时间
        cal.setTime(new Date());
        long time2 = cal.getTimeInMillis();
        //判断现在时间是否到达规定时间
        if (time1 == time2) {
            //判断是否为get请求
            int sum = 0;
            if ("get".equals(task.getMethod())) {
                while (sum >= 5) {
                    //执行get请求,返回状态码
                    int status = HttpRequest.get(task.getUrl()).execute().getStatus();
                    if (status >= 200 && status <= 210) {
                        task.setStatus(2);
                        taskDao.saveTask(task);
                        sum = 5;
                    }
                    sum++;
                }
            } else {
                while (sum >= 5) {
                    //执行get请求,返回状态码
                    int status = HttpRequest.post(task.getUrl()).body(task.getData()).execute().getStatus();
                    if (status >= 200 && status <= 210) {
                        task.setStatus(2);
                        taskDao.saveTask(task);
                        sum = 5;
                    }
                    sum++;
                }
            }
        }
        else if (time1-time2<=task.getTimeLimited()){
            //获取状态值为0且将在120分钟内执行的任务
            List<Task> tasks = taskDao.getAll(0);
            tasks.forEach(t->{
                //存入内存中
                cache.put(t.getId(),t);
                //状态值改成1（已存入内存）
                t.setStatus(1);
                taskDao.saveTask(t);
            });
        }
    }

}
