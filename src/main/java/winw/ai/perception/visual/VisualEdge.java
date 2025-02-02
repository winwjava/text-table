package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.imageio.ImageIO;

/**
 * 视觉感受野，亮暗边界。
 * 
 * <Point>
 * 初级视皮质的感受野是长条形，兴奋区和抑制区不是同心圆而是并排。这使得这一感受野除了对边缘敏感之外，对方向也十分敏感。初级视皮质细胞的感受野也分为兴奋区和抑制区。左侧表示的是一个在感受野内的条形刺激，整体处于兴奋区时使神经元兴奋，但当它的方向发生变化时会覆盖一部分抑制区，使得兴奋性降低。右侧表示的是条形刺激位置变化对神经元兴奋性的影响，可以看出这里对竖条的位置变化最敏感。
 * 
 * 但并不是所有初级视皮质神经元感受野都像上面一样对竖条的位置敏感，一些复杂细胞(complex
 * cell)会在感受野的同一位置同时产生兴奋和抑制反应，所以刺激的位置变化对神经元兴奋性的影响不大。这可以看作是一些简单神经元的叠加(如图)，这些神经元仍然对方向敏感，但对位置不敏感。
 * 初级视皮质的细胞也存在水平连接，兴奋可以在细胞之间传播，这使得图像可以连接在一起被处理。可以看到，一组横线方向一致时会引起更大的反应：
 * 另外还有一种超复杂细胞(hypercomplex
 * cell)，超复杂细胞对于超出感受野的刺激也会有反应。对于复杂细胞，只对感受野内的刺激反应，刺激如果超出感受野之外，那么超出的部分不会对细胞造成额外的影响，但对于超复杂细胞，超出感受野的部分会造成额外的抑制，这使得超复杂细胞对长度也很敏感。如图：
 * basic vision: an introduction of visual perception和principles of neural
 * science
 * 
 * 
 * https://zhuanlan.zhihu.com/p/504473111?utm_id=0
 * 
 * <Point> 视网膜上接受光信号的细胞叫Photoreceptor（感光细胞），并且分为两种类型，Rods（视杆细胞）和Cones（视锥细胞）。
 * Rods只有在非常低的光照下才启动，Cones在较亮的光照下工作，并且获得颜色信息。Cones的大小在1000nm左右，很接近
 * 最大可见波长（这暗示着人类可见光最大波长，或者说Cones不能再小了）。每个人的视网膜上包含120 million的Rods和6
 * million的Cones，但千万不要认为人感知颜色的能力就弱于感知亮度的能力。因为6 million
 * Cones绝大部分集中在Fovea（中央窝），我们可以通过转动眼睛将中央窝对准感兴趣的区域。
 * 
 * <Point> 方位选择性（orientation selectivity）
 * 所谓方位，也叫做“空间朝向”（orientation），指的是一条短线的倾斜角度（范围是0-180°，见下图）。 <Point>
 * 初级视觉皮层细胞有一种重要的性质，就是“方位选择性”。换句话说，绝大部分初级视觉皮层细胞，都有一个“最喜欢的方位”（preferred
 * orientation，中文习惯翻译成“最优方位”）。
 * 给一个初级视觉皮层细胞看不同方位的短棒，当短棒的方位等于这个细胞的最优方位时，细胞的反应就达到最强。如果短棒的方位逐渐偏离最优方位，细胞的反应就越来越弱。如果短棒的方位和最优方位垂直，那么此时细胞的反应就达到最低点。
 * 这样一来，我们就可以通过观察细胞的反应强弱变化，来得知这个细胞最喜欢哪个方位了。
 * 初级视觉皮层细胞的上述性质，被称为“方位选择性”。类似的，还存在“运动方向选择性”、“空间频率/时间频率选择性”（很快会讲到）、“颜色选择性”等等。
 * 这些选择性，本质上使得初级视觉皮层具备了检测并编码各种视觉特征的能力。
 * 
 * <Point> https://zhuanlan.zhihu.com/p/134596480
 * https://zhuanlan.zhihu.com/p/20579210 https://zhuanlan.zhihu.com/p/186999395
 * https://zhuanlan.zhihu.com/p/150301718 https://daily.zhihu.com/story/9646872
 * https://foundationsofvision.stanford.edu/chapter-5-the-retinal-representation/
 * https://foundationsofvision.stanford.edu/chapter-10-motion-and-depth/
 * https://foundationsofvision.stanford.edu/chapter-9-color/
 * 
 * @author winw
 *
 */
