package winw.ai.memory;

/**
 * 记忆之间的关联关系：指向、相同、相反，或者其他复杂逻辑关系：
 * 
 * @author winw
 *
 */
public class MemoryRelation {

	private Memory memory;

	/**
	 * 感知（刺激）和动作的连接灵敏程度。0是无感知，1为100%感知。
	 * 
	 * <p>
	 * 当奖赏为正，或可避免惩罚则敏感度提高，否则下降。
	 * 
	 */
	private double sensitive = 0;

	/**
	 * 1 指向（触发），-1 反向
	 */
	private int relation = 1;

	public double getSensitive() {
		return sensitive;
	}

	public void setSensitive(double sensitive) {
		this.sensitive = sensitive;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public int getRelation() {
		return relation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

}
