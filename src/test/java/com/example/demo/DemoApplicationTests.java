package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.dao.ITaskDao;
import com.example.demo.dao.impl.TaskDaoImpl;
import com.example.demo.domian.dataobject.TaskDo;
import com.example.demo.domian.entity.Task;
import com.example.demo.mapper.TaskMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;



@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    ITaskDao taskDao;

    @Test
    public void contextLoads() {
        Task taskDoById = taskDao.getTaskById(2);
        System.out.println(taskDoById);
        System.out.println("--------"+taskDoById.getData());
        String data = taskDoById.getData();
        Object userName = JSON.parseObject(data).get("userName");
        System.out.println(userName);
        List<Task> allByStatus = taskDao.getAllByExecuteTimeBeforeAndStatus(new Date(),0);
        System.out.println("====="+allByStatus);
    }

}
