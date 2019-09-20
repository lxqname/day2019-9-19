package com.example.demo.util;

/**
 * 常量类
 * @author 86151
 */
public class Constant {
    /**
     * 到达近LIMITED_TIME时间内，将数据库的任务存储到内存中
     */
    public static final int LIMITED_TIME=120*60*1000;

    /**
     * 内存最小时间（存入任务时，小于这个时间，直接存入内存）
     */
    public static final int MEMORY_MIN=120;

    /**
     * 判断GET请求值
     */
    public static final String GET="get";

    /**
     * 成功状态码的最小值
     */
    public static final int STATUS_MIN=200;

    /**
     * 成功状态码的最大值
     */
    public static final int STATUS_MAX=210;
}
