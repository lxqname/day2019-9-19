package com.example.demo.service;

import com.example.demo.domian.entity.Task;

public interface ITaskService {
    /**
     * 存储任务
     * @param task
     * @return
     */
    Task saveTask(Task task);

    /**
     * 根据id查询任务
     * @param id
     * @return
     */
    Task getTaskDoById(Integer id);


}
