package com.xkcoding.task.quartz.service.impl;

import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xkcoding.task.quartz.entity.domain.JobAndTrigger;
import com.xkcoding.task.quartz.entity.form.JobForm;
import com.xkcoding.task.quartz.job.TestJob;
import com.xkcoding.task.quartz.mapper.JobMapper;
import com.xkcoding.task.quartz.service.JobService;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Job Service
 * </p>
 *
 * @package: com.xkcoding.task.quartz.service.impl
 * @description: Job Service
 * @author: yangkai.shen
 * @date: Created in 2018-11-26 13:25
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Service
@Slf4j
public class JobServiceImpl implements JobService {
    private final Scheduler scheduler;
    private final JobMapper jobMapper;

    @Autowired
    public JobServiceImpl(Scheduler scheduler, JobMapper jobMapper) {
        this.scheduler = scheduler;
        this.jobMapper = jobMapper;
    }

    /**
     * 添加并启动定时任务
     *
     * @param form 表单参数 {@link JobForm}
     * @return {@link JobDetail}
     * @throws Exception 异常
     */
    @Override
    public void addJob(JobForm form) throws Exception {
        // 启动调度器
        scheduler.start();

        // 构建Job信息
        JobDetail jobDetail = JobBuilder.newJob(TestJob.class)
        						.withIdentity(form.getTaskName(), form.getJobGroupName())
        						.usingJobData(JobForm.COMMAND, form.getCommand())
        						.withDescription(form.getDescription())
        						.build();        
        
        // Cron表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder cron = CronScheduleBuilder.cronSchedule(form.getCronExpression());

        //根据Cron表达式构建一个Trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(form.getTaskName(), form.getCommand()).withSchedule(cron).build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("【定时任务】创建失败！", e);
            throw new Exception("【定时任务】创建失败！");
        }

    }

    /**
     * 删除定时任务
     *
     * @param form 表单参数 {@link JobForm}
     * @throws SchedulerException 异常
     */
    @Override
    public void deleteJob(JobForm form) throws SchedulerException {
        scheduler.pauseTrigger(TriggerKey.triggerKey(form.getTaskName(), form.getCommand()));
        scheduler.unscheduleJob(TriggerKey.triggerKey(form.getTaskName(), form.getJobGroupName()));
        scheduler.deleteJob(JobKey.jobKey(form.getTaskName(), form.getJobGroupName()));
    }

    /**
     * 暂停定时任务
     *
     * @param form 表单参数 {@link JobForm}
     * @throws SchedulerException 异常
     */
    @Override
    public void pauseJob(JobForm form) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(form.getTaskName(), form.getJobGroupName()));
    }

    /**
     * 恢复定时任务
     *
     * @param form 表单参数 {@link JobForm}
     * @throws SchedulerException 异常
     */
    @Override
    public void resumeJob(JobForm form) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(form.getTaskName(), form.getJobGroupName()));
    }

    /**
     * 重新配置定时任务
     *
     * @param form 表单参数 {@link JobForm}
     * @throws Exception 异常
     */
    @Override
    public void cronJob(JobForm form) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(form.getTaskName(), form.getCommand());
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(form.getCronExpression());

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            // 根据Cron表达式构建一个Trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            log.error("【定时任务】更新失败！", e);
            throw new Exception("【定时任务】创建失败！");
        }
    }

    /**
     * 查询定时任务列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页条数
     * @return 定时任务列表
     */
    @Override
    public PageInfo<JobAndTrigger> list(Integer currentPage, Integer pageSize,Map<String,Object> map) {
        PageHelper.startPage(currentPage, pageSize);
        List<JobAndTrigger> list = jobMapper.list(map);
        return new PageInfo<>(list);
    }

	@Override
	public List<String> taskGroupList() {
		
		return jobMapper.taskGroupList();
	}

	@Override
	public int getTaskNameCount(String taskName) {
		
		return jobMapper.taskNameCount(taskName);
	}
}
