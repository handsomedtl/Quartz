package com.xkcoding.task.quartz.controller;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.xkcoding.task.quartz.common.ApiResponse;
import com.xkcoding.task.quartz.entity.domain.JobAndTrigger;
import com.xkcoding.task.quartz.entity.form.JobForm;
import com.xkcoding.task.quartz.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

/**
 * <p>
 * Job Controller
 * </p>
 *
 * @package: com.xkcoding.task.quartz.controller
 * @description: Job Controller
 * @author: yangkai.shen
 * @date: Created in 2018-11-26 13:23
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {
    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * 保存定时任务
     */
    @PostMapping
    public ResponseEntity<ApiResponse> addJob(@Valid JobForm form) {
        try {
            jobService.addJob(form);
        } catch (Exception e) {
        	log.error("新建任务失败",e);
            return new ResponseEntity<>(ApiResponse.msg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(ApiResponse.msg("操作成功"), HttpStatus.CREATED);
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteJob(JobForm form) throws SchedulerException {
        if (StrUtil.hasBlank(form.getJobGroupName(), form.getTaskName())) {
            return new ResponseEntity<>(ApiResponse.msg("参数不能为空"), HttpStatus.BAD_REQUEST);
        }

        jobService.deleteJob(form);
        return new ResponseEntity<>(ApiResponse.msg("删除成功"), HttpStatus.OK);
    }

    /**
     * 暂停定时任务
     */
    @PutMapping(params = "pause")
    public ResponseEntity<ApiResponse> pauseJob(JobForm form) throws SchedulerException {
        if (StrUtil.hasBlank(form.getJobGroupName(), form.getTaskName())) {
            return new ResponseEntity<>(ApiResponse.msg("参数不能为空"), HttpStatus.BAD_REQUEST);
        }

        jobService.pauseJob(form);
        return new ResponseEntity<>(ApiResponse.msg("暂停成功"), HttpStatus.OK);
    }

    /**
     * 恢复定时任务
     */
    @PutMapping(params = "resume")
    public ResponseEntity<ApiResponse> resumeJob(JobForm form) throws SchedulerException {
        if (StrUtil.hasBlank(form.getJobGroupName(), form.getTaskName())) {
            return new ResponseEntity<>(ApiResponse.msg("参数不能为空"), HttpStatus.BAD_REQUEST);
        }

        jobService.resumeJob(form);
        return new ResponseEntity<>(ApiResponse.msg("恢复成功"), HttpStatus.OK);
    }

    /**
     * 修改定时任务，定时时间
     */
    @PutMapping(params = "cron")
    public ResponseEntity<ApiResponse> cronJob(@Valid JobForm form) {
        try {
            jobService.cronJob(form);
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.msg(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(ApiResponse.msg("修改成功"), HttpStatus.OK);
    }

    /*@GetMapping
    public ResponseEntity<ApiResponse> jobList(Integer currentPage, Integer pageSize) {     
    	
    	if (ObjectUtil.isNull(currentPage)) {
            currentPage = 1;
        }
        if (ObjectUtil.isNull(pageSize)) {
            pageSize = 10;
        }
        PageInfo<JobAndTrigger> all = jobService.list(currentPage, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(Dict.create().set("total", all.getTotal()).set("data", all.getList())));
    }*/
    
    @GetMapping
    public ResponseEntity<ApiResponse> jobList(Integer draw, Integer start,Integer length,String sortFeild,String sortDir,String searchWord) {
    	int currentPage = start / length + 1;
    	int pageSize = length;
    	
    	if (ObjectUtil.isNull(currentPage)) {
            currentPage = 1;
        }
        if (ObjectUtil.isNull(pageSize)) {
            pageSize = 10;
        }
        Map<String,Object> map = new HashMap<>();
        if(!StringUtils.isEmpty(sortFeild)){
        	map.put("sortFeild", sortFeild);
        	map.put("sortDir", sortDir);
        }
        
        if(!StringUtils.isEmpty(searchWord)){
        	map.put("searchWord", searchWord);
        }
        PageInfo<JobAndTrigger> all = jobService.list(currentPage, pageSize,map);
        return ResponseEntity.ok(ApiResponse.ok(Dict.create().set("total", all.getTotal()).set("data", all.getList()).set("draw", draw)));
    }
    
    @GetMapping(params = "taskGroupList")
    public ResponseEntity<ApiResponse> taskGroupList() {
    	List<String> all = jobService.taskGroupList();
       
        return ResponseEntity.ok(ApiResponse.ok(Dict.create().set("total", all.size()).set("data", all)));
    }
    
    @PostMapping(params = "checkExist")
    public Boolean checkExist(String taskName) {
    	int count = jobService.getTaskNameCount(taskName);
    	
    	return 0 == count ? true:false;
    }

}