public class VisualEdge {// TODO 这里考虑采用二维数组存储亮度信息，避免RGB算来算去很慢。

	/**
	 * 亮度0~255，亮度差异大于10，则认为是边缘。RGB经过灰度处理后R=G=B，只取其中一个值比较即可
	 */
	public static int RANGE = 15;// 明暗梯度，当前感受野存在亮度差异。黑暗环境下对比度小。

	// 总共640 × 480 像素，72度，每度大约6.6-8.8个像素，半径是3-5个像素。
	public static int radius = 3;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	public static BufferedImage show(BufferedImage image) {
//		BufferedImage scaledImage = new BufferedImage(image.getWidth() / 2, image.getHeight() / 2, image.getType());
//		Graphics2D graphics = scaledImage.createGraphics();
//		graphics.drawImage(
//				image.getScaledInstance(image.getWidth() / 2, image.getHeight() / 2, BufferedImage.SCALE_FAST), 0, 0,
//				null);
//		graphics.dispose();

		int[][] grayImage = VisualRetina.brightnessReceptiveField(image, radius);// 灰度处理，边缘增强，返回二值化二维数组，存储亮度0~255

		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		edgeReceptiveField(result, grayImage);// 在V1或V2，线条感受野，当两个有交集时，可以合并。
		return result;
	}

	/**
	 * V1区感受野，分为简单细胞感受野、复杂细胞感受野、超复杂细胞的感受野。 <Point>
	 * 外膝体细胞和节细胞一样具有开光（on）中心区和关光(off)中心区。不同的是，外膝体细胞感受野对视网膜不同区域明暗差别更灵敏。
	 * 皮层细胞感受野(Hubel, D.H, Wiesel.T.N, 1970)皮层分为简单、复杂和超复杂细胞（hypercomplex
	 * cells），对光信号刺激均具有方向选择性。
	 * 
	 * <Point> 简单细胞的感受野也分成开光（on）中心区和关光（off）中心区，但两区分界线不是圆而是直线， 其最佳刺激是有一定朝向和位置的线条。
	 * 
	 * <Point> 复杂细胞也要求有一定朝向的线条刺激，但其感受野无开光区、关光区之分，且不要求刺激有精确位置。 <Point>
	 * 超复杂细胞的感受野由中央兴奋区和侧旁抑制区构成，要求刺激有一定的朝向和运动方向，还会要求线条有端点或拐角等（而非极长的直线）。
	 * 
	 * 17区主要是简单细胞，也有复杂细胞；18区中90%是复杂细胞，其余为超复杂细胞；19区超复杂细胞则占一半。
	 * 
	 * 
	 * <Point> V2感受野的高分辨率空间结构可以分为三类（V2区由三种不同条带构成，可以通过内源信号成像方法观察到）： 1:和V1细胞感受野类似型;
	 * 2:细长型(高长宽比); 3:复杂结构型(含有多朝向成分)。 这些V2细胞感受野的结构的形成均可以通过V1细胞感受野的整合来解释。
	 * 
	 * @return
	 */
	public static LineGrid[][] edgeReceptiveField(BufferedImage image, int[][] grayImage) {

		// 复制一个Image作为结果Image，使得原Image不变，便于计算。
//		BufferedImage resultImage = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
//		int[][] resultImage = Arrays.stream(grayImage).map(a -> Arrays.copyOf(a, a.length)).toArray(int[][]::new);

		int[][] detected = new int[grayImage.length][grayImage[0].length];

		// 外侧膝状体LGN是二维数据。到了视皮层功能柱，会用List存储。
//		int[][] edges = new int[grayImage.length][grayImage[0].length];

		// TODO 外侧膝状体是二维数据，视觉皮层功能柱是一个List，相同方向相近位置的放在一起；方便合并运算；

		// 将线条放进 50*50的网格里，然后在周围网格里搜索。
		LineGrid[][] edgeGrid = new LineGrid[grayImage.length][grayImage[0].length];

		for (int i = 0 + radius + 1; i < grayImage.length - radius - 2; i++) {
			for (int j = 0 + radius + 1; j < grayImage[0].length - radius - 2; j++) {
				// 循环X和Y坐标，逐个像素比较。
				if (detected[i][j] <= 0) {
					simpleCellReceptiveField(image, grayImage, i, j, radius, detected, 1, edgeGrid);// 中间有一个像素的情况。
				}
			}
		}

		// 再用两个像素查找边缘线条；
		for (int i = 0 + radius + 1; i < grayImage.length - radius - 2; i++) {
			for (int j = 0 + radius + 1; j < grayImage[0].length - radius - 2; j++) {
				// 循环X和Y坐标，逐个像素比较。
				if (detected[i][j] <= 0) {
					simpleCellReceptiveField(image, grayImage, i, j, radius, detected, 2, edgeGrid);// 中间有两个像素的情况。
				}
			}
		}

//		complexCellReceptiveField(image, lineGridArray);
		return edgeGrid;
	}

