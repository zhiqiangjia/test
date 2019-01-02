# test
开源组件性能测试
[TOC]

## 简述
JEXL 是apache 提供的Java 脚本引擎, 可执行javascript 语言脚本; 主要介绍javascript执行方式，和简单性能测试

## 使用方式

#### Step 1
定义声明脚本

```
private final ScriptEngineManager manager = new ScriptEngineManager();
private final ScriptEngine engine = manager.getEngineByName("javascript");
```

#### Step 2
定义表达式并设置参数 
```
String expressionStr = " a1 / 11 + a2 * 2.3 - 8 ";
engine.put("a1", 22);
engine.put("a2", 5);
```
#### Step 3
执行结果

```
System.out.println(cengine.compile(expressionStr).eval());

```

#### 其他
支持javascript 语法 if while 函数等


## 性能测试

### 测试环境
- win 10
- intel Core i7-7700 3.60GHz
- 8G内存
- 测试语句

```
String express = "var result = 5.6;\r\n" + 
				"if(a1 > a2) result += 0.7\r\n" + 
				"else if(a1 > a3) result += 0.8\r\n" + 
				"else result += 0.3\r\n" + 
				"parseInt(result);";
```




### 测试结果

线程数量 | 300W次使用时间 |
---|---
1 | 21W+ TPS |
8(CPU个数) |43W+ TPS|
16(CPU个数二倍)|44W+ TPS|


### 详细结果：

```
# 单线程
JexlTest-thread-1 循环 3000000 使用时间：14158 ms
线程数量:1 执行时间：14159 ms  TPS:211000 /s

## CPU 个数整数倍
JexlTest-thread-3 循环 372878 使用时间：6867 ms
JexlTest-thread-7 循环 376529 使用时间：6867 ms
JexlTest-thread-1 循环 376708 使用时间：6867 ms
JexlTest-thread-8 循环 373569 使用时间：6867 ms
JexlTest-thread-5 循环 373263 使用时间：6867 ms
JexlTest-thread-4 循环 373008 使用时间：6867 ms
JexlTest-thread-2 循环 377198 使用时间：6867 ms
JexlTest-thread-6 循环 376854 使用时间：6867 ms
线程数量:8 执行时间：6868 ms  TPS:436000 /s


## CPU 个数2倍
JexlTest-thread-9 循环 189900 使用时间：6805 ms
JexlTest-thread-13 循环 188778 使用时间：6804 ms
JexlTest-thread-4 循环 188125 使用时间：6805 ms
JexlTest-thread-1 循环 186679 使用时间：6805 ms
JexlTest-thread-5 循环 188818 使用时间：6805 ms
JexlTest-thread-2 循环 187190 使用时间：6805 ms
JexlTest-thread-16 循环 187176 使用时间：6804 ms
JexlTest-thread-3 循环 185317 使用时间：6805 ms
JexlTest-thread-8 循环 189725 使用时间：6805 ms
JexlTest-thread-11 循环 187336 使用时间：6804 ms
JexlTest-thread-10 循环 186702 使用时间：6804 ms
JexlTest-thread-12 循环 186370 使用时间：6804 ms
JexlTest-thread-6 循环 186732 使用时间：6805 ms
JexlTest-thread-7 循环 184328 使用时间：6805 ms
JexlTest-thread-14 循环 188563 使用时间：6804 ms
JexlTest-thread-15 循环 188276 使用时间：6804 ms
线程数量:16 执行时间：6807 ms  TPS:440000 /s

```


### 核心代码


```
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

```
