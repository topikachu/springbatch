package net.topikachu.sqlserver.config;

import net.topikachu.sqlserver.batch.DynamicJpaPagingItemReader;
import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import net.topikachu.sqlserver.jpa.entity.TargetEntity;
import net.topikachu.sqlserver.service.MessageProcess;
import net.topikachu.sqlserver.service.SqlService;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by gongy on 2016/12/9.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;


    @Bean
    @Autowired
    public JobRepository getJobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return (JobRepository) factory.getObject();
    }


    @Bean
    @Autowired
    public JpaPagingItemReader reader(EntityManagerFactory emf) {
        DynamicJpaPagingItemReader jpaPagingItemReader = new DynamicJpaPagingItemReader();
        jpaPagingItemReader.setEntityManagerFactory(emf);
        return jpaPagingItemReader;
    }

    @Bean
    @Autowired
    public JpaItemWriter writer(EntityManagerFactory emf) {
        JpaItemWriter writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);
        return writer;

    }

    @Bean
    @Autowired
    public ItemProcessorAdapter<SampleEntity, TargetEntity> itemProcessorAdapter(MessageProcess messageProcess) {
        ItemProcessorAdapter<SampleEntity, TargetEntity> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(messageProcess);
        adapter.setTargetMethod("exchange");
        return adapter;
    }


    @Bean
    @Autowired
    public Step master(SqlService sqlService, TaskExecutor taskExecutor) {
        //int gridSize=4;
        List<String> ids = sqlService.partitionIds(4);
        return steps.get("master").partitioner("step", gridSize ->
                IntStream.range(0, gridSize)
                        .mapToObj(i -> {
                            String startId = null;
                            String endId = null;
                            if (i != 0) {
                                startId = ids.get(i - 1);
                            }
                            if (i != ids.size() - 1) {
                                endId = ids.get(i);
                            }
                            ExecutionContext executionContext = new ExecutionContext();
                            executionContext.put("startId", startId);
                            executionContext.put("endId", endId);
                            return new Tuple2<String, ExecutionContext>("partition-" + i, executionContext);
                        })
                        .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2)))
                .gridSize(4)
                .taskExecutor(taskExecutor)
                .build();
    }


    @Bean
    @Autowired
    public Job job(Step step) {
        return jobs.get("job").start(step).build();
    }

    @Bean
    @Autowired
    public Step step(JpaPagingItemReader<SampleEntity> reader, ItemProcessorAdapter<SampleEntity, TargetEntity> processor, JpaItemWriter<TargetEntity> writer) {
        return steps.get("step")
                .<SampleEntity, TargetEntity>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }


    @Bean
    public SimpleAsyncTaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    @Autowired
    public JobLauncher jobLauncher(JobRepository jobRepository, TaskExecutor taskExecutor) {
        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository);
        simpleJobLauncher.setTaskExecutor(taskExecutor);
        return simpleJobLauncher;
    }
}
