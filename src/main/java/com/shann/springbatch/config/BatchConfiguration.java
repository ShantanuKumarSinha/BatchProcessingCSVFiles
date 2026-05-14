package com.shann.springbatch.config;

import com.shann.springbatch.entity.User;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  private JobRepository jobRepository;
  private PlatformTransactionManager platformTransactionManager;
  private DataSource dataSource;

  public BatchConfiguration(
      JobRepository jobRepository,
      PlatformTransactionManager platformTransactionManager,
      DataSource dataSource) {
    this.jobRepository = jobRepository;
    this.platformTransactionManager = platformTransactionManager;
    this.dataSource = dataSource;
  }

  @Bean
  public FlatFileItemReader<User> reader() {
    LineMapper<User> lineMapper = new DefaultLineMapper<>();

    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setNames("id", "name", "email", "age");

    BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(User.class);

    ((DefaultLineMapper<User>) lineMapper).setLineTokenizer(tokenizer);
    ((DefaultLineMapper<User>) lineMapper).setFieldSetMapper(fieldSetMapper);

    FlatFileItemReader<User> reader = new FlatFileItemReader<>(lineMapper);

    reader.setResource(new ClassPathResource("csvFiles/users.csv"));
    reader.setLinesToSkip(1);

    return reader;
  }

  @Bean
  public ItemProcessor<User, User> processor() {
    return user -> {
      user.setName(user.getName().toUpperCase());
      return user;
    };
  }

  @Bean
  public JdbcBatchItemWriter<User> writer() {
    JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
    writer.setDataSource(dataSource);
    writer.setSql(
        """
        INSERT INTO batch_user (id, name, email, age)
        VALUES (:id, :name, :email, :age)
    """);
    writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
    return writer;
  }

  @Bean
  public Step step() {
    return new StepBuilder("csv-step", jobRepository)
        .<User, User>chunk(10)
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build();
  }

  @Bean
  public Job importUserJob() {
    return new JobBuilder("import-user-job", jobRepository).start(step()).build();
  }
}
