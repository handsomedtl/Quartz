package com.xkcoding.task.quartz.job;

import org.quartz.JobExecutionContext;

import com.xkcoding.task.quartz.entity.form.JobForm;
import com.xkcoding.task.quartz.job.base.BaseJob;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Hello Job
 * </p>
 *
 * @package: com.xkcoding.task.quartz.job
 * @description: Hello Job
 * @author: yangkai.shen
 * @date: Created in 2018-11-26 13:22
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Slf4j
public class HelloJob implements BaseJob {

    @Override
    public void execute(JobExecutionContext context) {    	
    	
        log.info("Hello Job 执行时间: {}", DateUtil.now());
    }
}