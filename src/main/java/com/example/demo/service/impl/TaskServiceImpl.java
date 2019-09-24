package com.example.demo.service.impl;

import cn.hutool.core.util.ObjectUtil;


import com.example.demo.util.Constant;
import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.entity.Task;
import com.example.demo.service.ITaskService;
import com.example.demo.util.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;


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

    @Resource
    AsyncTaskService asyncTaskService;

    @Override
    public Object saveTask(Task task) {
        //将状态置为0
        task.setStatus(TaskStatus.NOT_CONTROLLER_STATUS);
        //将id设为空
        task.setId(null);
        if (!ObjectUtil.isEmpty(task.getUrl())
                && !ObjectUtil.isEmpty(task.getTimeLimited())
                && !ObjectUtil.isEmpty(task.getMethod())
                && !ObjectUtil.isEmpty(task.getCreateTime())
                && !ObjectUtil.isEmpty(task.getUrl())
                && !ObjectUtil.isEmpty(task.getStatus())
                && !ObjectUtil.isEmpty(task.getExecuteTime())) {
            taskDao.saveTask(task);
            System.out.println(task.getId());
            if (task.getTimeLimited() < Constant.MEMORY_MIN) {
                cache.add(task);
            }
            return task;
        }
        return "不能为空";
    }

    @Override
    public Task getTaskDoById(Integer id) {
        return taskDao.getTaskById(id);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(new Date());
        for (int i = 1; i <= Constant.POOL_SUM; i++) {
            //启动项目
            asyncTaskService.sendTask();
        }
    }

    /**
     * 循环获取数据库中的任务，并将在120分钟内执行的任务存入内存中
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void storeInMemory() {
        System.out.println(new Date());
        System.out.println("定时循环数据库，把符合条件的存入内存");
        //获取状态值为0的任务且在120分钟内执行的任务
        taskDao.getAllByExecuteTimeBeforeAndStatus(new Date(System.currentTimeMillis() + Constant.LIMITED_TIME), TaskStatus.NOT_CONTROLLER_STATUS)
                .forEach(task -> {
                    //判断内存中是否已存入
                    if (!cache.parallelStream().filter(t -> t.getId().equals(task.getId())).findAny().isPresent()) {
                        System.out.println("将" + task.getId() + "存入内存");
                        cache.add(task);
                    }
                });
    }


}
