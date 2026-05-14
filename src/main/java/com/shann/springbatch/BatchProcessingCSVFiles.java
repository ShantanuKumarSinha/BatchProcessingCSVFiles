package com.shann.springbatch;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchProcessingCSVFiles{

    public static void main(String[] args) {
        SpringApplication.run(BatchProcessingCSVFiles.class, args);
    }

//    @Bean
//    public Step step(JobRepository jobRepository) {
//        return new StepBuilder(jobRepository).tasklet((contribution, chunkContext) -> {
//            System.out.println("Hello world!");
//            return RepeatStatus.FINISHED;
//        }).build();
//    }
//
//    @Bean
//    public Job job(JobRepository jobRepository, Step step) {
//        return new JobBuilder("myJob", jobRepository)
//                .start(step)
//                .build();
//    }

}
