package jzq.test.open_component.scriptEngine;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


import junit.framework.TestCase;

public class ScriptEngineUseTemplate extends TestCase {
	
	 private final ScriptEngineManager manager = new ScriptEngineManager();
	 private final ScriptEngine engine = manager.getEngineByName("javascript");
	 private final Compilable cengine = (Compilable) engine;
	
	/**
	 *  乘法测试 - 无参
	 * @throws Exception 
	 */
	public void testUseMultiplicationNoArguments() throws Exception {    
		System.out.println(cengine.compile("23 *33").eval());
	}
	
	/**
	 *  乘法测试 - 有参
	 * @throws Exception
	 */
	public void testUseMultipication() throws Exception {
		String expresss = "23*param";
		engine.put("param", 44);
		System.out.println(cengine.compile(expresss).eval());
	}
	
	/**
	 * 
	 *  四则预算
	 * @throws Exception 
	 * 
	 */
	public void testCompositeArithmetic() throws Exception {
		engine.put("a1", 22);
		engine.put("a2", 5);

		String expressionStr = " a1 / 11 + a2 * 2.3 - 8 ";
		System.out.println(cengine.compile(expressionStr).eval());
	}
	
	/**
	 * 常用函数
	 * 
	 * @throws Exception
	 */
	public void testMath() throws Exception {
		System.out.println(cengine.compile("Math.round(5/2)").eval());
		System.out.println(cengine.compile("Math.floor(5/2)").eval());
		System.out.println(cengine.compile("Math.ceil(5/2)").eval());
		System.out.println(cengine.compile("parseInt(5/2)").eval());
		System.out.println(cengine.compile("32%3").eval());
	}
	
	/**
	 * if 条件 判断
	 * 
	 * @throws ScriptException
	 */
	public void testIf() throws ScriptException {
		String express = "var result = 5.6;\r\n" + 
				"if(a1 > a2) result += 0.7\r\n" + 
				"else if(a1 > a3) result += 0.8\r\n" + 
				"else result += 0.3\r\n" + 
				"\r\n" + 
				"parseInt(result);";
		
		engine.put("a1", 22);
		engine.put("a2", 5);
		engine.put("a3", 2.4);
		System.out.println(cengine.compile(express).eval());

	}
}
