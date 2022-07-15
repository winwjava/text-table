package winw.ai.memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memory {

	// 只用于测试，应该用 Model 映射
	private static Map<String, Memory> memoryMap = new HashMap<String, Memory>();

	// 只用于测试，应该用 Model 映射
	public static Memory of(String name) {
		if (!memoryMap.containsKey(name)) {
			memoryMap.put(name, new Memory(name));
		}
		return memoryMap.get(name);
	}

	private String name;

	public Memory(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 与其他记忆的关系。
	 */
	private List<MemoryRelation> relMemory;

	public List<MemoryRelation> getRelMemory() {
		return relMemory;
	}

	public void setRelMemory(List<MemoryRelation> relMemory) {
		this.relMemory = relMemory;
	}

	/**
	 * 关联关系，以及关系可靠性
	 */
	private Map<Memory, Double> relations = new HashMap<Memory, Double>();

	public void setRelation(Memory memory, double sensitive) {
		relations.put(memory, sensitive);
	}

	public Double getRelation(Memory memory) {
		return relations.get(memory);
	}

	// 通过卷积神经网络，可以激活一个记忆。
}
