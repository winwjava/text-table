package winw.ai.memory;

import java.util.List;

public class Memory {

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
	private List<MemoryRelation<Memory>> relMemory;

	public List<MemoryRelation<Memory>> getRelMemory() {
		return relMemory;
	}

	public void setRelMemory(List<MemoryRelation<Memory>> relMemory) {
		this.relMemory = relMemory;
	}
	
	// 通过卷积神经网络，可以激活一个记忆。
}
