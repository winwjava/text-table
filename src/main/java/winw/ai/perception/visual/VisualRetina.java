package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

/**
 * 视网膜。
 */
public class VisualRetina extends RetinaReceptiveField {

	static Random RANDOM = new Random();

	// 总共640 × 480 像素，72度，每度大约6.6-8.8个像素，半径是3-5个像素。但每度像素太少也计算不出结果。
	public static int radius = 3;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	public static void main(String[] args) throws IOException {// 空间密度，与空间频率
		long t0 = System.currentTimeMillis();
		BufferedImage image = ImageIO.read(new File("D:/file/data/leuvenA.jpg"));// "D:/file/data/leuvenB.jpg"
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		VisualRetina.retinaReceptiveField(result, image, radius);
		ImageIO.write(result, "jpg", new File("D:/file/temp/leuvenA-retina.jpg"));// 05-retina.jpg

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Retina, cost: " + (t1 - t0) + "ms.");
	}

	/**
	 * 视网膜感受野，计算出当前感受野的对比度、空间频率、颜色
	 * 
	 * @return
	 */
	public static ReceptiveField[][] retinaReceptiveField(BufferedImage result, BufferedImage image, int radius) {
		ReceptiveField[][] fieldImage = new ReceptiveField[image.getWidth()][image.getHeight()];
		// 先灰度处理（将颜色转换为亮度）
		int[][] grayImage = VisualRetina.brightnessReceptiveField(image, radius);// 灰度处理，边缘增强，返回二值化二维数组，存储亮度0~255

		// 颜色功能柱，使每个颜色都对应到上面。作用是什么？
		// V1有无数个颜色柱，每个感受野一个颜色柱

		// 将相同颜色的Blob，怎么聚到一起？
		// 假设从V4向下看，肯定是V4中有一个形状的颜色，在V2和V1中都被激活了。

		// 要考虑颜色渐变。相邻的合并？

		// 复杂细胞的感受野，起到整合多个简单细胞感受野的作用？（整合野的概念不一定对）

		// 某一个功能柱内的神经元（这些神经元具有相似的功能特异性，例如偏好某一颜色、朝向、运动方向、深度信息等）倾向于与其他具有相同功能特性的功能柱产生连接。
		// V1 斑块区，整合为 V2区的细条纹区 stripes

		// V1区的标志则是色觉细胞形成的斑块。V1区4Cβ层的信号传至第2、3层后，斑块区的信号外送至V2区的细条纹区，斑块间区的信号外送至V2区的浅条纹区，V1区4B和4Cα层大细胞的信号则传至V2区的粗条纹区。细条纹区与色觉相关，浅条纹区与形状的感知(形觉)有关，粗条纹区与运动视觉和立体视觉有关。从V2区发出的信号再分别传送至其他脑区进行更多更复杂的处理，其中细条纹区和浅条纹区与V4区有联结，粗条纹区与V5区有联结。
		// https://www.zhihu.com/question/406919670/answer/1410089852

		// V1斑块区的信号外送至V2区的细条纹区，斑块间区的信号外送至V2区的浅条纹区，细条纹区与色觉相关，浅条纹区与形状的感知(形觉)有关，细条纹区和浅条纹区与V4区有联结。
		// 斑块区是颜色，斑块间区的信号，是形状信息，

		// http://www.ziint.zju.edu.cn/index.php/event/cdetails.html?tid=467&sid=0
		// 视皮层等级的提高，其感受野尺寸越来越大

		// 两种方案：用更大的感受野，或者相互连接，根据角检测器的模型，应该是更大的感受野。
		// 用多大的感受野合适？
		// V1用度，总共100度×70度，V2按十分之一，10个感受野整合为1个感受野？如果很多小的物体怎么办？

		// 柱状结构微环路，这些环路可以整合局部区域内（如V2内部）以及区域间（V2和V1之间）信息。它们普遍存在于处理颜色和朝向信息的网络中。
		// 这些微环路会把整个视野连在一起？还是部分？应该是全部，分为V2V3，是因为需要边缘分割。

		// 颜色功能柱：每个颜色与相应感受野匹配。
		Map<Integer, List<ReceptiveField>> colorColumn = new HashMap<Integer, List<ReceptiveField>>();// 如果一个区域混合了多种颜色

		for (int j = radius; j <= grayImage.length - radius - 1; j += radius) {
			for (int k = radius; k <= grayImage[0].length - radius - 1; k += radius) {
				// J,K 是从(radius+1,radius+1)开始的
				ReceptiveField field = new ReceptiveField();
				field.setX(j);
				field.setY(k);
				fieldImage[j][k] = field;
				int gray = grayImage[j][k];// 中心

				// 计算对比度，对比度高时，将RANGE调大，否则调小。
				int minBrightness = gray;// (rgb >> 16) & 0xFF; // 取红色分量作为亮度
				int maxBrightness = gray;

				int pixel = 0;
				// 计算视野中的对比度。
				for (int m = j - radius; m < j + radius; m++) {
					for (int n = k - radius; n < k + radius; n++) {
						pixel++;
						minBrightness = Math.min(minBrightness, grayImage[m][n]);
						maxBrightness = Math.max(maxBrightness, grayImage[m][n]);
					}
				}
				field.setPixel(pixel);
				// 对比度
				field.setContrast(contrastRatio(minBrightness, maxBrightness));

				// 人眼的对比度范围是相对的，并且受到环境亮度和适应性的影响。在高亮度环境下，人眼的对比度感知范围较窄，而在低亮度环境下，对比度感知范围则相对较宽。
				double contrastThreshold = 0.03;
				if (field.getContrast() > contrastThreshold) {// 有一定对比度
					// 空间频率
					field.setFrequency(spatialFrequency(grayImage, j, k, radius, contrastThreshold));
					field.setOrientation(0);

					if (field.getFrequency() > 0.25) {// 中高空间频率，可能是边缘

						// 统计颜色特征。
						TreeMap<Integer, Integer> colorRatio = new TreeMap<Integer, Integer>();
						// TODO 并且没有边缘，要判断是否有边缘。

						for (int m = j - radius; m < j + radius; m++) {
							for (int n = k - radius; n < k + radius; n++) {
//								result.setRGB(m, n, image.getRGB(j, k));
								int rgb = image.getRGB(j, k);
								int hue = (int) getHue(rgb);// 360的色相，只取36个颜色
								colorRatio.put(hue, colorRatio.getOrDefault(hue, 0) + 1);
							}
						}
						field.setColorStats(colorRatio);
						field.setColor(field.getColorStats().firstKey());
						// FIXME 首选颜色，应该单独设置功能柱
						colorColumn.getOrDefault(field.getColorStats().firstKey(), new ArrayList<ReceptiveField>())
								.add(field);

						for (int m = j - radius; m < j + radius; m++) {
							for (int n = k - radius; n < k + radius; n++) {

//								System.out.println(j + "," + k + ", SF: " + sf);
//								if (sf >= 3F) {// V1偏好1.5到4.25左右，特别是3附近
//								if (sf >= 1F) {// V2偏好0.5到3.5，特别是2附近
////								if (sf >= 0.25F && sf <= 0.5F) {// V4偏好0到2.5，特别是0.25附近
//									for (int m = x0 - radius; m < x0 + radius; m++) {
//										for (int n = y0 - radius; n < y0 + radius; n++) {
//											result.setRGB(m, n, 0xFFFFFF);
//										}
//									}
//								}

//								if (field.getFrequency() == 0.25) {// 边缘， 斑块间隙（interblobs，组成条间纹 inter stripe）
//									result.setRGB(m, n, 0xFFFFFF);// 白色
//								} else 
								if (field.getFrequency() <= 0.5) {// V4偏好低空间频率，0到2.5，特别是0.25附近，可能是边缘
//									result.setRGB(m, n, 0x00FF00);// 绿色
//									result.setRGB(m, n, field.getColorStats().firstKey());
								} else if (field.getFrequency() > 0.5 && field.getFrequency() < 3) {// V2偏好0.5到3.5，特别是2附近，可能是条纹、纹理
//									result.setRGB(m, n, 0x0000FF);// 蓝色
//									result.setRGB(m, n, field.getColorStats().firstKey());
								} else if (field.getFrequency() >= 3) {// V1偏好高空间频率，1.5到4.25左右，特别是3附近，属于边缘。
//									result.setRGB(m, n, 0xFF0000);// 红色
//									result.setRGB(m, n, field.getColorStats().firstKey());
								}

								// V1区blob和inter-blob有不同的最优SF
//									result.setRGB(m, n, 0x000000);// 黑色
							}
						}

					} else {// 低空间频率，可能是条纹、纹理，
						field.setColor(image.getRGB(j, k));
						colorColumn.getOrDefault(image.getRGB(j, k), new ArrayList<ReceptiveField>()).add(field);
					}
				} else {// 低对比度，纯颜色斑块
					field.setColor(image.getRGB(j, k));
					colorColumn.getOrDefault(image.getRGB(j, k), new ArrayList<ReceptiveField>()).add(field);
				}
			}
		}

		// 根据颜色柱，合并为形状。柱状结构微环路
		// colorColumn

		// 考虑相邻感受野颜色渐变。
		// 大脑用的算法，应该也是递归算法，相邻相近的颜色合并。

		List<Form> formList = new ArrayList<Form>();
		Set<ReceptiveField> repeatSet = new HashSet<ReceptiveField>();// 已经被合并过

		// 根据颜色合并
		for (int j = radius; j <= fieldImage.length - radius - 1; j += radius) {
			for (int k = radius; k <= fieldImage[0].length - radius - 1; k += radius) {
				ReceptiveField field = fieldImage[j][k];
				if (!repeatSet.contains(field) && field.getFrequency() <= 0.25) {
					Set<ReceptiveField> fieldSet = new HashSet<ReceptiveField>();
					List<ReceptiveField> waitSet = new ArrayList<ReceptiveField>();
					waitSet.add(field);
					while (waitSet.size() > 0) {// 没有用递归是因为递归会导致栈溢出
						ReceptiveField temp = waitSet.remove(0);
						if (!repeatSet.contains(temp)) {
							fieldSet.add(temp);
							repeatSet.add(temp);
							mergeNeighborByColor(grayImage, fieldImage, repeatSet, fieldSet, waitSet, temp);
						}
					}

					if (fieldSet.size() > 2) {
						Form form = new Form(fieldSet);
						formList.add(form);
					}
				}
			}
		}

		// 颜色的稠密程度。

		// TODO 在高空间频率区域中（1.5到4.25），存在颜色主题色或混合色，占主导地位的情况。加以区分即可。
		// 怎么找到主导颜色，

		// 根据空间频率合并，取9个感受野统计
		int v2radius = radius * 2;// 感受野半径
		for (int j = v2radius; j <= fieldImage.length - v2radius - 1; j += (v2radius * 2)) {
			for (int k = v2radius; k <= fieldImage[0].length - v2radius - 1; k += (v2radius * 2)) {

				// 取9个感受野，统计空间频率。
				double sf = 0;
				int count = 0;
//				int color = 0;// 空间频率是一个波动很大，不稳定的指标。但颜色规律跟容易合并。
				// 颜色再合并统计
				SortedMap<Integer, Integer> colorFeature = new TreeMap<Integer, Integer>();
				for (int m = j - v2radius + radius; m < j + v2radius; m += radius) {
					for (int n = k - v2radius + radius; n < k + v2radius; n += radius) {
						ReceptiveField field = fieldImage[m][n];
						count++;
						sf += field.getFrequency();
						if (field.getColorStats() != null) {
							for (int color : field.getColorStats().keySet()) {
								colorFeature.putIfAbsent(color,
										field.getColorStats().get(color) + colorFeature.getOrDefault(color, 0));
							}
						}
					}
				}
//				System.out.println("sf/" + count + ": " + (sf / count));

//				int c = colorFeature.size() > 0 ? colorFeature.firstKey() : 0;
//				System.out.println(c);
//				Color randomColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
//				sf = sf / count;
//				if (sf > 1.5 && sf <= 5) {
//					for (int m = j - v2radius + radius; m < j + v2radius; m += radius) {
//						for (int n = k - v2radius + radius; n < k + v2radius; n += radius) {
//							ReceptiveField field = fieldImage[m][n];
//							for (int x = -radius; x < radius; x++) {
//								for (int y = -radius; y < radius; y++) {
//									result.setRGB(field.getX() + x, field.getY() + y, randomColor.getRGB());
//								}
//							}
//						}
//					}
//				}

				ReceptiveField field = fieldImage[j][k];
				if (!repeatSet.contains(field)) {
					Set<ReceptiveField> fieldSet = new HashSet<ReceptiveField>();
					List<ReceptiveField> waitSet = new ArrayList<ReceptiveField>();
					waitSet.add(field);
					while (waitSet.size() > 0) {// 没有用递归是因为递归会导致栈溢出
						ReceptiveField temp = waitSet.remove(0);
						if (!repeatSet.contains(temp)) {
							fieldSet.add(temp);
							repeatSet.add(temp);
							mergeNeighborByHue(grayImage, fieldImage, repeatSet, fieldSet, waitSet, temp);
						}
					}

					if (fieldSet.size() > 2) {
						Form form = new Form(fieldSet);
						formList.add(form);
					}
				}
			}
		}

		for (Form form : formList) {
			Color randomColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
			for (ReceptiveField field : form.getFieldSet()) {
				for (int m = -radius; m < radius; m++) {
					for (int n = -radius; n < radius; n++) {
						result.setRGB(field.getX() + m, field.getY() + n, randomColor.getRGB());
					}
				}
			}
		}

//		for (int m = image.getMinX(); m < image.getWidth(); m++) {
//			for (int n = image.getMinY(); n < image.getHeight(); n++) {
		// 去掉RGB中的绿色
//				int rgb = image.getRGB(m, n);

//				float hue = getHue(rgb);
//				System.out.println("hue: " + hue);
//				int green = (rgb >> 8) & 0xFF;
//				rgb = ((rgb >> 16) & 0xFF) << 16 | ((rgb >> 0) & 0xFF) << 0 | (0xFF & 0xFF00);
//				rgb = (0xFF & 0x0000) | (green & 0xFF) << 8 | (0xFF & 0x0000);
//				result.setRGB(m, n, rgb);
//			}
//		}
		return fieldImage;
	}

