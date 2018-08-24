package winw.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * 公平选择器。
 * 
 * <p>
 * 类似背包问题。从若干个组中选择不超过max的数量，尽可能的保持公平（使每个组选择的数量相同，除非该组已被全部选择）。
 * 
 * @author sjyao
 *
 */
public class FairSelector {

	/**
	 * 从若干个组中选择不超过max的数量，尽可能的保持公平（使每个组选择的数量相同，除非该组已被全部选择）。
	 * 
	 * @param groups
	 *            Map的Entity中的key放Group对象，value放当前组的值。
	 * @param maxChoose
	 *            最多选择的数量。
	 * @return Map的Entity中的key放Group对象，value放当前组的值。
	 */
	public static <T extends Number> Map<T, Integer> choose(Map<T, Integer> groups, int maxChoose) {
		Map<T, Integer> result = new TreeMap<T, Integer>();

		double remainTotal = maxChoose;// 剩余可选总数
		List<Entry<T, Integer>> sortedList = sortEntryValues(groups.entrySet());// 按照 Value 从小到大排序
		for (int i = 0, t = sortedList.size(); i < sortedList.size(); i++) {
			// 取当前值或者剩余组的可选平均数的ceiling值的最小的一个
			int choose = Math.min(sortedList.get(i).getValue(), (int) Math.ceil(remainTotal / (t - i)));
			if (choose > 0) {
				remainTotal -= choose;
				result.put(sortedList.get(i).getKey(), choose);
			}
		}
		return result;
	}

	/**
	 * 按照entrySet中的value排序
	 * 
	 * @param entrySet
	 * @return
	 */
	public static <T extends Number> List<Entry<T, Integer>> sortEntryValues(Set<Map.Entry<T, Integer>> entrySet) {
		List<Entry<T, Integer>> sortedList = new ArrayList<Entry<T, Integer>>(entrySet);
		Collections.sort(sortedList, new Comparator<Map.Entry<?, Integer>>() {
			public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		return sortedList;
	}

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int maxChoose = 1000;
		// 普通测试
		HashMap<Number, Integer> tasks = new HashMap<Number, Integer>();
		tasks.put(5, 1);
		tasks.put(6, 0);
		tasks.put(7, 333);
		System.out.println(tasks);
		assertChooseResult(choose(tasks, maxChoose), 334);

		tasks.put(1, 200);
		tasks.put(2, 1700);
		tasks.put(3, 300);
		tasks.put(4, 10);
		tasks.put(5, 1);
		tasks.put(6, 0);
		tasks.put(7, 333);
		System.out.println(tasks);
		assertChooseResult(choose(tasks, maxChoose), maxChoose);

		// 当每个水桶的水位都超过最大选择量。
		for (Number key : tasks.keySet()) {
			tasks.put(key, maxChoose + key.intValue());
		}
		System.out.println(tasks);
		assertChooseResult(choose(tasks, maxChoose), maxChoose);

		// 当水桶超过1000个
		tasks.clear();
		for (int i = 0; i < 1024; i++) {
			tasks.put(i, i);
		}
		System.out.println(tasks);
		assertChooseResult(choose(tasks, maxChoose), maxChoose);
	}

	private static void assertChooseResult(Map<Number, Integer> choose, int total) {
		System.out.println("choose: " + choose);
		int choosed = 0;
		for (Integer key : choose.values()) {
			choosed += key;
		}
		if (choosed != total) {
			System.err.println("choosed != total, choosed: " + choosed);
		}
	}
}
