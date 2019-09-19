package com.example.demo.dao;


import com.example.demo.domian.entity.Task;

import java.util.List;

/**
 * @author 86151
 */
public interface ITaskDao {

    /**
     * 保存任务
     * @param task
     * @return
     */
    Task saveTask(Task task);

    /**
     * 根据id查询任务
     * @return
     */
    Task getTaskDoById(Integer id);

    /**
     * 查询所有未处理及要存在内存中的任务
     * @return
     */
    List<Task> getAll(Integer status);
}
