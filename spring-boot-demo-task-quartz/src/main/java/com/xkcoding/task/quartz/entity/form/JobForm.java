package com.xkcoding.task.quartz.entity.form;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 定时任务详情
 * </p>
 *
 * @package: com.xkcoding.task.quartz.entity.form
 * @description: 定时任务详情
 * @author: yangkai.shen
 * @date: Created in 2018-11-26 13:42
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Data
@Accessors(chain = true)
public class JobForm {
	public final static String COMMAND = "command";
	
    /**
     * 定时任务全类名
     */
    private String jobClassName;
    /**
     * 任务组名
     */
    @NotBlank(message = "任务组名不能为空")
    private String jobGroupName;
    /**
     * 定时任务cron表达式
     */
    @NotBlank(message = "cron表达式不能为空")
    private String cronExpression;
    
    
    /**
     * 定时任务名
     */
    @NotBlank(message = "定时任务名不能为空")
    private String taskName;
    
    /**
     * 命令参数
     */
    @NotBlank(message = "执行命令不能为空")
    private String command;
    
    /**
     * 任务描述
     */
    private String description;
}
