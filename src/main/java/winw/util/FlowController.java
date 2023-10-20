package winw.util;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 流速控制器。
 * 
 * <Point>
 * 控制低于某个速度运行，如果检测到超前发送，则应当取消本次发送。
 * 
 * <pre>
 * 使用滑动列表（slidingList）实现，slidingList记录最近固定长度（slidingLength）没有超速的发送时间。
 * 如果 currentTime - slidingList.getFirst() < 滑动列表所占的合理时间范围，则认为超速。
 * </pre>
 * 
 * <Point>
 * <i>假设网关一直是全速均匀发送，突然有一条提前到达运营商，那么这一条还是超速的。
 * 要保证不超速，则应当把速度降低到window_size同时到达运营商也不会超速。</i>
 * 
 * @author sjyao
 *
 */
public class FlowController {

	/**
	 * 滑动列表（slidingList）的长度。
	 */
	private int slidingLength;
	/**
	 * 滑动列表所占的合理时间范围。
	 */
	private long slidingDuration;

	/**
	 * 滑动列表，记录最近的若干次发送的时间。
	 */
	private LinkedList<Long> slidingList = new LinkedList<Long>();

	/**
	 * 构建一个长度为slidingLength的slidingList，slidingDuration（滑动列表所占的合理时间范围）用slidingLength
	 * * interval。
	 * 
	 * @param slidingLength
	 *            滑动列表的长度。
	 * @param interval
	 *            发送间隔时间。
	 * @param timeUnit
	 *            interval的时间单位
	 */
	public FlowController(int slidingLength, long interval, TimeUnit timeUnit) {
		// 分 10个窗口，9个安全窗口
		int windowTotal = 10;

		// 以纳秒为单位（1秒=1000毫秒，1毫秒=1000微秒，1微秒=1000纳秒）
		int second = 1000 * 1000 * 1000;

		long tps = second / timeUnit.toNanos(interval);

		this.slidingLength = (int) (tps / windowTotal);

		// 安全窗口比最大窗口数小1
		slidingDuration = second / (windowTotal - 1);
	}

	/**
	 * 返回是否超速。
	 * 
	 * <Point>
	 * 如果检测到超前发送，则应当取消本次发送。
	 * 
	 * <Point>
	 * 返回true表示超速，但不会记入slidingList。返回false表示没有超速并记入slidingList。
	 * 
	 * @return 是否超速。
	 */
	public boolean overSpeed() {
		long nanoTime = System.nanoTime();
		// 还没有达到足够的记录。
		if (slidingList.size() < slidingLength) {
			// timeUnit
			slidingList.addLast(nanoTime);
			return false;
		}

		// 如果currentTime - slidingList.getFirst() < 滑动列表所占的合理时间范围，则认为超速。
		if (nanoTime - slidingList.getFirst() < slidingDuration) {
			return true;
		}

		slidingList.removeFirst();
		// 将时间尽可能的往前推移，避免损失发送间隙。

		// 小于Δt，则使用当前时间
		if (nanoTime - slidingList.getLast() < slidingDuration / slidingLength) {
			slidingList.addLast(nanoTime);
		} else {// 补偿算法：否则使用Last + Δt 或 向前一个slidingDuration
			slidingList.addLast(
					Long.max(nanoTime - slidingDuration, slidingList.getLast() + slidingDuration / slidingLength));
		}

		return false;
	}

}
