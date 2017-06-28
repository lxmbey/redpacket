package com.miracle9.lottery;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.miracle9.lottery.service.AuthorizeLogService;
import com.miracle9.lottery.service.AwardConfigService;
import com.miracle9.lottery.utils.MyUtil;

@Component
public class MyApplicationContext implements ApplicationContextAware {

	@Override
	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
		ctx.getBean(AwardConfigService.class).loadAllConfig();
		ctx.getBean(AuthorizeLogService.class).loadCache();

		GameConfig gameConfig = ctx.getBean(GameConfig.class);
		gameConfig.begin = MyUtil.datetimeFormat(gameConfig.getBeginDate());
		gameConfig.end = MyUtil.datetimeFormat(gameConfig.getEndDate());
	}

}