	private static void mergeNeighborByColor(int[][] grayImage, ReceptiveField[][] fieldImage,
			Set<ReceptiveField> repeatSet, Set<ReceptiveField> fieldSet, List<ReceptiveField> waitSet,
			ReceptiveField field) {
		// 右邻
		if (field.getX() + radius < fieldImage.length - radius - 2) {
			ReceptiveField neighbor = fieldImage[field.getX() + radius][field.getY()];
			if (neighbor.getFrequency() <= 0.25 && rgbSimilar(field.getColor(), neighbor.getColor(), 15)) {
				waitSet.add(neighbor);// 确定合并，加入下一个待比较
			}
		}

		// 下邻
		if (field.getY() + radius < fieldImage[0].length - radius - 2) {
			ReceptiveField neighbor = fieldImage[field.getX()][field.getY() + radius];
			if (neighbor.getFrequency() <= 0.25 && rgbSimilar(field.getColor(), neighbor.getColor(), 15)) {
				waitSet.add(neighbor);// 确定合并，加入下一个待比较
			}
		}
	}

	private static void mergeNeighborByHue(int[][] grayImage, ReceptiveField[][] fieldImage,
			Set<ReceptiveField> repeatSet, Set<ReceptiveField> fieldSet, List<ReceptiveField> waitSet,
			ReceptiveField field) {
		// 右邻
		if (field.getX() + radius < fieldImage.length - radius - 2) {
			ReceptiveField neighbor = fieldImage[field.getX() + radius][field.getY()];
			if (neighbor.getFrequency() > 0.25
					&& hueSimilar(field.getColorStats().firstKey(), neighbor.getColorStats().firstKey())) {
				waitSet.add(neighbor);// 确定合并，加入下一个待比较
			}
		}

		// 下邻
		if (field.getY() + radius < fieldImage[0].length - radius - 2) {
			ReceptiveField neighbor = fieldImage[field.getX()][field.getY() + radius];
			if (neighbor.getFrequency() > 0.25
					&& hueSimilar(field.getColorStats().firstKey(), neighbor.getColorStats().firstKey())) {
				waitSet.add(neighbor);// 确定合并，加入下一个待比较
			}
		}
	}

