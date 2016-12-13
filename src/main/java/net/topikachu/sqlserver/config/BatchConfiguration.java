package net.topikachu.sqlserver.config;

import net.topikachu.sqlserver.exception.InvalidDataException;
import net.topikachu.sqlserver.exception.InvalidRecordBaseException;
import net.topikachu.sqlserver.jpa.entity.SampleEntity;
import net.topikachu.sqlserver.jpa.entity.TargetEntity;
import net.topikachu.sqlserver.service.MessageProcess;
import net.topikachu.sqlserver.service.RangedSampleQueryProvider;
import net.topikachu.sqlserver.service.SaveErrorService;
import net.topikachu.sqlserver.service.SqlService;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    @StepScope
    public JpaPagingItemReader reader(EntityManagerFactory emf, @Value("#{stepExecutionContext[startId]}") String startId, @Value("#{stepExecutionContext[endId]}") String endId) {
        JpaPagingItemReader jpaPagingItemReader = new JpaPagingItemReader();
        jpaPagingItemReader.setEntityManagerFactory(emf);
        RangedSampleQueryProvider rangedSampleQueryProvider = new RangedSampleQueryProvider(startId, endId);
        jpaPagingItemReader.setQueryProvider(rangedSampleQueryProvider);
        return jpaPagingItemReader;
    }

    @Bean
    @Autowired
    @StepScope
    public JpaItemWriter writer(EntityManagerFactory emf) {
        JpaItemWriter writer = new JpaItemWriter();
        writer.setEntityManagerFactory(emf);
        return writer;

    }

    @Bean
    @Autowired
    @StepScope
    public ItemProcessorAdapter<SampleEntity, TargetEntity> itemProcessorAdapter(MessageProcess messageProcess) {
        ItemProcessorAdapter<SampleEntity, TargetEntity> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(messageProcess);
        adapter.setTargetMethod("exchange");
        return adapter;
    }

    @Bean
    @StepScope
    @Autowired
    TaskExecutorPartitionHandler get(@Value("#{jobParameters[gridSize] ?: 4}") long gridSize, @Qualifier("step") Step step, TaskExecutor taskExecutor) {
        TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(step);
        taskExecutorPartitionHandler.setGridSize((int) gridSize);
        taskExecutorPartitionHandler.setTaskExecutor(taskExecutor);
        return taskExecutorPartitionHandler;

    }


    @Bean
    @Autowired
    public Step master(SqlService sqlService, TaskExecutor taskExecutor, TaskExecutorPartitionHandler partitionHandler) {
        return steps.get("master").partitioner("step", gridSize -> {
            List<String> ids = sqlService.partitionIds(gridSize);
            return IntStream.range(0, gridSize)
                    .mapToObj(i -> {
                        String startId;
                        String endId;
                        if (i == 0) {
                            startId = null;
                        } else {
                            startId = ids.get(i - 1);
                        }
                        if (i == gridSize - 1) {
                            endId = null;
                        } else {
                            endId = ids.get(i);
                        }

                        ExecutionContext executionContext = new ExecutionContext();
                        executionContext.put("startId", startId);
                        executionContext.put("endId", endId);
                        return new Tuple2<String, ExecutionContext>("partition-" + i, executionContext);
                    })
                    .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
        })
                .partitionHandler(partitionHandler)
                .taskExecutor(taskExecutor)
                .build();
    }


    @Bean
    @Autowired
    public Job job(@Qualifier("master") Step master) {
        return jobs.get("job").start(master)
                .build();
    }

    @Bean
    @Autowired
    public Step step(JpaPagingItemReader<SampleEntity> reader, ItemProcessorAdapter<SampleEntity, TargetEntity> processor, JpaItemWriter<TargetEntity> writer, SaveErrorService saveErrorService) {
        return steps.get("step")
                .<SampleEntity, TargetEntity>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(InvalidDataException.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(new SkipListener<SampleEntity, TargetEntity>() {
                    @Override
                    public void onSkipInRead(Throwable t) {

                    }

                    @Override
                    public void onSkipInWrite(TargetEntity item, Throwable t) {

                    }

                    @Override
                    public void onSkipInProcess(SampleEntity item, Throwable t) {
                        if (t instanceof InvalidRecordBaseException) {
                            saveErrorService.saveError((InvalidRecordBaseException) t);
                        }

                    }
                })
                .build();
    }


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(10);

        return threadPoolTaskExecutor;
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
