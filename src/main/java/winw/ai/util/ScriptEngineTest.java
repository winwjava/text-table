package winw.ai.util;

import java.io.FileReader;
import javax.script.Invocable;

import javax.script.ScriptEngine;

import javax.script.ScriptEngineManager;


public class ScriptEngineTest {

	public static void main(String[] args) throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");

		String jsFileName = "E:\\expression.js"; // 读取js文件
		// function merge(a, b) {  return a * b; }
		
		FileReader reader = new FileReader(jsFileName); // 执行指定脚本
		engine.eval(reader);

		if (engine instanceof Invocable) {
			Invocable invoke = (Invocable) engine;
			System.out.println(invoke.invokeFunction("merge", 2, 3));
		}

		reader.close();

	}
}