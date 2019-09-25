package com.example.demo.dao.impl;

import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.dataobject.TaskDo;
import com.example.demo.domian.entity.Task;
import com.example.demo.mapper.TaskMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 86151
 */
@Service
public class TaskDaoImpl implements ITaskDao {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Task saveTask(Task task) {
        TaskDo taskDo = new TaskDo();
        BeanUtils.copyProperties(task, taskDo);
        taskMapper.save(taskDo);
        BeanUtils.copyProperties(taskDo, task);
        return task;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = new Task();
        taskMapper.getTaskDoById(id)
                .ifPresent(t -> BeanUtils.copyProperties(t, task));
        return task;
    }

    @Override
    public List<Task> getAllByExecuteTimeBeforeAndStatus(Date date, Integer status) {
        //查询根据状态值，执行任务时间之前的传入的时间的任务
        return taskMapper.getAllByExecuteTimeBeforeAndStatus(date, status)
                .stream().map(t -> {
                    Task task = new Task();
                    BeanUtils.copyProperties(t, task);
                    return task;
                }).collect(Collectors.toList());
    }
}
