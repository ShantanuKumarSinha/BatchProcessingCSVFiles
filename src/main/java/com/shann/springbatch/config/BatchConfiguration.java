package com.shann.springbatch.config;

import com.shann.springbatch.entity.Product;
import com.shann.springbatch.entity.User;
import javax.sql.DataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final DataSource dataSource;

    public BatchConfiguration(JobRepository jobRepository, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.dataSource = dataSource;
    }

    // -------------------- USER JOB --------------------

    @NonNull
    static FlatFileItemReader<User> getUserFlatFileItemReader(
            LineMapper<User> lineMapper, DelimitedLineTokenizer tokenizer) {

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
    public FlatFileItemReader<User> userReader() {
        LineMapper<User> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "email", "age");

        return getUserFlatFileItemReader(lineMapper, tokenizer);
    }

    @Bean
    public ItemProcessor<User, User> userProcessor() {
        return user -> {
            user.setName(user.getName().toUpperCase());
            return user;
        };
    }

    @Bean
    public JdbcBatchItemWriter<User> userWriter() {
        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("""
        INSERT INTO batch_user (id, name, email, age)
        VALUES (:id, :name, :email, :age)
        """);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }

    @Bean
    public Step userStep() {
        return new StepBuilder("user-step", jobRepository)
                .<User, User>chunk(10)
                .reader(userReader())
                .processor(userProcessor())
                .writer(userWriter())
                .build();
    }

    @Bean
    public Job importUserJob() {
        return new JobBuilder("importUserJob", jobRepository)
                .start(userStep())
                .build();
    }

    // -------------------- PRODUCT JOB --------------------

    @NonNull
    static FlatFileItemReader<Product> getProductFlatFileItemReader(
            LineMapper<Product> lineMapper, DelimitedLineTokenizer tokenizer) {

        BeanWrapperFieldSetMapper<Product> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Product.class);

        ((DefaultLineMapper<Product>) lineMapper).setLineTokenizer(tokenizer);
        ((DefaultLineMapper<Product>) lineMapper).setFieldSetMapper(fieldSetMapper);

        FlatFileItemReader<Product> reader = new FlatFileItemReader<>(lineMapper);
        reader.setResource(new ClassPathResource("csvFiles/products.csv"));
        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public FlatFileItemReader<Product> productReader() {
        LineMapper<Product> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(
                "id",
                "name",
                "description",
                "brand",
                "category",
                "price",
                "currency",
                "stock",
                "ean",
                "color",
                "size",
                "availability",
                "internalId"
        );

        return getProductFlatFileItemReader(lineMapper, tokenizer);
    }

    @Bean
    public ItemProcessor<Product, Product> productProcessor() {
        return product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Product> productWriter() {
        JdbcBatchItemWriter<Product> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("""
        INSERT INTO batch_product
        (id, name, description, brand, category, price, currency, stock, ean, color, size, availability, internal_id)
        VALUES
        (:id, :name, :description, :brand, :category, :price, :currency, :stock, :ean, :color, :size, :availability, :internalId)
        """);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        return writer;
    }

    @Bean
    public Step productStep() {
        return new StepBuilder("product-step", jobRepository)
                .<Product, Product>chunk(10)
                .reader(productReader())
                .processor(productProcessor())
                .writer(productWriter())
                .build();
    }

    @Bean
    public Job importProductJob() {
        return new JobBuilder("importProductJob", jobRepository)
                .start(productStep())
                .build();
    }
}