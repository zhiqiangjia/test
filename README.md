[TOC]

## 简述


## 使用方式


```
ScriptEngineManager manager = new ScriptEngineManager();
ScriptEngine engine = manager.getEngineByName("javascript");
String expressionStr = " a1 / 11 + a2 * 2.3 - 8 ";
engine.put("a1", 22);
engine.put("a2", 5);
System.out.println(cengine.compile(expressionStr).eval());
```


#### 常用函数

- 取整 

 
```
cengine.compile("parseInt(5/2)").eval();

输出 2
```

- 向上取整

```
cengine.compile("Math.ceil(5/2)").eval();

输出 3
```
- 四舍五入

```
cengine.compile("Math.round(5/2)").eval();

输出 2

```

- 求余

```
cengine.compile("32%3").eval();
```



## 性能测试

### 测试环境
- win 10 64位
- intel Core i8-8400 2.80GHz
- 8G内存
- 版本 jdk1.8
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
6(CPU个数) |48W+ TPS|
12(CPU个数二倍)|50W+ TPS|


### 详细结果：

```
# 单线程
scriptEngine-thread-1 循环 3000000 使用时间：14323 ms
线程数量:1 执行时间：14324 ms  TPS:209000 /s


## CPU 个数整数倍
scriptEngine-thread-1 循环 495660 使用时间：6217 ms
scriptEngine-thread-4 循环 495229 使用时间：6217 ms
scriptEngine-thread-5 循环 501722 使用时间：6217 ms
scriptEngine-thread-2 循环 517845 使用时间：6217 ms
scriptEngine-thread-3 循环 501301 使用时间：6217 ms
scriptEngine-thread-6 循环 488248 使用时间：6217 ms
线程数量:6 执行时间：6219 ms  TPS:482000 /s



## CPU 个数2倍
scriptEngine-thread-4 循环 247902 使用时间：5967 ms
scriptEngine-thread-3 循环 246780 使用时间：5966 ms
scriptEngine-thread-9 循环 248009 使用时间：5966 ms
scriptEngine-thread-12 循环 249860 使用时间：5966 ms
scriptEngine-thread-1 循环 252352 使用时间：5967 ms
scriptEngine-thread-5 循环 246443 使用时间：5966 ms
scriptEngine-thread-8 循环 253732 使用时间：5966 ms
scriptEngine-thread-6 循环 251059 使用时间：5966 ms
scriptEngine-thread-11 循环 249429 使用时间：5966 ms
scriptEngine-thread-7 循环 257943 使用时间：5966 ms
scriptEngine-thread-2 循环 247627 使用时间：5967 ms
scriptEngine-thread-10 循环 248875 使用时间：5966 ms
线程数量:12 执行时间：5968 ms  TPS:502000 /s


```


