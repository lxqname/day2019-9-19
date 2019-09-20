package com.example.demo.dao;


import com.example.demo.domian.entity.Task;

import java.util.Date;
import java.util.List;

/**
 * @author 86151
 */
public interface ITaskDao {

    /**
     * 保存任务/更新任务
     *
     * @param task
     * @return
     */
    Task saveTask(Task task);

    /**
     * 根据id查询任务
     *
     * @param id
     * @return
     */
    Task getTaskById(Integer id);

    /**
     * 查询根据状态值，执行任务时间之前的传入的时间的任务
     *
     * @param date
     * @param status
     * @return
     */
    List<Task> getAllByExecuteTimeBeforeAndStatus(Date date, Integer status);
}
