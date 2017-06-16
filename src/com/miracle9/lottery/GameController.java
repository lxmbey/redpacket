package com.miracle9.lottery;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.miracle9.lottery.service.RedPacketLogService;
import com.miracle9.lottery.utils.LogManager;

/**
 * 抽奖控制器
 */
@Component
public class GameController {
	public static int YI_BAI = 10;
	public static int YI_SHI = 95;

	public static final int BAI = 8800;
	public static final int SHI = 1000;

	public static Random random = new Random();

	@Autowired
	private RedPacketLogService logService;

	public void init() {
		YI_BAI -= logService.getCount(BAI);
		YI_SHI -= logService.getCount(SHI);
		LogManager.info(String.format("还有%s个100，%s个10", YI_BAI, YI_SHI));
	}

	/**
	 * 获取出奖金额
	 * 
	 * @return int
	 */
	// public synchronized int draw() {
	// Long time = AwardConfigService.awardTime;
	// if (System.currentTimeMillis() - time >=
	// AwardConfigService.config.getDayinterval()) {
	// AwardConfigService.awardTime = System.currentTimeMillis();
	// return AwardConfigService.config.getMoney();
	// }
	// return 0;
	// }

	/**
	 * 获取出奖金额
	 * 
	 * @return int
	 */
	public synchronized int draw() {
		int result = 0;
		if (YI_BAI == 0 && YI_SHI == 0) {
			return 0;
		}
		if (YI_BAI == 0) {
			result = SHI;
		} else if (YI_SHI == 0) {
			result = BAI;
		} else {
			int i = random.nextInt(105);
			if (i < 10) {
				result = BAI;
			} else {
				result = SHI;
			}
		}
		
		if (result == BAI) {
			YI_BAI--;
		} else {
			YI_SHI--;
		}
		LogManager.info(String.format("还有%s个100，%s个10", YI_BAI, YI_SHI));
		return result;
	}

	public synchronized void addAward(int money) {
		if (money == BAI) {
			YI_BAI++;
		} else if (money == SHI) {
			YI_SHI++;
		}
		LogManager.info(String.format("还有%s个100，%s个10", YI_BAI, YI_SHI));
	}

}
