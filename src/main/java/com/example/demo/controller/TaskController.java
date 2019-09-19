package com.example.demo.controller;

import com.example.demo.domian.entity.Task;
import com.example.demo.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/saveTask")
    public void saveTask(@RequestBody Task task){
        taskService.saveTask(task);
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
