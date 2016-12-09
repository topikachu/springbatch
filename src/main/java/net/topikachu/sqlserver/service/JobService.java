package net.topikachu.sqlserver.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

/**
 * Created by gongy on 2016/12/9.
 */
public class JobService {
    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("master")
    Job job;

    public void run() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("start", new Date())
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
    }

}