	private static void mergeNeighborByTexture(int[][] grayImage, ReceptiveField[][] fieldImage,
			Set<ReceptiveField> repeatSet, Set<ReceptiveField> fieldSet, List<ReceptiveField> waitSet,
			ReceptiveField field) {

		// 当前感受野，取左、中、右，3个感受野，并统计

		// 右邻
		if (field.getX() + radius < fieldImage.length - radius - 2) {
			ReceptiveField neighbor = fieldImage[field.getX() + radius][field.getY()];
			if (neighbor.getColorStats() != null && textureSimilar(field, neighbor)) {// 空间频率相近
				waitSet.add(neighbor);// 确定合并，加入下一个待比较
			}
		}

		// 下邻
		if (field.getY() + radius < fieldImage[0].length - radius - 2) {
			ReceptiveField neighbor = fieldImage[field.getX()][field.getY() + radius];
			if (neighbor.getColorStats() != null && textureSimilar(field, neighbor)) {// 空间频率相近
				waitSet.add(neighbor);// 确定合并，加入下一个待比较
			}
		}

	}

	public static boolean textureSimilar(ReceptiveField field, ReceptiveField neighbor) {
		// 颜色统计相近，空间频率相近
//		if (Math.abs(neighbor.getFrequency() - field.getFrequency()) > 1) {
//			return false;
//		}

		// TODO 空间频率 一定有其他规律，组成纹理。

		// FIXME 如果3组（可以有多种组合方式）感受野连结组成的区域与其他位置3组感受野组成的区域，统计结果相同，则认为这些区域是纹理？

		// 纹素（texel）是纹理图片空间的基本单元，可以看成是纹理的组成“像素”
		// 有规律的纹理，无规律的纹理，只能用统计方法。用其中的少量感受野组合，与其他位置比较。

		// 空间频率分布规律。颜色也是通过相近算法。
		// 是否有边缘强化？
		// 用更大的感受野，肯定可以做到。

		// V2细条纹接收V1斑点区的投射，V2的粗/浅条纹接收V1斑点间区的投射

		// 需要用到卷积

		// 空间频率，应当用密度分析？

		boolean sfSimilar = false;
		if (field.getFrequency() <= 4 && neighbor.getFrequency() <= 4) {// V4偏好低空间频率，0到2.5，特别是0.25附近，可能是边缘
			sfSimilar = true;
//			result.setRGB(m, n, 0x00FF00);// 绿色
//			result.setRGB(m, n, field.getColorStats().firstKey());
//		} else if (field.getFrequency() > 1 && field.getFrequency() < 2.5 && neighbor.getFrequency() > 1
//				&& neighbor.getFrequency() < 2.5) {// V2偏好0.5到3.5，特别是2附近，可能是条纹、纹理
//			sfSimilar = true;
//			result.setRGB(m, n, 0x0000FF);// 蓝色
//			result.setRGB(m, n, field.getColorStats().firstKey());
		} else if (field.getFrequency() >= 4.3 && neighbor.getFrequency() >= 4.3) {// V1偏好高空间频率，1.5到4.25左右，特别是3附近，属于边缘。
			sfSimilar = true;
//			result.setRGB(m, n, 0xFF0000);// 红色
//			result.setRGB(m, n, field.getColorStats().firstKey());
		}
		return sfSimilar;
//		if (!sfSimilar) {
//			return false;
//		}

//		if (rgbSimilar(field.getColor(), neighbor.getColor(), 15)) {
//			return true;
//		}
//
//		SortedMap<Integer, Integer> fieldColor = field.getColorStats();
//		SortedMap<Integer, Integer> neighborColor = neighbor.getColorStats();
//
//		// 如果颜色统计相近
//		for (int c1 : fieldColor.keySet()) {
//			for (int c2 : neighborColor.keySet()) {
//				if (rgbSimilar(c1, c2, 30)) {// 纹理中的颜色，饱和度会不断变化。
//					return true;
//				}
//			}
//		}

//		return false;
	}

