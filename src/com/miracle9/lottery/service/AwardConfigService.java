package com.miracle9.lottery.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miracle9.lottery.dao.BaseDao;
import com.miracle9.lottery.entity.AwardConfig;

@Service
public class AwardConfigService {
	/**
	 * 开奖时间
	 */
	public static Long awardTime;

	public static AwardConfig config;

	@Autowired
	private BaseDao baseDao;

	public void loadAllConfig() {
		List<AwardConfig> list = baseDao.getList(AwardConfig.class, "from AwardConfig");
		if (list.isEmpty()) {
			AwardConfig c = new AwardConfig(100, 20000, 8);
			baseDao.add(c);
			list.add(c);
		}
		for (AwardConfig c : list) {
			if (c.getDayNum() > 0) {
				c.setDayinterval(c.getHourNum() * 60 * 60 * 1000 / c.getDayNum());
			} else {
				c.setDayinterval(Long.MAX_VALUE);
			}
			config = c;
			// 初始化开奖时间
			awardTime = System.currentTimeMillis();
			return;
		}
	}
}
