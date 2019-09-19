package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.domian.dataobject.TaskDo;
import com.example.demo.mapper.TaskMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    TaskMapper taskMapper;

    @Test
    public void contextLoads() {
        TaskDo taskDoById = taskMapper.getTaskDoById(1);
        System.out.println(taskDoById);
        System.out.println(taskDoById.getData());
        String data = taskDoById.getData();
        Object userName = JSON.parseObject(data).get("userName");
        System.out.println(userName);
        LocalDateTime createTime = taskDoById.getCreateTime();
    }

}
