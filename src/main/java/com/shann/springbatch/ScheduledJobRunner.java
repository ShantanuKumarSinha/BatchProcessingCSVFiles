package com.shann.springbatch;

import java.time.LocalDateTime;
import org.springframework.batch.core.job.Job;

import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJobRunner {

    private final JobOperator jobOperator;
    private final Job importMovieRatingJob;

    public ScheduledJobRunner(JobOperator jobOperator, Job importMovieRatingJob) {
        this.jobOperator = jobOperator;
        this.importMovieRatingJob = importMovieRatingJob;
    }

    @Scheduled(cron = "0 20 16 * * *") // every day at 2:00 AM
    public void runMovieRatingImport() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .addString("scheduled.at", LocalDateTime.now().toString())
                .toJobParameters();

        jobOperator.start(importMovieRatingJob, params);
    }
}