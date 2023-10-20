package winw.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import winw.ai.memory.Memory;
import winw.ai.memory.MemoryRelation;
import winw.ai.model.Model;
import winw.ai.subjective.Action;
import winw.ai.subjective.SubjectiveDeclarativeMemory;

/**
 * 智能主体。包含神经系统的智能主体可以更有效适应环境，改造环境和利用环境。
 * 
 * <Point>
 * 智能体基于神经网络，包含有记忆系统，感知系统，自主反应系统和主观反应系统。
 * 
 * @author winw
 *
 */
public class Agent extends CerebralCortex {

	/**
	 * 管理记忆。
	 */
	protected Hippocampus hippocampus = new Hippocampus();

	/**
	 * 陈述性记忆
	 */
	protected SubjectiveDeclarativeMemory declarativeMemory = new SubjectiveDeclarativeMemory();
	
	/**
	 * 概念记忆，一个模型对应一个概念记忆。经典条件反射。
	 */
	protected Map<Model, Memory> modelMapping = new HashMap<Model, Memory>();

	/**
	 * 序列记忆，一个模型对应一个时序记忆。
	 */
	// protected Map<Model, Memory> seriesMemory = new HashMap<Model, Memory>();


	/**
	 * 主观价值（存放在额叶？），介于-1到1，越接近1则越喜爱，越接近-1则越厌恶。
	 */
	protected Map<Action, Double> subjectiveValue = new HashMap<Action, Double>();


	/**
	 * 事件关联学习。
	 * <Point>
	 * 外显记忆（自传式记忆，自我），3岁之前的记忆基本都会丢失，形成稳定的人格。
	 * 
	 * <Point>
	 * 当一个 Action 完成后，预期和实际不尽相同，而结果会作为一个事件记忆与这个 Action 关联。 以便在下次 执行 Action 时提供
	 * 主观价值评估的参考。
	 * <Point>
	 * 所以，事件关联是强化学习的重要一环。有些事件关联是很容易的，比如被火烧了一次，便可以学会火是危险的，相反，普通人被树上掉落的苹果砸到很难关联到万有引力。
	 * 除非他的大脑正在思考着类似的事情，正好被关联到。这正是大脑的联想功能。
	 * 
	 */
	protected void eventCorrelation() {
		/*
		 * 海马体和前额叶：事件关联学习
		 * 哥伦比亚大学的研究者们用双光子钙离子成像，探索了海马体联系两个关联事件的细胞级别机制。实验利用了一个声音（tone）和一阵air
		 * puff之间的联系学习（associative learning），其间是一个15秒的间隙。
		 * 
		 * 在前额皮质中有着一种类似的机制来维护工作记忆：在需要工作记忆的事件间隙中，前额皮质的细胞会持续放电，以维持对之前发生的事件的表征。因此，
		 * 哥伦比亚大学的研究人员们本来期待找到海马体中类似的机制——然而并没有：他们没有在海马体中发现这15秒间隙内持续放电的细胞/细胞团体。与之相对地，
		 * 研究人员们在这15秒间隙内，在海马体中观察到了看似随机，但其实有特定规律地簇状发放（bursts）。
		 * 长久以来，簇状发放就被认为是能促进神经网络突触重构的重要活动（通常认为通过LTP和LTD机制）。研究人员们认为，
		 * 海马体之所以选择通过规律的簇状发放来维持对前序事件的表征，是因为这样不需要浪费过多能量。相比于前额皮质，海马体的策略利用了突触，而非神经元本身，
		 * 来短暂地储存对前序事件的表征，以便于在15秒间隙后将前序事件与后序事件联系起来。 doi: 10.1016/j.neuron.2020.04.013
		 */

		// 把奖赏和关联的 Action 关联起来，使智能体更有竞争力。这种关联是主观意识内的。

		// FIXME 如何关联？是神经突触的特性、多巴胺、海马等共同作用的结果。并不在主观意识内。

		// 关联学习是智能体学习能力的关键，学习之后形成经典条件反射和操作性条件反射。

		// TODO 每次相邻的两件事都组成新的关联记忆（时序记忆）？是的

		// TODO 关联记忆，需要有意识的介入。
		// TODO 关联记忆如何存储？

		// TODO 大脑中存储的形式类似人类自然语言（区别？），可表述逻辑关系。有主语、动词、介词、形容词等等。

		// TODO Segment
	}

	/**
	 * 奖赏预测误差，RPE。伏隔核内壳和腹侧苍白球一起被认为是大脑奖赏系统的核心。
	 */
	protected void rewardPredictionError() {
		// 价值抉择，经典条件反射，与操作性条件反射，都需要训练。都需要奖赏学习。
	}

