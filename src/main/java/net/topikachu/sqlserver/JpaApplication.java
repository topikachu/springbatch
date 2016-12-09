package net.topikachu.sqlserver;

import net.topikachu.sqlserver.service.JobService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JpaApplication {

    static Log log = LogFactory.getLog(JpaApplication.class);

    public static void main(String[] args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        ConfigurableApplicationContext ctx = SpringApplication.run(JpaApplication.class, args);
        JobService jobService = ctx.getBean(JobService.class);
        jobService.run();


    }
}
