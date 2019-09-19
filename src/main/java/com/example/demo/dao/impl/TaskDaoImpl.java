package com.example.demo.dao.impl;

import com.example.demo.dao.ITaskDao;
import com.example.demo.domian.dataobject.TaskDo;
import com.example.demo.domian.entity.Task;
import com.example.demo.mapper.TaskMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskDaoImpl implements ITaskDao {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public Task saveTask(Task task) {
        TaskDo taskDo = new TaskDo();
        TaskDo taskDo1 = taskMapper.save(taskDo);
        if (null!=taskDo1){
            BeanUtils.copyProperties(taskDo1,task);
        }
        return task;
    }

    @Override
    public Task getTaskDoById(Integer id) {
        TaskDo taskDo = taskMapper.getTaskDoById(id);
        Task task = new Task();
        if (null!=taskDo){
            BeanUtils.copyProperties(taskDo,task);
            return task;
        }
       return task;
    }

    @Override
    public List<Task> getAll(Integer status) {
        Date date = new Date();
        List<TaskDo> taskDos = taskMapper.getAllByExecuteTimeBeforeAndStatus(date,0);
        if (0!=taskDos.size()){
            return taskDos.stream().map(t->{
                Task task = new Task();
                BeanUtils.copyProperties(t,task);
                return task;
            }).collect(Collectors.toList());
        }
        return new ArrayList<Task>();
    }


}