	/**
	 * 主观价值评估。
	 * <Point>
	 * 一个Action 的 主观价值，需要多种因素综合研判。纹状体、中脑腹侧被盖区、外侧僵核，以及黒质，都是用来发挥这一作用。
	 * 
	 * <Point>
	 * 一个事物相关的正面价值，经过纹状体反映在 VTA ，相关的负面价值经过纹状体后先通过VP反映到LHb，然后也反映到VTA。 最后VTA得到综合的价值。
	 * 
	 */
	protected void subjectiveValueValuation(Action a) {

		// TODO Model("饥饿") 触发的时候， new Action("吃食物") 的 奖赏 是正向的。

		// FIXME 多巴胺最后又扮演了触发 Action 的组成回路？ 还是多巴胺释放到皮层后促进了关联学习？
		// FIXME 如果预期和实际总是一致，多巴胺将很少释放？

		// TODO 多巴胺系统更多的是用于学习和形成习惯，一旦学习完成，或者习惯形成，便可以潜意识 的 直接执行
		// Action。没有必要每次都占用大量的资源，重复计算。
		// 这就是经典条件反射形成后，可以持续保持。

	}

	/**
	 * 海马，工作记忆。只保持在工作中，
	 */
	protected void hippocampusWorkingMemory() {

	}

	/**
	 * 海马，短时记忆。可以保持几天或几周的时间。
	 * <Point>
	 * 对注意力中发生的事件，按序记录下来。短期保持。
	 */
	protected void hippocampusShortTermMemory() {
		// FIXME 按什么顺序记录。
		// FIXME 听到铃声，然后看到食物。在相近时间发生的事件，记录下来。
	}

	protected void hippocampusLongTermPotentiation() {
		// 海马长时程增强。

		// TODO 根据时序记忆，将两个“同时”发生的事物联系起来。

		// 任意一个记忆被激活时，海马区的内嗅皮质则会被激活。两个相邻的记忆短时间内先后激活后，可生成时序记忆。
	}

	/**
	 * 强化学习，主要是主体Agent根据处境State，做出行为Action，并且最大化奖励Reward的过程。
	 * 
	 * <ul>
	 * <li>1、Agent根据处境State做出Action，并预期一个Reward。
	 * <li>2、Agent的Action影响到环境。
	 * <li>3、Agent识别到环境的Reward。
	 * <li>4、Agent根据预测的Reward和实际的Reward改进对StateA的Action。
	 * </ul>
	 * 
	 * <Point>
	 * 经典非条件反射：是StateX 与另外一个RewardY之间建立联系，并强化学习。
	 * <Point>
	 * 操作性条件反射：是ActionX 与另外一个RewardY之间建立联系，并强化学习。
	 * 
	 * <Point>
	 * 参考： Human-level control through deep reinforcement learning
	 * 
	 */
	protected void reinforcementLearning() {
		// 1. Initial Q
		// 2. Choose Action
		// 3. Perform Action
		// 4. Measure Reward
		// 5. Update Q

		// training

		//
		// Agent
		// 环境奖赏
		//

	}

	protected void init() {

		// 看到食物和流口水是先天的非条件反射。
//		unconditionedReflex.put(new Model("食物"), new Action("流口水"));
//		unconditionedReflex.put(new Model("饥饿"), new Action("吃食物"));
//		unconditionedReflex.put(new Model("饱腹"), new Action(""));
		modelMapping.put(new Model("食物"), new Memory("食物"));

		// 躲避危险

		// 获得奖赏

		// 对新事物好奇

		subjectiveValue.put(new Action("吃食物"), 1D);
	}

	public static void main(String[] args) {

		// 经过训练，形成经典条件反射。

		// 例如巴普洛夫的狗，反复用铃声和食物同时显示给狗，狗则逐渐“学会”在只有铃响但没有食物的情况下分泌唾液。

		// 感知P0 天然会引发反射P0-->A0，经过学习可形成 P1和A0的反射P1-->A0。

		// 学习，需要短时记忆，时序记忆，海马参与。

		// TimeSeriesMemory 时序记忆参与，感知的关联，

		Agent agent = new Agent();

		// 经常把铃声和食物同时显示给狗
		agent.modelMapping.put(new Model("铃声"), new Memory("食物"));

		// 铃声和事物之间的关联，存在奖赏学习，强化学习。

		// 则会使狗在听到铃声时也流口水。
	}
}
