package winw.ai.subjective;

import java.util.ArrayList;
import java.util.List;

import winw.ai.Hippocampus;
import winw.ai.memory.EngramSeries;
import winw.ai.memory.EventSeries;

/**
 * 主观陈述性记忆（自传体记忆）。
 * 
 * <Point>
 * 包括情景记忆、时序记忆。
 * 
 * <Point>
 * 陈述性记忆是高级认知、强化学习的基础。可以经过强化学习，变为程序性记忆。
 * 
 * @author winw
 *
 */
public class SubjectiveDeclarativeMemory {

	/**
	 * 管理记忆。
	 */
	protected Hippocampus hippocampus = new Hippocampus();

	/**
	 * 时序记忆，一段语音。鸟类擅长鸣唱。
	 */
	protected List<EngramSeries> engramSeriesMemory = new ArrayList<EngramSeries>();

	/**
	 * 事件序列记忆。
	 */
	protected List<EventSeries> eventSeriesMemory = new ArrayList<EventSeries>();

	/**
	 * 由注意力，把事件记录下来。
	 */
	public void eventMemory(EventSeries event) {// Past Events, Current Event, Future Events
		// TODO 将认知记录为 Event，比如听到铃声、看到食物
		// TODO 将当前事件与之前的事件关联起来。注意力没有离开

		// TODO 如何避免将无关的事情关联起来。注意力分割
	}
	// 按时间给各个记忆关联起来。

	// 时序记忆。

	// 所有认知的事物，按时间顺序记录。

	// 将记忆碎片联系起来。有海马体参与。

	// 不一定需要Action
}
