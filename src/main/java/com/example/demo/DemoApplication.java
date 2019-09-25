package com.example.demo;

import com.example.demo.service.ITaskService;
import com.example.demo.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author 86151
 */
@SpringBootApplication
@EnableScheduling
@EnableRetry
public class DemoApplication implements ApplicationRunner {

    private static Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    @Autowired
    ITaskService taskService;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(()->{
            logger.info("监听");
            while (true){
                taskService.sendTask();
                try {
                    Thread.sleep(Constant.SLEEP_MINE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
