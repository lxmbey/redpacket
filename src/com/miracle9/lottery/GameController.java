package com.miracle9.lottery;

import org.springframework.stereotype.Component;

import com.miracle9.lottery.service.AwardConfigService;

/**
 * 抽奖控制器
 */
@Component
public class GameController {

	/**
	 * 获取出奖金额
	 * 
	 * @return int
	 */
	public synchronized int draw() {
		Long time = AwardConfigService.awardTime;
		if (System.currentTimeMillis() - time >= AwardConfigService.config.getDayinterval()) {
			AwardConfigService.awardTime = System.currentTimeMillis();
			return AwardConfigService.config.getMoney();
		}
		return 0;
	}

}
