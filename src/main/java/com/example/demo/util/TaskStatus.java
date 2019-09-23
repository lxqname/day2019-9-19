package com.example.demo.util;

/**
 * @author 86151
 */
public interface TaskStatus {
    /**
     * 定义未处理状态
     */
    int NOT_CONTROLLER_STATUS = 0;

    /**
     * 定义已处理状态(但执行失败)
     */
    int CONTROLLER_FAILED_STATUS = 1;
    /**
     * 定义已处理状态(执行成功)
     */
    int CONTROLLER_STATUS = 2;

    /**
     * 请求无法找到
     */
    String REQUEST_NOT_FOUND = "请求无法找到";
    /**
     * 响应无法找到
     */
    String RESPONSE_NOT_FOUND = "响应无法找到";
}
