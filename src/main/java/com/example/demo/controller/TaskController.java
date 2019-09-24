package com.example.demo.controller;

import com.example.demo.domian.entity.Task;
import com.example.demo.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 86151
 */
@RestController
public class TaskController {

    @Autowired
    private ITaskService taskService;

    /**
     * 存储任务
     * @param task
     */
    @PostMapping("/saveTask")
    public Object saveTask(@RequestBody Task task){
        return taskService.saveTask(task);
    }

    /**
     * 根据id查询任务
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public Task getById(@RequestParam("id")Integer id){
        return taskService.getTaskDoById(id);
    }
}
