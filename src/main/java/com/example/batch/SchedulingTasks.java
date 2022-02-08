package com.example.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class SchedulingTasks {

    @Scheduled(timeUnit = TimeUnit.SECONDS, fixedRate = 5)
    public void task1() {
        System.out.println("task1 >> start task  [thread.name='" + Thread.currentThread().getName() + "'] " + new Date());
        try {
            System.out.println("task1 >> i am working");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("task1 >> i finish my task");
        }
    }

    @Scheduled(fixedRate = 2000)
    public void task2() {
        System.out.println("task2 >> start task  [thread.name='" + Thread.currentThread().getName() + "'] " + new Date());
    }
}
