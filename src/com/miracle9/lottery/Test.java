package com.miracle9.lottery;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.miracle9.lottery.utils.HttpUtil;

public class Test {
	public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);

	public static void main(String[] args) {
		//while (true) {
			for (int i = 0; i < 1; i++) {
				executor.execute(new Runnable() {

					@Override
					public void run() {
						String url = "http://localhost:8080/redpacket/getResult.do";
						String str = HttpUtil.sendPost(url, "openId=okQNpwP_yuWkxFNhCMhNTw93Znkw");
						if (Thread.currentThread().getName().equals("pool-1-thread-15")) {
							System.out.println(executor.getQueue().size() + "," + str);
						}
					}
				});
			}
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		//}
	}
}
