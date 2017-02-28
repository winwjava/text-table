package winw.util;

import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class FlowControllerTest {
	// protected static final Logger logger =
	// LoggerFactory.getLogger(FlowControllerTest.class);

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

	FlowController flowController = new FlowController(64, 2000, TimeUnit.MICROSECONDS);

	// 窗口大小 = 64
	BlockingQueue<Long> queue = new LinkedBlockingQueue<Long>(64);// DelayQueue

	@Test
	public void test() throws InterruptedException {

		// 发送线程，模拟客户端
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					send();
				} catch (InterruptedException e) {
				}
			}

		}, 0, 2000, TimeUnit.MICROSECONDS);

		final AtomicInteger takeCounter = new AtomicInteger();
		// 接收线程，模拟服务端
		scheduler.execute(new Runnable() {

			@Override
			public void run() {
				try {
					receive(queue, takeCounter);
				} catch (InterruptedException e) {
				}
			}

		});

		new Timer("dashboard", true).scheduleAtFixedRate(new TimerTask() {

			int lastTakeNum = 0;

			public void run() {
				int currentTakeNum = takeCounter.get();
				System.out.println("take: " + (currentTakeNum - lastTakeNum) + ".");
				// logger.info("take: {}.", (currentTakeNum - lastTakeNum));
				lastTakeNum = currentTakeNum;
			}
		}, new Date(), 1000);
		Thread.sleep(Long.MAX_VALUE);
	}

	Random random = new Random();
	static int count = 0;

	private void send() throws InterruptedException {
		if (flowController.overSpeed()) {
			// logger.warn("over speed, skiped.");
			return;
		} else {
			// logger.info("send......");
		}

		queue.put(1L);
		// TODO 模拟一个延迟
		// int nextInt = random.nextInt(10);
		// int nextInt = 4;
		count++;

		if (count % 200 == 0) {
			// logger.info("****......");
			Thread.sleep(90);
		}
		// else{
		//// if (nextInt < 4) {
		// Thread.sleep(nextInt);
		// }
	}

	private LinkedList<Long> recentReceiveList = new LinkedList<Long>();

	private void receive(BlockingQueue<Long> queue, final AtomicInteger takeCounter) throws InterruptedException {
		for (;;) {
			queue.take();
			takeCounter.incrementAndGet();
			// 记录1秒内接收的总数
			long currentTimeMillis = System.currentTimeMillis();

			recentReceiveList.addLast(System.currentTimeMillis());
			// 移除1秒以前的记录
			while (currentTimeMillis - recentReceiveList.getFirst() >= 1000) {
				recentReceiveList.removeFirst();
			}
			// 如果超速，打印日志。
			if (recentReceiveList.size() > 500) {
				// logger.warn("over speed: {}.", recentReceiveList.size());
				System.out.println("WARN! Over speed: " + recentReceiveList.size());
			}

		}
	}
}