	/**
	 * 色相相似
	 * 
	 * @param rgb
	 * @param color
	 * @param contrast
	 * @return
	 */
	public static boolean hueSimilar(int hue, int other) {
		System.out.println("hue: " + hue + "/" + other);
		return hue / 10 == other / 10;
	}

	public static boolean rgbSimilar(int rgb, int color, int contrast) {
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
		return rgbSimilar(color, r, g, b, contrast);
	}

	public static boolean rgbSimilar(int rgb, int r0, int g0, int b0, int contrast) {// TODO 应当与平均值比较，还应当与相邻的像素比较（渐变）
//		return contrastSimilar(rgb, r0, g0, b0);
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);

		// 当颜色是黑色或白色时，RGB的3个值，

		// 应当计算RGB三个值得累计差

		// TODO 如果周围对比度高，则应当把阈值调高，否则调低。

//		return Math.pow(r - r0, 2) + Math.pow(g - g0, 2) + Math.pow(b - b0, 2) < Math.pow(contrast, 2) && // 颜色相近

//		return Math.abs(brightness(rgb) - brightness(r0, g0, b0)) < 10 ||
//		&& // 亮度相近
		return Math.abs(r - r0) < contrast && Math.abs(g - g0) < contrast && Math.abs(b - b0) < contrast;
//				|| (Math.pow(r - r0, 2) + Math.pow(g - g0, 2) + Math.pow(b - b0, 2) < Math.pow(12, 2)); // 颜色相近
//		return Math.abs(r - r0) + Math.abs(g - g0) + Math.abs(b - b0) < 20;

//		return Math.abs(r - r0) < RANGE && Math.abs(g - g0) < RANGE && Math.abs(b - b0) < RANGE;
	}

	/**
	 * 空间频率，是单位视角内明暗条纹重复出现的周期数。或急剧变化的情况？
	 * 
	 * <p>
	 * FIXME 高空间频率是指在单位空间中出现的频率高？还是单位空间中出现的频率变化高？
	 */
	public static double spatialFrequency(int[][] grayImage, int x0, int y0, int radius, double contrastThreshold) {

		List<Integer> list = new ArrayList<Integer>();
		// 计算当前感受野的空间频率。
		double sf = 0;
		int x1, y1, x2, y2;
		// 方位和空间频率属性的梯度线夹角以大角度（60°-90°）为主，根据方位图检测？而在非方位选择性区域内，则不存在这一现象
		for (int i = 0; i < 180; i += 5) {// 在180度范围内搜索，在8个方向搜索即可
			// 外环两个点
			x1 = (int) Math.round(x0 - radius * Math.sin(Math.PI * (i - 90) / 180));
			y1 = (int) Math.round(y0 + radius * Math.cos(Math.PI * (i - 90) / 180));// - radius

			x2 = (x1 > x0) ? x0 - (x1 - x0) : x0 + (x0 - x1);
			y2 = (y1 > y0) ? y0 - (y1 - y0) : y0 + (y0 - y1);
			list.clear();
			if (i > 45 && i < 135) {// 将Y轴与X轴调换，为了获得更多的像素点
				double slope = ((double) (x2 - x1)) / ((double) (y2 - y1));
				int from = Math.min(y1, y2);
				int to = Math.max(y1, y2);

				for (int y = from; y <= to; y++) {
					// 当斜率等于0，平行与X轴
					int x = (int) Math.round((y - y1) * slope + x1);
					// 求这条线上交替光栅的周期数。
					list.add(grayImage[x][y]);
				}

				sf = Math.max(sf, spatialFrequency(list, contrastThreshold));
			} else {
				double slope = ((double) (y2 - y1)) / ((double) (x2 - x1));
				int from = Math.min(x1, x2);
				int to = Math.max(x1, x2);
				for (int x = from; x <= to; x++) {
					// 当斜率等于0，平行与X轴
					int y = (int) Math.round((x - x1) * slope + y1);
					list.add(grayImage[x][y]);
				}
				sf = Math.max(sf, spatialFrequency(list, contrastThreshold));
			}
		}
		// 求一个最大值即可，还是要求各个方向？

		// 怎么计算当前视野的空间频率。

		// FIXME 在同一个视野里，有一次灰度变换，则认为是空间频率为1，有两次变换则认为是2？

		// 梯度值表征为第x行第y列的某像素和他的右邻或下邻像素的差值，梯度越大说明像素值差异越大，画面内容突变越明显

		// 傅里叶变换
		// 亮暗作正弦调制的栅条周数，单位是周/度。空间频率的物理内涵是单位长度所含的波数，也可以认为是单位视角内明暗条纹重复出现的周期数。
		// 怎么计算？

		// 高空间频率功能柱与方位功能柱的变化梯度呈正交分布。
		return sf;
	}

	/**
	 * 空间频率为3左右为高空间频率，
	 * 
	 * @param data
	 * @param threshold
	 * @return
	 */
	public static double spatialFrequency(List<Integer> data, double threshold) {
//		double frequency = 0;
		int sin = 0;// 如果是负数，则表示在波谷，如果是正数，则是在波峰，0 表示在中间。
		int min = data.get(0);// 区间极小值
		int max = data.get(0);// 区间极大值

		// TODO 应当看波长？还是应当以波长为准？
		int minWavelength = 100;// 最小波长
		int lastPeak = 0;
		for (int i = 1; i < data.size(); i++) {
			max = Math.max(data.get(i), max);
			min = Math.min(data.get(i), min);
			if (sin <= 0 && contrastRatio(data.get(i), min) >= threshold) {// 出现波峰
				minWavelength = lastPeak > 0 ? Math.min(minWavelength, i - lastPeak) : 100;
				lastPeak = i;
//				frequency++;
				sin = 1;
				max = data.get(i);// 极大值，重新定义
			} else if (sin >= 0 && contrastRatio(max, data.get(i)) >= threshold) {// 出现波谷
				minWavelength = lastPeak > 0 ? Math.min(minWavelength, i - lastPeak) : 100;
				lastPeak = i;
//				frequency++;
				sin = -1;
				min = data.get(i);// 极小值，重新定义
			}
//			System.out.println(data.get(i) + ", f: " + frequency + ", sin: " + sin);
		}

		return (data.size() / 2F) / minWavelength;// TODO 如果是斜着的话就不准。
//		return frequency / 4f;// 两个波峰和两个波谷，算一个正玄频率
	}

	/**
	 * 视网膜亮度感受野。
	 * 
	 * <Point> 先通过同心圆拮抗式感受野重新计算亮度。 <Point>
	 * 同心圆拮抗式感受野，由一个兴奋作用强的中心机制和一个作用较弱但面积更大的抑制性周边机制构成[Rodieck 1965]．
	 * 这两个具有相互拮抗作用的机制，都具有高斯分布的性质，但中心机制具有更高的峰敏感度，而且彼此方向相反，故称相减关系，又称高斯差模型(Difference
	 * of Gaussians，DOG)．
	 * 
	 * <Point> 马赫带效应，处于明暗边界稍稍偏向亮处位置，感受野的反应最强烈，因为其感受野兴奋性中心全部被光照射，而抑制性周边没有全部被光照。
	 * 相反，处在边界偏向暗处时，因为只有小部分抑制性周边受到光照，故其反应比黑暗中无刺激时的神经节细胞自发放电水平还要低。
	 * 这说明，明暗边界感受野并不是根据一个像素得到的，而是通过周围像素的加权平均得到，并且需要通过拮抗式计算。
	 * 
	 * <Point> 马赫带的好处是，可以增强明暗边界的对比度，更有利于计算物体边缘和轮廓。
	 * 
	 * @return
	 */
	public static int[][] brightnessReceptiveField(BufferedImage image, int radius) {

		BufferedImage grayII = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		int[][] grayImage = new int[image.getWidth()][image.getHeight()];
		for (int m = image.getMinX(); m < image.getWidth(); m++) {
			for (int n = image.getMinY(); n < image.getHeight(); n++) {
				// image.setRGB 实际是sRGB，存储的还有Alpha，存取RGB的值，需要通过Color。
				int b = brightness(image.getRGB(m, n));// 灰度处理
				int gray = (b <<16) | (b<< 8)|b;
				grayImage[m][n] = gray;
				
				
				grayII.setRGB(m, n, grayImage[m][n]);
			}
		}

		try {
			ImageIO.write(grayII, "jpg", new File("D:/file/temp/leuvenA-gray.jpg"));// 05-retina.jpg
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 高斯模糊，增强边界
		int[][] gaussianImage = filteringGaussian(grayII, 2);

		grayImage = gaussianImage;

		// 马赫带效应处理，构造同心圆拮抗式感受野。将明暗边界处的亮侧增强亮度，暗测降低亮度。
		// 在灰度处理的基础上，考虑视网膜感受野的马赫带效应，增强明暗边界的对比度，更有利于计算物体边缘和轮廓。
		// 中心全被光照，环绕部分有小部分没有光照，则将这个中心区域的亮度增强；
		int[][] machImage = Arrays.stream(grayImage).map(a -> Arrays.copyOf(a, a.length)).toArray(int[][]::new);
		for (int x = image.getMinX() + radius + 1; x < image.getWidth() - radius - 1; x += (radius / 2)) {
			for (int y = image.getMinY() + radius + 1; y < image.getHeight() - radius - 1; y += (radius / 2)) {
				List<Integer> centerPoint = new ArrayList<Integer>();// 中心
				List<Integer> aroundPoint = new ArrayList<Integer>();// 环绕

				for (int j = x - radius; j < x + radius; j++) {
					for (int k = y - radius; k < y + radius; k++) {
						if ((j - x) * (j - x) + (k - y) * (k - y) <= (radius / 2) * (radius / 2)) {// 在同心圆中心部分
							centerPoint.add(grayImage[j][k]); // 只取RGB中的R，便于计算
						} else if ((j - x) * (j - x) + (k - y) * (k - y) <= radius * radius) {// 在同心圆环绕部分
							aroundPoint.add(grayImage[j][k]);// 只取RGB中的R，便于计算
						}
					}
				}

				// 太多的大数字相加，溢出
				Double centerBrightness = centerPoint.stream().mapToDouble(a -> a).average().getAsDouble();
				Double aroundBrightness = aroundPoint.stream().mapToDouble(a -> a).average().getAsDouble();
				int compare = centerBrightness.compareTo(aroundBrightness);
				if (compare == 0) {
				} else if (centerBrightness - aroundBrightness > 1) {// 中心部分比环绕部分亮
					for (int j = x - radius; j < x + radius; j++) {
						for (int k = y - radius; k < y + radius; k++) {
							if ((j - x) * (j - x) + (k - y) * (k - y) <= (radius / 2) * (radius / 2)) {// 在同心圆中心部分
								int gray = grayImage[j][k];// 亮度
								// 增强或减弱的过大，会形成新的边缘。// gray / 100;// 亮度增强10%
								gray += (centerBrightness - aroundBrightness) > 3 ? 3
										: (centerBrightness - aroundBrightness);
								machImage[j][k] = gray > 255 ? 255 : gray;
							}
						}
					}
				} else if (aroundBrightness - centerBrightness > 1) {// 中心部分比环绕部分暗
					for (int j = x - radius; j < x + radius; j++) {
						for (int k = y - radius; k < y + radius; k++) {
							if ((j - x) * (j - x) + (k - y) * (k - y) <= (radius / 2) * (radius / 2)) {// 在同心圆中心部分
								int gray = grayImage[j][k];// 亮度
								// 增强或减弱的过大，会形成新的边缘。// gray / 100;// 亮度增强10%
								gray -= (aroundBrightness - centerBrightness) > 3 ? 3
										: (aroundBrightness - centerBrightness);// gray / 100;// 亮度减弱10%
								machImage[j][k] = gray < 0 ? 0 : gray;
							}
						}
					}
				}

			}
		}

		return machImage;
	}

	/**
	 * 高斯滤波
	 * 
	 * @param image
	 * @param g
	 * @return
	 */
	private static int[][] filteringGaussian(BufferedImage image, double g) {
		int w = image.getWidth();
		int h = image.getHeight();

		int length = 5;
		int k = length / 2;
		double sigma = Math.sqrt(g);

		double[][] gaussian = new double[length][length];
		double sum = 0;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				gaussian[i][j] = Math.exp(-((i - k) * (i - k) + (j - k) * (j - k)) / (2 * sigma * sigma));
				gaussian[i][j] /= 2 * Math.PI * sigma * sigma;
				sum += gaussian[i][j];
			}
		}
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				gaussian[i][j] /= sum;
			}
		}

		BufferedImage gaussianImg = new BufferedImage(w, h, image.getType());

		int[][] grayImage = new int[image.getWidth()][image.getHeight()];
		for (int x = k; x < w - k; x++) {
			for (int y = k; y < h - k; y++) {
				int newpixel = 0;
				for (int gx = 0; gx < length; gx++) {
					for (int gy = 0; gy < length; gy++) {
						int ix = x + gx - k;
						int iy = y + gy - k;
						if (ix < 0 || iy < 0 || ix >= w || iy >= h)
							continue;
						else {
							newpixel += getGray(image.getRGB(ix, iy)) * gaussian[gx][gy];
						}
					}
				}
				newpixel = (int) Math.round(1.0 * newpixel);
				gaussianImg.setRGB(x, y, new Color(newpixel, newpixel, newpixel).getRGB());
				grayImage[x][y] = newpixel;// 灰度处理
			}
		}
		return grayImage;
	}

	private static int getGray(int pixel) {
		return pixel & 0xff;
	}

	public static int brightness(int rgb) {
		// 亮度公式 Brightness = ((R*299)+(G*587)+(B*114))/1000
		return ((((rgb & 0xff0000) >> 16) * 299) + (((rgb & 0xff00) >> 8) * 587) + ((rgb & 0xff) * 114)) / 1000;
	}

	/**
	 * 最大亮度与最小亮度之间的比值
	 */
	public static double contrastRatio(double brightness1, double brightness2) {
		if (brightness1 <= 0 && brightness2 <= 0) {
			return 0;
		} else {// 对比度公式需要改进
			return Math.abs(brightness1 - brightness2) / (brightness1 + brightness2);
		}
	}

	public static float[] rgbToHsv(int r, int g, int b) {
		float r1 = r / 255f;
		float g1 = g / 255f;
		float b1 = b / 255f;

		float max = Math.max(r1, Math.max(g1, b1));
		float min = Math.min(r1, Math.min(g1, b1));
		float delta = max - min;

		float h, s, v;

		v = max; // Value: the average of the max and min
		s = (max == 0) ? 0 : delta / max; // Saturation
		if (max == min) {
			h = 0; // undefined
		} else if (max == r1) {
			h = (60 * ((g1 - b1) / delta) + 360) % 360; // Red is the max
		} else if (max == g1) {
			h = (60 * ((b1 - r1) / delta) + 120) % 360; // Green is the max
		} else {
			h = (60 * ((r1 - g1) / delta) + 240) % 360; // Blue is the max
		}

		return new float[] { h, s * 100, v * 100 }; // 返回HSV值，其中S和V乘以100以便于阅读
	}

	/**
	 * 色相
	 * 
	 * @param pixel
	 * @return
	 */
	private static float getHue(int rgb) {

		float r1 = ((rgb & 0xff0000) >> 16) / 255f;
		float g1 = ((rgb & 0xff00) >> 8) / 255f;
		float b1 = ((rgb & 0xff)) / 255f;

		float max = Math.max(r1, Math.max(g1, b1));
		float min = Math.min(r1, Math.min(g1, b1));
		float delta = max - min;

		float h, s, v;
//		v = max; // Value: the average of the max and min
//		s = (max == 0) ? 0 : delta / max; // Saturation
		if (max == min) {
			h = 0; // undefined
		} else if (max == r1) {
			h = (60 * ((g1 - b1) / delta) + 360) % 360; // Red is the max
		} else if (max == g1) {
			h = (60 * ((b1 - r1) / delta) + 120) % 360; // Green is the max
		} else {
			h = (60 * ((r1 - g1) / delta) + 240) % 360; // Blue is the max
		}

		// 色调H取值范围为0°～360°。若从红色开始按逆时针方向计算，红色为0°，绿色为120°，蓝色为240°。它们的补色是：黄色为60°，青色为180°，紫色为300°；
		// 饱和度S：取值范围为0.0～1.0；
		// 亮度V：取值范围为0.0(黑色)～1.0(白色)。
		return h;
	}

}
