package com.wuqr.rabbitmq.utils;

/**
 * @author wql78
 * @title: SleepUtils
 * @description: 睡眠工具类
 * @date 2021-09-23 23:00:54
 */
public class SleepUtils {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
