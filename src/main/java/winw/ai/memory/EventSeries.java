package winw.ai.memory;

import java.util.LinkedHashMap;
import java.util.Map;

import winw.ai.EmotionalCenter;

/**
 * 事件序列，或称时序记忆。
 * 
 * <Point>
 * 是强化学习的关键。将主观认知的事件，记录为时序记忆。
 * 
 * <Point>
 * 经典条件反射：每次听到铃声，然后看到食物；操作性条件反射：按压水龙头，喝到水；
 * 
 * @author winw
 *
 */
public class EventSeries {

	private Memory memory;// 序列中的某个记忆，便于检索

	// <时间特征（通过频率、定时等方式形成时间刻度），记忆内容>
	private Map<Long, Memory> series = new LinkedHashMap<Long, Memory>();

	public EventSeries() {
		super();
	}

	public EventSeries(Memory... events) {
		super();
		addEvents(events);
	}

	public EventSeries(Memory memory, Map<Long, Memory> series) {
		super();
		this.memory = memory;
		this.series = series;
	}

	public void addEvents(Memory... events) {
		for (int i = 0; i < events.length; i++) {
			series.put(Long.valueOf(i), events[i]);
		}
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public Map<Long, Memory> getSeries() {
		return series;
	}

	public void setSeries(Map<Long, Memory> series) {
		this.series = series;
	}

	public static void main(String[] args) {
		// 1、形成了时序记忆
		EventSeries eventSeries = new EventSeries(Memory.of("铃声"), Memory.of("食物"));
		// 2、遇到同样场景，根据线索回忆到之前的时序记忆。预期到结果，并释放多巴胺（用于强化学习）；
		// 听到铃声，触发时序记忆
		eventSeries.toString();

		// 3、行动后的结果，与之前时序记忆中的奖赏是否一致，根据奖赏误差调整这个时序记忆中事件的关系可靠性。

		// 强化学习，产生了新的关联记忆。形成知识。

		Memory.of("铃声").setRelation(Memory.of("食物"), 0.5);

		// TODO 预期奖赏，预期有0.5，经过综合评估，影响情绪和动机，情绪愉悦，强化学习
		// TODO 奖赏误差纠正，与预期不同，也会影响情绪

		// TODO 情绪系统、动机系统
		// 达到预期，情绪愉悦，强化学习
		
		new EmotionalCenter();

		// 上面的过程是强化学习的必要过程，如果记忆没有形成，或者回忆不起来，都不会产生强化学习。
		// 带有奖赏或者惩罚的记忆会特别深刻：吃到特别的食物，犯错后挨揍的记忆，终身难忘。

		// TODO 怎么演示？
	}

}
