package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Configuration
public class JobDefinition {

    private final JobBuilderFactory jobBuilderFactory;

    public JobDefinition(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public Job simpleJob1(Step taskletStep, Step chunkStep) {
        return simpleJobDefinition("job1", taskletStep, chunkStep);
    }

    @Bean
    public Job simpleJob2(Step taskletStep, Step chunkStep) {
        return simpleJobDefinition("job2", taskletStep, chunkStep);
    }

    @Bean
    public Step taskletStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("tasklet-step").tasklet((contribution, chunkContext) -> {
            System.out.println("done tasklet-step , [thread.name=" + Thread.currentThread().getName() + "]");
            return RepeatStatus.FINISHED;
        }).build();
    }

    @Bean
    public Step chunkStep(StepBuilderFactory stepBuilderFactory, ItemReader<Integer> simpleItemReader) {

        return stepBuilderFactory.get("chunk-step").<Integer, String>chunk(5)
                .reader(simpleItemReader)
                .processor((ItemProcessor<Integer, String>) Object::toString)
                .writer(list -> {
                    System.out.println("------------***------------");
                    System.out.println(list);
                    System.out.println("------------***------------");
                }).build();
    }

    @Bean
    @StepScope
    public ItemReader<Integer> simpleItemReader(@Value("#{jobParameters['readCount']}") long readCount) {
        List<Integer> numbers = new Random().ints(1, 20000)
                .limit(readCount).boxed()
                .collect(Collectors.toList());
        AtomicInteger index = new AtomicInteger(0);
        return () -> {
            if (index.get() == numbers.size()) {
                return null;
            }
            return numbers.get(index.getAndIncrement());
        };
    }

    private Job simpleJobDefinition(String jobName, Step taskletStep, Step chunkStep) {
        return jobBuilderFactory.get(jobName)
                .start(taskletStep)
                .next(chunkStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        System.out.println("Executing job >> [jobName=" + jobExecution.getJobInstance().getJobName() + "],[executionId=" + jobExecution.getId() + "], with params [" + jobExecution.getJobParameters() + "]");
                    }
                    @Override
                    public void afterJob(JobExecution jobExecution) {
                    }
                })
                .build();
    }
}
