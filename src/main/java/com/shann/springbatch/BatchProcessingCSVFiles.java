package com.shann.springbatch;

import java.util.Arrays;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BatchProcessingCSVFiles implements CommandLineRunner {

  private final JobOperator jobOperator;
  private final Job importUserJob;
  private final Job importProductJob;

  public BatchProcessingCSVFiles(JobOperator jobOperator, Job importUserJob, Job importProductJob) {
    this.jobOperator = jobOperator;
    this.importUserJob = importUserJob;
    this.importProductJob = importProductJob;
  }

  public static void main(String[] args) {
    SpringApplication.run(BatchProcessingCSVFiles.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    var jobArg = extractJobArg(args);
    if (jobArg == null || jobArg.isBlank()) jobArg = "all";
    System.out.println("\n========== Starting Batch Job: " + jobArg + " ==========\n");

    try {
      switch (jobArg.toLowerCase()) {
        case "user" -> {
          System.out.println("Executing: importUserJob");
          runJob(importUserJob, "importUserJob");
        }
        case "product" -> {
          System.out.println("Executing: importProductJob");
          runJob(importProductJob, "importProductJob");
        }
        case "all" -> {
          System.out.println("Executing: importUserJob");
          runJob(importUserJob, "importUserJob");
          System.out.println("Executing: importProductJob");
          runJob(importProductJob, "importProductJob");
        }
        default -> System.out.println("Invalid job argument: " + jobArg);
      }
      System.out.println("\n========== Batch Job Completed Successfully ==========\n");
    } catch (Exception e) {
      System.err.println("\n========== Batch Job Failed ==========");
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void runJob(Job job, String jobName) throws Exception {
    JobParameters params =
        new JobParametersBuilder()
            .addLong("run.id", System.currentTimeMillis())
            .addString("job.name", jobName)
            .toJobParameters();

    jobOperator.start(job, params);
  }

  private String extractJobArg(String[] args) {
    return Arrays.stream(args)
        .filter(a -> a.startsWith("--job="))
        .map(a -> a.substring("--job=".length()))
        .findFirst()
        .orElse(null);
  }
}