	static int count = 0;

	/**
	 * 取两点之间直线上每个点的亮度
	 */
	public static List<Integer> getBrightness(int[][] image, int x1, int y1, int x2, int y2) {
//		List<Point> list = new ArrayList<Point>();
		List<Integer> list = new ArrayList<Integer>();
		if (x1 == x2) {// Tangent = NaN
			int from = Math.min(y1, y2);
			int to = Math.max(y1, y2);
			for (int y = from; y <= to; y++) {
				list.add(image[x1][y]);
			}
		} else {
			double slope = ((double) (y2 - y1)) / ((double) (x2 - x1));
			int step = (x2 > x1) ? 1 : -1;
			for (int x = x1; x != x2; x += step) {
				int y = (int) ((x - x1) * slope + y1);
				list.add(image[x][y]);
			}
		}
		return list;
	}

	/**
	 * 条形光斑，对线条或者边缘的感受器。简单细胞，开放的条形光斑。
	 * 
	 * <Point> 简单细胞的感受野来自于将一系列的 LGN 细胞输入的转换，许多环状的感受野就能组成一条线。
	 * 
	 * <Point> 中心区域为开的区域，即刺激能够激活的区域。而关的周围区域则分布在两侧，即刺激难以激活的区域。
	 * 在视觉皮层，对于这样感受野的细胞，我们称之为简单细胞。
	 * 
	 * <Point> 空间上这些简单细胞在视觉皮层中通常限缩到非常狭窄的区域，它们最好的刺激方式是条带光斑，并且它们对于光斑的朝向非常敏感，对于刺激有
	 * 开和关的区域拮抗。当发散光覆盖整个开和关的区域时，不能够刺激这些细胞。简单细胞因此能被看成是视觉特定区域里面对线条或者边缘的感受器。
	 * 
	 * <Point> 条形光斑的两端是开放区域，对宽度有限缩。有利于拼出更长的线条。 <Point>
	 * 一旦条形光斑固定好一定高度，增长不会产生额外的刺激效果，相反，不管在条形光斑哪边的宽度增加都会降低视觉神经元的放电频率。
	 * 因此这些视觉细胞的感受野可以被描绘成如图 3c所示。
	 * 中心区域为开的区域，即刺激能够激活的区域。而关的周围区域则分布在两侧，即刺激难以激活的区域。在视觉皮层，
	 * 对于这样感受野的细胞，我们称之为简单细胞。空间上这些简单细胞在视觉皮层中通常限缩到非常狭窄的区域，
	 * 它们最好的刺激方式是条带光斑，并且它们对于光斑的朝向非常敏感，对于刺激有开和关的区域拮抗。当发散光覆盖整个开和关的区域时，
	 * 不能够刺激这些细胞。简单细胞因此能被看成是视觉特定区域里面对线条或者边缘的感受器。
	 * 
	 * @return
	 */
	public static boolean simpleCellReceptiveField(BufferedImage image, int[][] blurImage, int x0, int y0, int radius,
			int[][] detected, int width, LineGrid[][] edgeGrid) {// orientationSelectivity
		// 亮度分界
		// 区域汇聚

//		List<Line> lineColumn = new ArrayList<Line>();// 方向、线条功能柱，相同方向相近位置的放在一起；

		int gray = blurImage[x0][y0];// 中心

		int grayR = blurImage[x0 + width][y0];
		int grayL = blurImage[x0 - 1][y0];
		int grayT = blurImage[x0][y0 + width];
		int grayD = blurImage[x0][y0 - 1];

		// 计算对比度，对比度高时，将RANGE调大，否则调小。
		int minBrightness = gray;// (rgb >> 16) & 0xFF; // 取红色分量作为亮度
		int maxBrightness = gray;

		// 计算视野中的对比度。
		for (int j = x0 - radius; j < x0 + radius; j++) {
			for (int k = y0 - radius; k < y0 + radius; k++) {
				minBrightness = Math.min(minBrightness, blurImage[j][k]);
				maxBrightness = Math.max(maxBrightness, blurImage[j][k]);
			}
		}

		int cc = maxBrightness - minBrightness;

		int range = 20;
//		System.out.println(maxBrightness + " - "+ minBrightness +" = "+ cc);

		if (cc < 15) {
			return false;
		}

//		range = cc / 2;

		if (cc > 60) {
			range = 30;
		} else if (cc > 30) {
			range = 20;
		}
//		else {
//			range = 10;
//		}

		// 如果中心点周围4个点没有亮度差异，则跳过，否则开始找边缘。
		if (Math.abs(grayR - gray) < range && Math.abs(gray - grayL) < range && Math.abs(gray - grayT) < range
				&& Math.abs(gray - grayD) < range) {
			return false;
		}

		Graphics graphics = image.getGraphics();
		int x1, y1, x2, y2;

		// 用外环线绕180度，看每个角度的直线。
		// 方位选择性（orientation selectivity），也叫做“空间朝向”（orientation），指的是一条短线的倾斜角度（范围是0-180°）
		// 绝大部分初级视觉皮层细胞，都有一个“最喜欢的方位”（preferred orientation，中文习惯翻译成“最优方位”）
		// 类似的，还存在“运动方向选择性”、“空间频率/时间频率选择性”（很快会讲到）、“颜色选择性”等等。

		TreeMap<Integer, Double> orientationSelectivity = new TreeMap<Integer, Double>();
		for (int i = 0; i < 180; i++) {
			// 外环两个点
			x1 = (int) Math.round(x0 - radius * Math.sin(Math.PI * (i - 90) / 180));
			y1 = (int) Math.round(y0 + radius * Math.cos(Math.PI * (i - 90) / 180));// - radius

			x2 = (x1 > x0) ? x0 - (x1 - x0) : x0 + (x0 - x1);
			y2 = (y1 > y0) ? y0 - (y1 - y0) : y0 + (y0 - y1);

			List<Integer> centerFieldBrightness = new ArrayList<Integer>();

			int centerDiff = 0;// 在线条上，中间与两侧拮抗；
			int bothSidesDiff = 0;// 在边缘上，两侧拮抗；

			if (i > 45 && i < 135) {// 将Y轴与X轴调换，为了获得更多的像素点
				double slope = ((double) (x2 - x1)) / ((double) (y2 - y1));
				int from = Math.min(y1, y2);
				int to = Math.max(y1, y2);
				for (int y = from; y <= to; y++) {
					// 当斜率等于0，平行与X轴
					int x = (int) Math.round((y - y1) * slope + x1);

					centerFieldBrightness.add(blurImage[x][y]);
					detected[x][y] = 1;
					if (i >= 30 && i < 60) {// 靠近45度斜线，10到11点钟方向
						// https://lup.lub.lu.se/luur/download?func=downloadFile&recordOId=1333297&fileOId=1333298
						// 两种情况：1、感受野两侧亮度差异；感受野两侧亮度相同，与中间的亮度存在差异；

						// 中间线条的亮度平均数、与两侧亮度平均数比较；

						if (Math.abs(blurImage[x + width][y - 1] - blurImage[x - 1][y + width]) >= range) {
							bothSidesDiff++;
						}
						if (Math.abs(blurImage[x + width][y - 1] - blurImage[x][y]) >= range
								&& Math.abs(blurImage[x - 1][y + width] - blurImage[x][y]) >= range) {
							centerDiff++;
						}
					} else if (i >= 60 && i < 120) {// 靠近Y轴
						if (Math.abs(blurImage[x + width][y] - blurImage[x - 1][y]) >= range) {
							bothSidesDiff++;
						}
						if (Math.abs(blurImage[x + width][y] - blurImage[x][y]) >= range
								&& Math.abs(blurImage[x - 1][y] - blurImage[x][y]) >= range) {
							centerDiff++;
						}
					} else if (i >= 120 && i < 150) {// 靠近135度斜线，1-2点钟方向
						if (Math.abs(blurImage[x + width][y + width] - blurImage[x - 1][y - 1]) >= range) {
							bothSidesDiff++;
						}
						if (Math.abs(blurImage[x + width][y + width] - blurImage[x][y]) >= range
								&& Math.abs(blurImage[x - 1][y - 1] - blurImage[x][y]) >= range) {
							centerDiff++;
						}
					}
				}
				double contrast = Math.max(centerDiff, bothSidesDiff)
						/ Integer.valueOf(Math.abs(to - from + 1)).doubleValue();
				if (contrast > 0.2F) {
					orientationSelectivity.put(i, contrast);
				}
//				if(orientationSelectivity.get(i) > 0.95F) {
//					System.out.println("方向" + i + "度，总共比对像素：" + centerFieldBrightness.size() + "，两侧差异数： " + bothSidesDiff
//							+ ", 中线与两侧差异数：" + centerDiff);
//				}
			} else {
				double slope = ((double) (y2 - y1)) / ((double) (x2 - x1));
				int from = Math.min(x1, x2);
				int to = Math.max(x1, x2);
				for (int x = from; x <= to; x++) {
					// 当斜率等于0，平行与X轴
					int y = (int) Math.round((x - x1) * slope + y1);

					centerFieldBrightness.add(blurImage[x][y]);
					detected[x][y] = 1;
					if (i >= 30 && i < 60) {// 靠近45度斜线，10到11点钟方向
						if (Math.abs(blurImage[x + width][y - 1] - blurImage[x - 1][y + width]) >= range) {
							bothSidesDiff++;
						}
						if (Math.abs(blurImage[x + width][y - 1] - blurImage[x][y]) >= range
								&& Math.abs(blurImage[x - 1][y + width] - blurImage[x][y]) >= range) {
							centerDiff++;
						}
					} else if (i < 30 || i >= 150) {// 靠近X轴
						if (Math.abs(blurImage[x][y + width] - blurImage[x][y - 1]) >= range) {
							bothSidesDiff++;
						}
						if (Math.abs(blurImage[x][y + width] - blurImage[x][y]) >= range
								&& Math.abs(blurImage[x][y - 1] - blurImage[x][y]) >= range) {
							centerDiff++;
						}
					} else if (i >= 120 && i < 150) {// 靠近135度斜线，1-2点钟方向
						if (Math.abs(blurImage[x + width][y + width] - blurImage[x - 1][y - 1]) >= range) {
							bothSidesDiff++;
						}
						if (Math.abs(blurImage[x + width][y + width] - blurImage[x][y]) >= range
								&& Math.abs(blurImage[x - 1][y - 1] - blurImage[x][y]) >= range) {
							centerDiff++;
						}
					}
				}
				double contrast = Math.max(centerDiff, bothSidesDiff)
						/ Integer.valueOf(Math.abs(to - from + 1)).doubleValue();

				if (contrast > 0.2F) {
					orientationSelectivity.put(i, contrast);// > 135 ? i - 180 : i
				}
			}

//			if(orientationSelectivity.get(i) > 0.95F) {
//				System.out.println("方向" + i + "度，总共比对像素：" + centerFieldBrightness.size() + "，两侧差异数： " + bothSidesDiff
//						+ ", 中线与两侧差异数：" + centerDiff);
//			}
		}

//		System.out.println("最佳方位：" + key + "度，拮抗比例：" + orientationSelectivity.get(key));
		// TODO 需要前馈机制，在复杂细胞合并线条时，重新计算简单细胞。
		// FIXME 两侧差异大于 90%，取得最佳，不一定是最佳，需要根据后续的猜想，只需要在一个范围内就可以。

		// 取出最大的10个值
		List<Map.Entry<Integer, Double>> list = new ArrayList<>(orientationSelectivity.entrySet());
		list.sort(Map.Entry.<Integer, Double>comparingByValue().reversed());

		// 只取前10个元素
		List<Map.Entry<Integer, Double>> top10 = list.subList(0, Math.min(10, list.size()));

		boolean result = false;// 需要安装VALUE排序，取最大的10个。

		// TODO 取矢量平均。将所有方向的度数相加取平均值
		if (orientationSelectivity.size() <= 0) {
			return false;
		}
//		double average = calculateAverageAngle(orientationSelectivity.keySet());
//		double average = orientationSelectivity.keySet().stream()
//      .mapToDouble(angle -> Math.toRadians(angle)).average().orElse(0);
		double average = Math.round(top10.stream().mapToDouble(angle -> angle.getKey()).average().orElse(90));
//		System.out.println(average);
		// 靠近0，或者180附近，平均数是90

//		if (orientationSelectivity.get(key) > 0.20F) {
		x1 = (int) Math.round(x0 - radius * Math.sin(Math.PI * (average - 90) / 180));
		y1 = (int) Math.round(y0 + radius * Math.cos(Math.PI * (average - 90) / 180));// - radius

		x2 = (x1 > x0) ? x0 - (x1 - x0) : x0 + (x0 - x1);
		y2 = (y1 > y0) ? y0 - (y1 - y0) : y0 + (y0 - y1);

//		graphics.setColor(Color.WHITE);
		graphics.setColor(new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()));
		graphics.drawLine(x1, y1, x2, y2);

//		lineArray[x1][y1] = new Line(key, x1, y1, x2, y2);
		LineGrid p1 = edgeGrid[x1][y1];
		LineGrid p2 = edgeGrid[x2][y2];
		if (p1 == null) {
			p1 = new LineGrid();
			edgeGrid[x1][y1] = p1;
		}
		if (p2 == null) {
			p2 = new LineGrid();
			edgeGrid[x2][y2] = p2;
		}
		p1.getLineList().add(new Line((int) average, x1, y1, x2, y2));
		p2.getLineList().add(new Line((int) average, x1, y1, x2, y2));

//		for (Entry<Integer, Double> top : list.subList(0, Math.min(30, list.size()))) {// 从小到大排列。
//			if (result) {
//				break;
//			}
//			count++;
//			if (orientationSelectivity.get(top.getKey()) > 0.95F) {// 90%置信度，有一个边缘也是可信的。
//				result = true;
//			} else {
//				return false;
//			}
//			if (orientationSelectivity.get(top.getKey()) < 0.1F) {
//				result = true;
//			}
////			if (orientationSelectivity.get(key) > 0.20F) {
//			x1 = (int) (x0 - radius * Math.sin(Math.PI * (top.getKey() - 90) / 180));
//			y1 = (int) (y0 + radius * Math.cos(Math.PI * (top.getKey() - 90) / 180));// - radius
//
//			x2 = (x1 > x0) ? x0 - (x1 - x0) : x0 + (x0 - x1);
//			y2 = (y1 > y0) ? y0 - (y1 - y0) : y0 + (y0 - y1);
//
////			graphics.setColor(Color.RED);
//			graphics.setColor(new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()));
//			graphics.drawLine(x1, y1, x2, y2);
//
////			lineArray[x1][y1] = new Line(key, x1, y1, x2, y2);
//			LineGrid lineGrid = lineGridArray[x1 / (radius * 4)][y1 / (radius * 4)];
//			if (lineGrid == null) {
//				lineGrid = new LineGrid();
//				lineGridArray[x1 / (radius * 4)][y1 / (radius * 4)] = lineGrid;
//			}
//			lineGrid.getLineList().add(new Line(top.getKey(), x1, y1, x2, y2));
////			result = true;
////			}
//		}

