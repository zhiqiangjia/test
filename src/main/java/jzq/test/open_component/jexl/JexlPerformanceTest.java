package jzq.test.open_component.jexl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import javax.script.Compilable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JexlPerformanceTest {
	// 线程数量
	private static int corePoolSize =  1;//2 * Runtime.getRuntime().availableProcessors();
	private static final ScriptEngineManager manager = new ScriptEngineManager();
	private static final ScriptEngine engine = manager.getEngineByName("javascript");
	private static final Compilable cengine = (Compilable) engine;
	// 循环次数
	static final Integer loopTime = 3000 * 1000;
	// 共享资源
	static AtomicInteger increment = new AtomicInteger(loopTime);
	// 信号
	static final Semaphore semaphore = new Semaphore(corePoolSize);

	final static ThreadLocalRandom random = ThreadLocalRandom.current();
	//自定义表达式
	static final String express = "var result = 5.6;\r\n"
				+ "if(a1 > a2) result += 0.7\r\n"
				+ "else if(a1 > a3) result += 0.8\r\n" 
				+ "else result += 0.3\r\n" + "\r\n" + "parseInt(result);";

	public static void main(String args[]) throws InterruptedException {
		ExecutorService scheduleService = Executors.newFixedThreadPool(corePoolSize, new ThreadFactory() {
			AtomicInteger increment = new AtomicInteger();
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("JexlTest-thread-" + increment.incrementAndGet());
				t.setDaemon(false);
				return t;
			}
		});
		long starTime = System.currentTimeMillis();
		for (int index = 0; index < corePoolSize; index ++) {
			scheduleService.submit(new Task());
		}
		Thread.sleep(50);
		semaphore.acquireUninterruptibly(corePoolSize);
		long endTime = System.currentTimeMillis();

		long useTime = (endTime - starTime);
		System.out.println("线程数量:" + corePoolSize + " 执行时间：" + useTime + " ms  TPS:" + (loopTime / useTime) * 1000 +" /s");
		
		scheduleService.shutdownNow();
	}

	static class Task implements Runnable {
		
		public void run() {
			int index = 1;
			try {
				long startTime = System.currentTimeMillis();
				semaphore.acquire();
				while (increment.decrementAndGet() > 0) {
					engine.put("a1", random.nextInt(100));
					engine.put("a2", random.nextInt(100));
					engine.put("a3", random.nextInt(100));
					cengine.compile(express).eval();
					index ++;
				}
				
				long endTime = System.currentTimeMillis();
				long useTime = (endTime - startTime);
				System.out.println(Thread.currentThread().getName() + " 循环 " + index + " 使用时间：" + useTime + " ms");
				semaphore.release();
			} catch (Exception e) {
				System.out.println(" 操作失败");
			}

		}
	}

}
