package com.dxhy.order.consumer.handle;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.stereotype.Component;

/**
 * xxl测试定时任务
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:44
 */
@Component
@JobHandler(value="testTask")
public class TestTask extends IJobHandler {
	
	@Override
	public ReturnT<String> execute(String param) throws Exception {
		System.out.println("定时任务执行");
		
		return ReturnT.SUCCESS;
	}
}
