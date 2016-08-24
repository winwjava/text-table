package winw.utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Checkpoint, Used to limit speed.
 * 
 * @see Semaphore
 * 
 * @author sjyao
 *
 */
public class Checkpoint {

	private volatile long intervalMillis = 1000;

	private volatile int limit = 1000;

	private AtomicInteger counter = new AtomicInteger();

	private volatile long lastCheck = System.currentTimeMillis();

	private Lock lock = null;

	private Condition condition = null;

	public Checkpoint() {
		super();
		this.lock = new ReentrantLock();
	}

	public Checkpoint(long interval, int limit, boolean fair) {
		super();
		this.intervalMillis = interval;
		this.limit = limit;
		this.lock = new ReentrantLock(fair);
		this.condition = lock.newCondition();
	}

	/**
	 * acquire
	 * 
	 * @param limit
	 * @param num
	 * @return available
	 * @throws InterruptedException
	 */
	public int acquire(int num) throws InterruptedException {
		if (num <= 0 || num > limit) {
			throw new IllegalArgumentException("Illegal acquire: " + num + ", less than 1 or greater than " + limit);
		}
		// 如果计数器没有达到限制速度
		if (counter.get() < limit) {
			// 计数器增加
			return limit - counter.addAndGet(num);
		}

		lock.lock();
		try {
			// 等待到下一个放行时间
			condition.await(lastCheck + intervalMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

			// 增加判断是为了在await结束后都会向下运行，但只能重置一次
			if (System.currentTimeMillis() - lastCheck >= intervalMillis) {
				// 计数器归零
				counter.set(0);
				lastCheck = System.currentTimeMillis();
			}
			// 进入新的检查点
			return acquire(num);
		} finally {
			lock.unlock();
		}
	}

	public long getIntervalMillis() {
		return intervalMillis;
	}

	public void setIntervalMillis(long intervalMillis) {
		this.intervalMillis = intervalMillis;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	// public static void main(String[] args) throws InterruptedException {
	// Checkpoint checkpoint = new Checkpoint();
	// Runnable r = () -> {
	// for (int i = 0; i < 100; i++) {
	// try {
	// checkpoint.acquire(1000, 100);
	// System.out.println(new Date().toString() + " - " +
	// Thread.currentThread().getName() + " - 100");
	// } catch (InterruptedException e) {
	// }
	// }
	// };
	//
	// for (int i = 0; i < 10; i++) {
	// Thread t = new Thread(r);
	// t.setDaemon(true);
	// t.start();
	// }
	// Thread.sleep(10000);
	// }
}