		// TODO 需要先做高斯滤波平滑噪声
		// https://zhuanlan.zhihu.com/p/143426695
		// TODO 用Canny边缘检测，先计算每个像素点两个方向的梯度，然后计算幅值
		return result;
	}

	public void vectorAveraging() {
		// avg(a) = atan( avg(sin(a)) / avg(cos(a)) )

	}

//	public static double calculateAverageAngle(Set<Integer> angles) {
//        if (angles.isEmpty()) {
//            throw new IllegalArgumentException("角度列表为空");
//        }
//
//        // 将角度转换为弧度并计算总和
//        double totalRadians = angles.stream()
//                .mapToDouble(angle -> Math.toRadians(angle))
//                .sum();
//
//        // 计算平均弧度
//        double averageRadians = totalRadians / angles.size();
//
//        // 将平均弧度转换回角度
//        double averageAngle = Math.toDegrees(averageRadians);
//
//        // 确保平均角度在0到360度之间
//        averageAngle = (averageAngle + 360) % 360;
//
//        return averageAngle;
//    }

	static Random RANDOM = new Random();

	/**
	 * 超复杂细胞感受野，线条端点、拐角。
	 * 
	 * 简单细胞：特定位置，特定方向，复杂细胞：不管位置，特定方向，超复杂细胞：不论位置、方向，对角起反应
	 * 
	 * <Point> V1对简单的方向有反应，V2可以编码两个方向的夹角
	 */
	public static void hypercomplexCellReceptiveField() {// 分为低阶超复杂细胞和高阶超复杂细胞。类似简单细胞和复杂细胞的关系。

	}

	public static int gray(Color color) {// 灰度处理：将亮度值（0~255）分别设置到R、G、B里面
		int brightness = brightness(color);
//		int gray = (brightness << 16) | (brightness << 8) | brightness;
//		if (gray > 255 * 255 * 255) {
//			System.out.println("brightness: " + brightness +", gray: "+gray);
//		}
		return (brightness << 16) | (brightness << 8) | brightness;
	}

	public static int brightness(Color color) {
//        int gray = (int) ((0.2125f * red) +
//                          (0.7154f * grn) +
//                          (0.0721f * blu) + 0.5f);

		// int gray = (77*red + 150*green + 29*blue + 128) >> 8; // 性能更佳
		return (color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114) / 1000;
	}

	public static double brightness(int r, int g, int b) {
		// 亮度公式是 Brightness = 0.3 * R + 0.6 * G + 0.1 * B，
		// Y = ((R*299)+(G*587)+(B*114))/1000
		return ((r * 299) + (g * 587) + (b * 114)) / 1000;
	}

	public static void onCenterField() {// 用于检测光斑，与线条、区域不冲突，用不相连的点可以组成线或面

	}

	public static void offCenterField() {// 用于检测暗斑，与线条、区域不冲突，用不相连的点可以组成线或面

	}

	/**
	 * 视觉皮层功能柱
	 * 
	 * <Point>
	 * Hubel和Wiesel根据对视觉刺激的反应特征，在视皮层发现了多种神经元，分别叫做简单细胞、复杂细胞以及超复杂细胞。他们后续的研究以及后来大量科研工作者的实验对这些不同细胞的功能进行了深入的探索。他们的另外一项重要发现则是在视皮层中证实了之前由Vernon
	 * Mountcastle(1918--2015)根据其在躯体感觉皮层的研究提出的皮层功能柱的结构。他们的发现可以简单描述为许多具有相同特性的皮层细胞，在视皮层内按照一定的规则在空间上排列起来，这种按功能排列的皮层结构，即皮层的功能构筑，沿着皮层的不同层次呈现柱状分布，例如方向柱、方位柱、眼优势柱、空间频率柱以及颜色柱等。这一结构的形成对于皮层内感觉信息的处理具有重要的影响。
	 * 
	 * <Point> 有4种类型视皮层神经元。
	 * 
	 * 1．简单细胞。感受野面积较小，给光区和撤光区分离，有较明显的空间总合，反应具有线性特征，没有或很少有自发放电。具有特定方向和在视野中有固定位置的刺激，最能激发简单细胞。
	 * 
	 * 》》简单型：感受野呈狭长型，分给光区和撤光区，最佳刺激是线条。具有最佳朝向的线条（与给光区朝向相同）能诱发最强反应，反之则抑制。
	 * 
	 * 2．复杂细胞。给光区和撤光区重叠，反应具有非线性特征，空间总合不明显，自发放电强。相比简单细胞：①反应要求一定方位的线性刺激，但不管在视野中的部位如何；②当光线移过视野时，能继续激发对适当方位的线性刺激。由此，复杂细胞对于适当方位的移动的直线刺激能继续激发，可以认为它们接受大量的简单细胞输入的刺激。
	 * 》》复杂型：感受野较大，无明显光区、线条方向检测，只对朝向敏感，对位置不敏感。
	 * 
	 * 3．超复杂细胞。能从几种复杂细胞中接受兴奋性和抑制性的输入信息。反应特点同复杂细胞，也反应特殊方位的线性刺激，但有明显的端点抑制，这种刺激不能超过某种长度。
	 * 》》超复杂型：感受野的反应特征与复杂型相似，但有明显的终端抑制，即长方形的长度超过一定的限度则有抑制效应（检测端点）。
	 * 4．极高度复杂细胞。反应移过视野的边，只要是这边有一特定的宽度。有些极高度复杂细胞特别反应两个边形成的直角，这种细胞也称为角探测器。
	 * 
	 * 视皮层神经元对视觉刺激的各种静态和动态特征都具有高度选择性。一是方位/方向选择性。只有当刺激线条或边缘处在适宜的方位角并按一定的方向移动时，才表现出最大兴奋（最佳方位或最佳方向）。二是空间频率选择性。每一个视皮层细胞都有一定的空间频率调谐。在同一皮层区内，不同细胞也有不同的空间频率调谐。三是速度选择性。对移动图形的反应比对静止的闪烁图形要强得多。而且对某一最佳速度的反应最大，移动速度高于或低于这一速度时，反应都会减小。四是双眼视差选择性。与外膝体细胞不同，大部分视皮层细胞接受双眼输入，在左、右视网膜上分别有一个感受野。这一对感受野在视网膜上的位置差（相对于注视点）称为“视差”（disparity）。根据视差可判断该细胞的调谐距离，从而形成深度视觉。五是颜色选择性。与外膝体细胞一样，皮层细胞也具有颜色选择性。与皮层下的单颉顽式感受野不同，视皮层的颜色感受野具有双颉顽式结构。例如对于R-G型感受野，其颜色结构可能有两种形式：感受野中心可能被绿视锥细胞的输入兴奋，同时被红视锥细胞输入抑制，或者相反；外周对颜色的反应性质正好与中心相反。因此，该细胞通过感受野中心的颜色颉顽能分辨红色和绿色，通过中心与外周的相互作用能使红—绿对比的边缘得到增强。
	 *
	 * <Point>
	 * 在大脑视觉皮层中，具有相同视功能特性（相同图像特征选择性和相同感受野）的皮层细胞，以垂直于大脑表面的方式排列称柱状结构，被称为视皮层功能柱。大体有两种功能柱理论，即特征提取功能柱和空间频率功能柱。特征提取功能柱包括：方位柱、眼优势柱、颜色柱等。
	 * 
	 * <Point> 研究表明，功能柱系统正好与各种特征检测功能一一对应。所有功能柱都垂直于皮层表面，排列成片层状。
	 * 
	 * 1. 方位柱。位于17区和18区。细胞的敏感方位总是很有规律地按顺时针或逆时针方向变化。
	 * 
	 * 2. 眼优势柱。左眼优势细胞与右眼优势细胞通过一定的间隔交替出现。
	 * 
	 * 3. 空间频率柱。皮层细胞的最佳空间频率也是有规则地以柱的形式垂直于皮层表层排列。试验证明，猫皮层17区存在该结构。
	 * 
	 * 4. 颜色柱。试验发现，有颜色特异性的细胞和没有颜色特异性的细胞成串交替出现。同一柱内所有的细胞具有相同的光谱特性。
	 * 
	 * 猕猴大脑皮层由约109个神经元组成，这些神经元构成了约105个功能柱结构，每个功能柱结构中包含了约104个神经元。先前的解剖学证据表明，某一个功能柱内的神经元（这些神经元具有相似的功能特异性，例如偏好某一颜色、朝向、运动方向、深度信息等）倾向于与其他具有相同功能特性的功能柱产生连接。
	 * 
	 * <Point> http://www.ziint.zju.edu.cn/index.php/event/details.html?tid=415
	 */
	public void functionColumn() {

		// 功能柱，是卷积？
	}

	public static double calculateContrast(BufferedImage image) {
		// 计算亮度的最大值和最小值
		int[] minMax = findMinMax(image);
		int minBrightness = minMax[0];
		int maxBrightness = minMax[1];

		// 计算对比度
		double contrast = maxBrightness - minBrightness;

		return contrast;
	}

	private static int[] findMinMax(BufferedImage image) {
		int minBrightness = Integer.MAX_VALUE;
		int maxBrightness = Integer.MIN_VALUE;

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				int brightness = (rgb >> 16) & 0xFF; // 取红色分量作为亮度
				minBrightness = Math.min(minBrightness, brightness);
				maxBrightness = Math.max(maxBrightness, brightness);
			}
		}

		return new int[] { minBrightness, maxBrightness };
	}

	public static void main(String[] args) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File("D:/file/05.jpg"));

//		 BufferedImage resizedImage = new BufferedImage(bufferedImage.getWidth()  * 2 / 3, bufferedImage.getHeight() * 2 / 3 , bufferedImage.getType());
//		 
//         // 绘制并缩放原图到新图片
//         Graphics g2d = resizedImage.createGraphics();
//         g2d.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth() * 2 / 3, bufferedImage.getHeight()  * 2 / 3, null);
//         g2d.dispose();
//         ImageIO.write(resizedImage, "jpg", new File("D:/file/05-resized.jpg"));

		long t0 = System.currentTimeMillis();
		BufferedImage result = show(bufferedImage);

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Edge, cost: " + (t1 - t0) + "ms.");
		FileOutputStream output = new FileOutputStream(new File("D:/file/05-edge.jpg"));

		ImageIO.write(result, "jpg", output);
		output.flush();
		output.close();

		// TODO 将亮暗边界的视野扩大，扩大到整个视野，将小视野的明暗边界连接为更大视野的明暗边界。

		// TODO V1到V2 明暗边界到物体形状，是拼凑的吗？
		// 非经典感受野的意义？

	}

}
