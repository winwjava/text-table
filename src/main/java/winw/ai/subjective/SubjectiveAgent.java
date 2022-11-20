package winw.ai.subjective;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import winw.ai.Agent;
import winw.ai.memory.Memory;
import winw.ai.memory.MemoryRelation;
import winw.ai.model.Model;

/**
 * 主观行动。
 * 
 * <p>
 * 主观能动性。
 * 
 * @author winw
 *
 */
public class SubjectiveAgent extends Agent{

	/**
	 * 概念记忆，一个模型对应一个概念记忆。经典条件反射。
	 */
	protected Map<Model, Memory> modelMapping = new HashMap<Model, Memory>();
	
	/**
	 * 瞬时记忆与活跃值。由海马体维持，如果短时间没有持续激活，则会减弱，甚至消失。
	 * <p>
	 * 活跃值 介于0和1，数字越大越活跃。
	 */
	protected Map<Memory, Double> momentMemory = new HashMap<Memory, Double>();
	/**
	 * 注意力和工作记忆（纹状体和前额叶），专门处理当前的瞬时记忆。
	 * <p>
	 * 主观反应系统。包含价值抉择。逻辑推演。动机。
	 * 
	 * <p>
	 * 主观价值评估。
	 * <p>
	 * 思考和决策。
	 * <p>
	 * <p>
	 * 当处于危险、新奇场景，需要提高注意力，对感官输入更敏锐。
	 * 
	 * <p>
	 * 注意力缺陷多动症(ADHD)，主要特征是不专注、过动和冲动。过去我们认为ADHD与脑部前额叶相关。然而，事实上，大部分受ADHD困扰的人是由于小脑没有适当地发挥功能。在过去的20年中的研究发现，小脑发育不良的人同时有ADHD的症状。藉由功能性扫描也发现ADHD患者的小脑活跃度很低。
	 */
	protected void attention() {// TODO 还有潜意识，潜意识可以不占用注意力资源。
		// 从 momentMemory 中取 活跃度 最高的一个。
		double active = 0;
		Memory attention = null;
		for (Memory memory : momentMemory.keySet()) {// 有 高价值的 Action 应当获得更多 注意力。
			if (momentMemory.get(memory) > active) {
				attention = memory;
			}
		}

		System.out.println("脑海中闪过，意识到：" + attention.getName());

		// 由基底核中的伏隔核等判断。

		// 注意力是否停留在当前事物上。

		// TODO 饥饿时对食物会更加有注意力

		// 将 RelMemory 放进 瞬时记忆，关联思考能力
		List<MemoryRelation> relMemory = attention.getRelMemory();
		if (relMemory != null && relMemory.size() > 0) {
			// 随机取一个作为瞬时记忆。
			momentMemory.put(relMemory.get(0).getMemory(), 0.5);
		}

		// TODO 如果看到食物，并且很饥饿，则会很倾向于吃食物（价值抉择）

		// TODO “倾向吃食物” 与 “吃食物” 是 不同的，后者是Action，前者是什么？

		// TODO 对食物的倾向怎么表达？与奖赏或惩罚的关系？

//		Action action = classicalConditioning.get(attention);

		// 主观价值评估。
//		subjectiveValueValuation(action);// 评估的结果会返回到纹状体（前额的工作记忆？），以便与其他 Action 比较。

		// 主观价值抉择。（当有多种 Action 选择的场景时，需要分别计算每个 Action 的主观价值，以便选择一个主观最优的 Action）

		// 当主观价值抉择时，主观价值评估是并行的。

	}

	/**
	 * 潜意识，不需要注意力参与。所有养成的行动习惯。
	 * <p>
	 * 又称内隐记忆。程序性记忆。
	 * 
	 * <p>
	 * 程序性记忆要比自传式记忆简单的多，波士顿动力的人行机器人是一个很好的证明。
	 * <p>
	 * 可以一边吃饭，一边看电视。但是不能一边听课一边玩游戏。
	 */
	protected void subconscious() {
		// 大部分行动，不需要主观价值参与。只需要运动皮层的程序性记忆即可。
	}

	protected boolean sleep = true;

	protected ExecutorService executorService = Executors.newFixedThreadPool(5);

	/**
	 * 唤醒，超过阈值的感知会自动唤醒。
	 */
	protected void resume() {
		if (sleep) {
			executorService.execute(() -> {
				attention();// 注意力
			});

			// 记忆处理
			executorService.execute(() -> {
				hippocampusWorkingMemory();
				hippocampusShortTermMemory();
				hippocampusLongTermPotentiation();
			});

			executorService.execute(() -> {
				eventCorrelation();// 事件关联
			});
		}

		sleep = false;
	}

	/**
	 * 睡眠
	 */
	protected void sleep() {

	}

}
