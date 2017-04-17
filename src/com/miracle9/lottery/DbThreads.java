package com.miracle9.lottery;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.miracle9.lottery.utils.LogManager;

public class DbThreads {
	public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

	public static void executor(Runnable runnable) {
		executor.execute(runnable);
		int size = executor.getQueue().size();
		if (size > 500) {
			LogManager.warn("DbThreads queue size：" + size);
		}
	}
}
