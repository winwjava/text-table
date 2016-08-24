package winw.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 导出进度。
 * 
 * @author sjyao
 *
 */
public class ExportProgress {
	// 全局缓存，每个SESSION对应一个ExportProgress。
	private static ConcurrentMap<String, ExportProgress> SESSION_EXPORT_PROGRESS = new ConcurrentHashMap<String, ExportProgress>();

	public static ExportProgress get(String sessionId) {
		ExportProgress result = SESSION_EXPORT_PROGRESS.get(sessionId);
		result.setLastRequestTime();
		result.calcExportPercent();
		return result;
	}

	public static ExportProgress add(String sessionId, String filename) {
		ExportProgress exportProgress = new ExportProgress();
		exportProgress.setFilename(filename);
		SESSION_EXPORT_PROGRESS.put(sessionId, exportProgress);
		return exportProgress;
	}

	private ExportProgress() {
		super();// TODO asyncExport
	}

	private NumberFormat numberFormat = new DecimalFormat("##0.00%");

	public void addExportCount(int exportCount) {
		this.exportCount += exportCount;
	}

	public boolean exportCompleted() {
		return exportCount == exportTotal;
	}

	private void calcExportPercent() {
		// 导出完成，或还没有开始导出
		if (exportCompleted || exportTotal == 0) {
			return;
		}

		double nowTime = System.currentTimeMillis();
		if (nowTime - lastRequestTime > 10000) {
			// 如果超过10秒钟，客户端没有REQUEST，则表示客户端取消导出。抛出异常，停止导出。
			throw new RuntimeException("Client Abort This Export!");
		}

		if (exportCount == exportTotal) {// 写文件完成，正在压缩
			if (endTime == -1) {
				endTime = System.currentTimeMillis();
			}

			// 由于XLSX写文件完成之后需要压缩，所以将压缩的时间计算在内。
			double adds = (nowTime - endTime) / (nowTime - startTime);
			if (adds > 0.0499) {
				adds = 0.0499;
			}
			exportPercent = numberFormat.format(0.95d * exportCount / exportTotal + adds);
		} else if (exportTotal != -1) {// 正在写文件
			exportPercent = numberFormat.format(0.95d * exportCount / exportTotal);
		}
	}

	/**
	 * 文件名称。
	 */
	private String filename;

	/**
	 * 目前导出的数量。
	 */
	private int exportCount;

	/**
	 * 导出总数。
	 */
	private int exportTotal = -1;

	/**
	 * 导出完成的百分比。
	 */
	private String exportPercent = "0.00%";

	private boolean exportCompleted;

	private long startTime = System.currentTimeMillis();

	private long endTime = -1L;

	private long lastRequestTime = System.currentTimeMillis();

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getExportCount() {
		return exportCount;
	}

	public void setExportCount(int exportCount) {
		this.exportCount = exportCount;
	}

	public int getExportTotal() {
		return exportTotal;
	}

	public void setExportTotal(int exportTotal) {
		this.exportTotal = exportTotal;
	}

	public String getExportPercent() {
		return exportPercent;
	}

	public void setExportPercent(String exportPercent) {
		this.exportPercent = exportPercent;
	}

	public boolean isExportCompleted() {
		return exportCompleted;
	}

	public void setExportCompleted(boolean exportCompleted) {
		this.exportCompleted = exportCompleted;
		if (exportCompleted) {
			this.exportPercent = "100.00%";
		}
	}

	public long getLastRequestTime() {
		return lastRequestTime;
	}

	public void setLastRequestTime() {
		this.lastRequestTime = System.currentTimeMillis();
	}

	public void setLastRequestTime(long lastRequestTime) {
		this.lastRequestTime = lastRequestTime;
	}

	// TEST
	// public static void main(String[] args) throws InterruptedException {
	// String sessionId = "TEST@346T445623V";
	// ExportProgress exportProgress = ExportProgress.add(sessionId, "TEST.xlsx");
	//
	// // export
	// exportProgress.setExportTotal(100);
	// for (int i = 0; i < 100; i++) {
	// exportProgress.addExportCount(1);
	// Thread.sleep(100);
	// exportProgress.setLastRequestTime();
	// exportProgress.calcExportPercent();
	// System.out.println(exportProgress.getExportPercent());
	// }
	//
	// for (int i = 0; i < 10; i++) {
	// Thread.sleep(100);
	// exportProgress.setLastRequestTime();
	// exportProgress.calcExportPercent();
	// System.out.println(exportProgress.getExportPercent());
	// }
	// exportProgress.setExportCompleted(true);
	// System.out.println(exportProgress.getExportPercent());
	// }
}
