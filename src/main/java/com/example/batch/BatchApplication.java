package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Collections;

//@EnableScheduling
@EnableTask
@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }

    @Bean
    public TaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("scheduling-thread-");
        taskScheduler.setThreadGroupName("spring-scheduling");
        return taskScheduler;
    }

    //@Bean
    public CommandLineRunner simpleJobRunner(JobLauncher jobLauncher, Job simpleJob1, Job simpleJob2) {
        return args -> {
            try {
                System.out.println("start job , [thread.name=" + Thread.currentThread().getName() + "]");
                {
                    JobExecution execution1 = jobLauncher.run(simpleJob1, new JobParameters(Collections.singletonMap("readCount", new JobParameter(3L))));
                    System.out.println("Job1 Status : " + execution1.getStatus());
                    System.out.println("Job1 completed");
                }
                {
                    JobExecution execution2 = jobLauncher.run(simpleJob2, new JobParameters(Collections.singletonMap("readCount", new JobParameter(10L))));
                    System.out.println("Job2 Status : " + execution2.getStatus());
                    System.out.println("Job2 completed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Job failed");
            }
        };
    }
}
