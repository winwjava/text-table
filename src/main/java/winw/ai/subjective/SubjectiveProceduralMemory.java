package winw.ai.subjective;

import java.util.HashMap;
import java.util.Map;

import winw.ai.memory.Memory;
import winw.ai.model.Model;

/**
 * 主观程序性记忆。
 * 
 * <Point>
 * 强化学习，对运动的学习，前馈学习和反馈学习。
 * 
 * @author winw
 *
 */
public class SubjectiveProceduralMemory {

	/**
	 * 非条件反射
	 */
	protected Map<Model, Action> unconditionedReflex = new HashMap<Model, Action>();

	/**
	 * 经典条件反射
	 */
	protected Map<Memory, Action> classicalConditioning = new HashMap<Memory, Action>();

	/**
	 * 操作性条件反射。主观反应记忆。
	 */
	protected Map<Action, Memory> operantConditioning = new HashMap<Action, Memory>();
	
	
	// 非经典条件反射，操作性条件反射

	// 有 Action，有 Reward，学到新的模型作为 Reward

	// 在真实世界中寻求 Reward？
	


	public static void main(String[] args) {
		// 强化学习测试

		// 1. Initial Q
		// 2. Choose Action
		// 3. Perform Action
		// 4. Measure Reward
		// 5. Update Q

		// training

		// 完成一次开门操作。
		
		Action action = new Action("L");
		Action actionR = new Action("R");
		Action actionT = new Action("T");
		Action actionB = new Action("B");
		
		// Action 有 上下左右，Reward 有 门开了，门关了。
		
		new Reward("门开了");
		
	}
}
