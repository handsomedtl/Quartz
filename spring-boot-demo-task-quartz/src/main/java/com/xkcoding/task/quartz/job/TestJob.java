package com.xkcoding.task.quartz.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.quartz.JobExecutionContext;
import org.quartz.impl.JobDetailImpl;

import com.xkcoding.task.quartz.entity.form.JobForm;
import com.xkcoding.task.quartz.job.base.BaseJob;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Test Job
 * </p>
 *
 * @package: com.xkcoding.task.quartz.job
 * @description: Test Job
 * @author: yangkai.shen
 * @date: Created in 2018-11-26 13:22
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@Slf4j
public class TestJob implements BaseJob {

	@Override
	public void execute(JobExecutionContext context) {
		String command = context.getMergedJobDataMap().getString(JobForm.COMMAND);
		
		executeCMD(((JobDetailImpl)context.getJobDetail()).getName(),command);
	}
	
	private void executeCMD(String jobName, String cmdString){
		try{
			log.info(jobName +" : {}", cmdString);
			Process proc = Runtime.getRuntime().exec(cmdString,null,new File("c:\\"));
						
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is,Charset.forName("GBK"));
			BufferedReader resultOutputWriter = new BufferedReader(isr);
			String line;
			while ((line = resultOutputWriter.readLine()) != null)
				log.info(line);
			
			InputStream stderr = proc.getErrorStream();
			InputStreamReader errorInputReader = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(errorInputReader);
			line = null;
			while ( (line = br.readLine()) != null)
				log.info("ERROR> " + line);;
		
			
				
			log.info(jobName + " 执行完毕");
		}
		catch(Exception e){
			log.error(jobName + " 执行失败，原因:"  + e.toString());
		}
	}
	
}