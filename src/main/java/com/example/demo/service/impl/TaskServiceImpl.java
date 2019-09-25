package com.example.demo.service.impl;

import cn.hutool.core.util.ObjectUtil;


import cn.hutool.http.HttpRequest;
import com.example.demo.util.Constant;
import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.entity.Task;
import com.example.demo.service.ITaskService;
import com.example.demo.util.TaskStatus;
import com.example.demo.util.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;


/**
 * @author 86151
 */
@Service
public class TaskServiceImpl implements ITaskService {

    /**
     * 打印日志
     */
    private static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    /**
     * 自定义线程池
     */
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(ThreadPool.CORE_POOL_SIZE, ThreadPool.MAX_POOL_SIZE, ThreadPool.KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>());


    /**
     * 定义全局变量cache(内存，存放数据)
     */
    public static BlockingQueue<Task> cache = new PriorityBlockingQueue<>(100000, Comparator.comparingLong(t -> t.getExecuteTime().getTime()));


    @Autowired
    private ITaskDao taskDao;


    @Resource
    TaskManage taskManage;

    @Override
    public Object saveTask(Task task) {
        //将状态置为0
        task.setStatus(TaskStatus.NOT_CONTROLLER_STATUS);
        //将id设为空
        task.setId(null);
        if (!ObjectUtil.isEmpty(task)
                && !ObjectUtil.isEmpty(task.getMethod())
                && !ObjectUtil.isEmpty(task.getCreateTime())
                && !ObjectUtil.isEmpty(task.getUrl())
                && !ObjectUtil.isEmpty(task.getStatus())
                && !ObjectUtil.isEmpty(task.getExecuteTime())) {
            taskDao.saveTask(task);
            if (task.getExecuteTime().before(new Date(System.currentTimeMillis() + Constant.LIMITED_TIME))) {
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
    public void sendTask() {
        while (true) {
            //从内存中取得第一个元素并移除
            Task task = cache.peek();
            //判断现在时间是否到达任务执行时间
            if (!ObjectUtil.isEmpty(task)) {
                if (task.getExecuteTime().before(new Date())) {
                    threadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Task task1 = null;
                            try {
                                task1 = cache.take();
                                int status = taskManage.run(task1);
                                //根据返回的状态码设置任务的状态码
                                task.setStatus(status == Constant.STATUS_MIN ? TaskStatus.CONTROLLER_STATUS : TaskStatus.CONTROLLER_FAILED_STATUS);
                                taskDao.saveTask(task);
                            } catch (Exception e) {
                                //e.printStackTrace();
                                logger.info("执行任务失败");
                            }
                        }
                    });
                }
            }
        }
    }




    /**
     * 循环获取数据库中的任务，并将在120分钟内执行的任务存入内存中
     */
    @Scheduled(cron = "0/10 * * * * ? ")
    public void storeInMemory() {
        logger.info("定时循环数据库，把符合条件的存入内存");
        //获取状态值为0的任务且在120分钟内执行的任务
        List<Task> tasks = taskDao.getAllByExecuteTimeBeforeAndStatus(new Date(System.currentTimeMillis() + Constant.LIMITED_TIME), TaskStatus.NOT_CONTROLLER_STATUS);
        cache.clear();
        logger.info("存入内存中" + tasks.size());
        cache.addAll(tasks);
    }
}
