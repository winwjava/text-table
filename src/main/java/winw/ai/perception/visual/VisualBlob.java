package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * 颜色块，从表面亮度分块（经过马赫带亮度处理），然后标记颜色。
 * 
 * V1区的色觉细胞集群形成一种"斑块"结构并位于眼优势柱中心，主要位于第2、3层(也有位于第5、6层)，这种斑块结构形成了V1区标志性的纹状表征。
 * <Point>
 * V1区的初级视觉信号还需要传到其他脑区进行进一步的联合处理后形成其他与视觉相关的复杂感觉，这些脑区被称为视觉联合区(V2~V5，其中V5又称为MT区即中颞区)。V2区与V1区是这些视觉联合区中唯一能在形态学意义上区分的，V2区表面为大细胞深层为粗斜有髓纤维形成标志性的条纹，V1区的标志则是色觉细胞形成的斑块。V1区4Cβ层的信号传至第2、3层后，斑块区的信号外送至V2区的细条纹区，斑块间区的信号外送至V2区的浅条纹区，V1区4B和4Cα层大细胞的信号则传至V2区的粗条纹区。细条纹区与色觉相关，浅条纹区与形状的感知(形觉)有关，粗条纹区与运动视觉和立体视觉有关。从V2区发出的信号再分别传送至其他脑区进行更多更复杂的处理，其中细条纹区和浅条纹区与V4区有联结，粗条纹区与V5区有联结。这些视觉联合皮层出现损伤都会导致人所感知到的视觉的功能缺失，如V4区损伤可能导致色觉全部丧失，而V5区的损伤则对应着选择性的、特定方向上运动觉和深度知觉的受损或缺失。
 * 链接：https://www.zhihu.com/question/406919670/answer/1410089852 <Point>
 * 在视网膜中神经节细胞分为两类，小细胞（也称侏儒节细胞，midget cells)和大细胞（伞状节细胞，parasol
 * cells）。他们的形态不一样，midget cells尺寸很小，突触短；而parasol
 * cells尺寸大，神经突出长，如一个降落伞的形状。这些细胞对应了相应的大小的反应野。大细胞具有很大的感受野，小细胞的感受野相对小了很多。两种细胞在尺寸、感受野和反应速度上都各不相同。大细胞的尺寸大（3倍midget），反应野大，反应速度快。
 * <Point> Midget and parasol systems分别对应了parvo cellular 和 magno
 * cellular两个通路。这是视觉信息传输的最主要两个通道。两个通道对相应视觉信息的处理和信息传输，最终决定了眼睛对各类视觉信息的感知功能。
 * <Point>
 * 在解剖学上分析，可以看到大小细胞的突触分别连接了不同数量的的视网膜感光细胞和双极细胞。Midget细胞和红、黄及蓝/黄拮抗系统连接，接收单个视锥信号。在视网膜中央，感受野接受到小细胞信号并传输至外膝体（LGN）。Pararol细胞与多个视锥细胞通过双极性细胞形成连接，在中央和周边都能接受到视锥和视杆信号的输入。在这样“聪明”的wiring模式下，Midget
 * 和Parasol的反应野形成了层层重叠。midget
 * 存在典型的on-off的反应，而parasol对瞬间改变的光有快速的短脉冲反应。在暗视觉状态下，由于连接更多的rods，大细胞的感受野变得更大。
 * <Point>
 * 视觉系统中，大细胞损伤影响运动和频闪的探测；小细胞损伤严重影响对颜色、材质、图案、精细的形状、对比度和立体视的感知。视亮度和暗视觉接受两个系统的信号，单独破坏其中管一个系统，相应视觉功能不受影响。
 * <Point> 总体而言，对于空间频率的探测功能，小细胞的系统更为强大；并包括了颜色和细节的信息。对于时间频率的探测，大细胞系统具有更为强大的功能。
 * 
 * <Point> 参考：https://wap.sciencenet.cn/blog-1197804-1335303.html
 * 
 * @author winw
 *
 */
public class VisualBlob {// 颜色：侏儒神经节细胞（或称小细胞，midget）接收单个视锥信号， 然后整合到 V1 斑块区，整合为 V2区的细条纹区

	// 从中间点开始遍历，向四周扩散，将相同颜色的区域分割为斑块。斑块的大小应该是小型的。

	// 600-700万个视锥细胞，可以检测700万个像素点，每个感受野（1度空间频率，100个像素点）一个斑块。再用斑块整合成为条纹，条纹再整合为区域。

	// 只需要很少的颜色采样即可。

	/**
	 * 区域汇聚：根据亮度、颜色聚合为一个一个区域。
	 */
	public void areaCluster() {// 符合梯度
		// 将相同亮度、颜色的区域聚类。多层、深度计算。第一层做简单计算，只分析附近几个像素。
		// 使用 Kmeans聚类实现颜色的分割
		// https://blog.csdn.net/weixin_39683025/article/details/114094828
	}

	/**
	 * 
	 * 视网膜感受野。
	 * 
	 * 分为明暗感受野、颜色感受野。
	 * 
	 * @param image
	 * @return
	 */
	public static List<BlobArea> colorReceptiveField(BufferedImage resultImage, BufferedImage image) {
		// 转换为灰度图像
//		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
//		BufferedImage grayImage = op.filter(image, null);

		int[][] grayImage = new int[image.getWidth()][image.getHeight()];// 不需要做灰度处理，不需要做边缘增强，可以做模糊处理。

//		BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);
		for (int i = image.getMinX(); i < image.getWidth(); i++) {
			for (int j = image.getMinY(); j < image.getHeight(); j++) {
				resultImage.setRGB(i, j, image.getRGB(i, j));
				grayImage[i][j] = image.getRGB(i, j);
			}
		}

		// 需要定义感受野半径
//		int[][] grayImage = VisualEdge.brightnessReceptiveField(image);// 灰度处理，边缘增强，返回二值化二维数组，存储亮度0~255
//		LineGrid[][] lineGridArray = VisualEdge.edgeReceptiveField(resultImage, grayImage);// 在V1或V2，线条感受野，当两个有交集时，可以合并。

//		grayImage = VisualEdge.brightnessReceptiveField(resultImage);
		int[][] finished = new int[image.getWidth()][image.getHeight()];

		// 斑块数据，值是VisualBlobColor，则表示这个点周围组成了斑块
		Blob[][] blobs = new Blob[image.getWidth()][image.getHeight()];
//		HashMap<Integer, List<Blob>> blobColumn = new HashMap<>();// 颜色功能柱

//		LinkedHashMap<int[][], Integer> blobMap = new LinkedHashMap<int[][], Integer>();
		for (int i = image.getMinX() + radius + 1; i < image.getWidth() - radius - 1; i++) {
			for (int j = image.getMinY() + radius + 1; j < image.getHeight() - radius - 1; j++) {
				// 循环X和Y坐标，逐个像素比较。
				if (finished[i][j] <= 0) {
					colorReceptiveField(image, grayImage, blobs, i, j, radius, finished);// , blobColumn
//					blobs[i][j] = new VisualBlobColor(i, j, radius, radius, blob, blurImage.getRGB(i, j));
				}
			}
		}

//		System.out.println("color total : " + blobColumn.size());

		// 将颜色聚类：很多颜色是相近的、渐变的，怎么划分这些颜色

//		HashMap<Integer, Integer> colorMerge = new HashMap<>();// 颜色合并，
//		for (Integer color : blobColumn.keySet()) {
//
//			// 颜色选择性，找到相近的颜色
//			int drawColor = -1;
//			for (int c : colorMerge.keySet()) {// 可能与多个颜色相近
//				if (rgbSimilar(color, c, 25)) {
//					drawColor = colorMerge.get(c);
//				}
//			}
//			if (drawColor == -1) {
//				drawColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat()).getRGB();
//				colorMerge.put(color, drawColor);
//			}
//
//			for (Blob tempBlob : blobColumn.get(color)) {
//				int[][] blobArray = tempBlob.getBlob();
//				for (int m = 0; m < blobArray.length; m++) {
//					for (int n = 0; n < blobArray[m].length; n++) {
//						if (blobArray[m][n] > 0) {
//							resultImage.setRGB(tempBlob.getX0() - radius + m, tempBlob.getY0() - radius + n, drawColor);
//						}
//					}
//				}
//			}
//		}

		// 颜色柱：blobColumn

		// 颜色合并，应当用根据颜色聚类，将邻居分块

		// TODO 把颜色感受野合并为斑块区：相似颜色，并且相邻区域则合并成更大的区域。

		
		// FIXME degree 是空间频率，是一个很重要的参数。
		int degree = 36;// 把视野分为20*20份，每一个空间频率里去分辨合并颜色；
		
		List<BlobArea> blobList =  new ArrayList<BlobArea>();

		Set<Blob> repeatSet = new HashSet<Blob>();// 已经被合并过
		for (int m = 0; m < blobs.length; m += degree) {
			for (int n = 0; n < blobs[0].length; n += degree) {
				Blob blob = blobs[m][n];

				if (blob == null) {
					continue;
				}

				// 用任意一个Blob，查找相邻并且颜色相似的Blob，并用这个相邻的Blob再去找下一个相邻的
				Set<Blob> blobSet = new HashSet<Blob>();
				List<Blob> waitSet = new ArrayList<Blob>();
				waitSet.add(blob);
				while (waitSet.size() > 0) {// 没有用递归是因为递归会导致栈溢出
					Blob temp = waitSet.remove(0);
					if (temp != null && !repeatSet.contains(temp)) {
						blobSet.add(temp);
						repeatSet.add(temp);
						searchNeighborBlob(grayImage, blobs, repeatSet, blobSet, waitSet, temp);
					}
				}
				if(blobSet.size() > 0) {
					blobList.add(new BlobArea(blobSet));
				}

			}
		}

		// FIXME 用颜色卷积?

		// 还需要再做一次合并，有一些小的Blob没有被合并进去。

		// 有一些Blob被漏掉了，在上下左右找到时候，漏掉了，被分割了。

		for (BlobArea blobArea : blobList) {
			Color randomColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());

			for (int m = 0; m < blobArea.getBlob().length; m++) {
				for (int n = 0; n < blobArea.getBlob()[m].length; n++) {
					if (blobArea.getBlob()[m][n] > 0) {
						resultImage.setRGB(blobArea.getMinX() + m, blobArea.getMinY() + n,
								randomColor.getRGB());
					}
				}
			}
			
//			for (Blob colorBlob : blobArea.getBlobSet()) {
//				int[][] blobArray = colorBlob.getBlob();
//				for (int m = 0; m < blobArray.length; m++) {
//					for (int n = 0; n < blobArray[m].length; n++) {
//						if (blobArray[m][n] > 0) {
//							resultImage.setRGB(colorBlob.getX0() - radius + m, colorBlob.getY0() - radius + n,
//									randomColor.getRGB());
//						}
//					}
//				}
//			}
		}

		return blobList;
	}

	private static void searchNeighborBlob(int[][] grayImage, Blob[][] blobs, Set<Blob> repeatSet, Set<Blob> blobSet,
			List<Blob> waitSet, Blob colorBlob) {

		int[][] blob = colorBlob.getBlob();
		for (int x = 0; x < blob.length; x++) {
			for (int y = 0; y < blob[0].length; y++) {
				Blob neighbor = null;
				int neighborEdgeColor = 0;
				if (x == radius && y == radius) {
				} else if (x == 0 && blob[x][y] == 1 && colorBlob.getX0() - radius + x - 1 >= 0) {// 左边界
					neighbor = blobs[colorBlob.getX0() - radius + x - 1][colorBlob.getY0() - radius + y];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x - 1][colorBlob.getY0() - radius + y];

					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				} else if (x == blob.length - 1 && blob[x][y] == 1
						&& colorBlob.getX0() - radius + x + 2 < blobs.length) {// 右边界
					neighbor = blobs[colorBlob.getX0() - radius + x + 1][colorBlob.getY0() - radius + y];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x + 1][colorBlob.getY0() - radius + y];
					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				} else if (y == 0 && blob[x][y] == 1 && colorBlob.getY0() - radius + y - 1 > 0) {// 上边界
					neighbor = blobs[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y - 1];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y - 1];
					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				} else if (y == blob[0].length - 1 && blob[x][y] == 1
						&& colorBlob.getY0() - radius + y + 1 > blobs[0].length) {// 下边界
					neighbor = blobs[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y + 1];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y + 1];
					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				} else if (x - 1 > 0 && blob[x][y] == 0 && blob[x - 1][y] == 1) {// 右边界
					neighbor = blobs[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				} else if (x + 1 < blob.length && blob[x][y] == 0 && blob[x + 1][y] == 1) {// 左边界
					neighbor = blobs[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				} else if (y - 1 > 0 && blob[x][y] == 0 && blob[x][y - 1] == 1) {// 上边界
					neighbor = blobs[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				} else if (y + 1 < blob[0].length && blob[x][y] == 0 && blob[x][y + 1] == 0) {// 下边界
					neighbor = blobs[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					neighborEdgeColor = grayImage[colorBlob.getX0() - radius + x][colorBlob.getY0() - radius + y];
					addWait(waitSet, colorBlob, neighbor, repeatSet, blobSet, neighborEdgeColor);
				}
				// 在对比度比较小的地方，但不能跨区域
				// 中心点与周围像素比较，中心点与中心点比较

//				int r = (colorBlob.getColor() & 0xff0000) >> 16;
//				int g = (colorBlob.getColor() & 0xff00) >> 8;
//				int b = (colorBlob.getColor() & 0xff);
			}
		}
	}

	private static void addWait(List<Blob> waitSet, Blob colorBlob, Blob neighbor, Set<Blob> repeatSet,
			Set<Blob> blobSet, int neighborEdgeColor) {

		if (neighbor == null || neighbor == colorBlob || blobSet.contains(neighbor) || repeatSet.contains(neighbor)) {
			return;
		}

		if (rgbSimilar(neighbor.getColor(), colorBlob.getColor(), 10)
//				|| rgbSimilar(neighbor.getColor(), colorBlob.getColor(), 12) // 相邻Blob 的中心点
//				|| rgbSimilar(neighborEdgeColor, colorBlob.getColor(), 12) // 相邻Blob 的中心点
		) {// && rgbSimilar(neighborEdgeColor, r, g, b, range) 相邻Blob 边界
			waitSet.add(neighbor);// 确定合并，加入下一个待比较
		}
	}

	public static void colorReceptiveField(BufferedImage resultImage, int[][] blurImage, Blob[][] blobs, int x0, int y0,
			int radius, int[][] finished) {// 视网膜上的视锥细胞只是采集了颜色。, HashMap<Integer, List<Blob>> blobColumn

		// 斑块（Blobs）由若干个不等长的长条组合而成。
		// 类似卷积的方式。

		// 以感受野中心，向四周扩散到每一度空间的最大范围，并标出这个颜色。

		int count = 0;// 同色像素计数
		int rgb = blurImage[x0][y0];// 中心
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
//		int avg = blurImage[x0][y0];// 亮度平均值
		int[][] blob = new int[radius + radius][radius + radius];// 中心点是（radius，radius）
		// 判断一个感受野中的像素是否与中心点的颜色一致：在感受野的对比度高度情况下，把颜色范围稍微放宽，否则按照正常范围；
		// 判断两个感受野的颜色是否一致：色差在一定范围内。
//		blobColor.setBlob(blob);


		// 计算对比度，对比度高时，将RANGE调大，否则调小。
		int minBrightness = brightness(rgb);// (rgb >> 16) & 0xFF; // 取红色分量作为亮度
		int maxBrightness = brightness(rgb);

		// 计算视野中的对比度。
		for (int j = x0 - radius; j < x0 + radius; j++) {
			for (int k = y0 - radius; k < y0 + radius; k++) {
				minBrightness = Math.min(minBrightness, brightness(blurImage[j][k]));
				maxBrightness = Math.max(maxBrightness, brightness(blurImage[j][k]));
			}
		}

		int cc = maxBrightness - minBrightness;

		int range = 12;

		if (cc > 60) {
			range = 25;
		} else if (cc > 30) {
			range = 22;
		} else {
			range = 17;
		}

		// 向4个象限扩大。
		// 第一象限，右上
		boolean quit = false;
		quit = false;
		for (int m = 0; m < radius; m++) {
			for (int n = 0; n < radius; n++) {
				if (rgbSimilar(blurImage[x0 + m][y0 - n], r, g, b, range)) {
					count++;
					finished[x0 + m][y0 - n] = 1;// 已经检测
					blob[radius + m][radius - n] = 1;// 与中心点颜色相同
//					blobs[x0 + m][y0 - n] = blobColor;
				} else {// 发现色差，跳出整个循环
//					System.out.println(x0 + ", " + y0 + ": " + rgb + ", (" + m + "," + n + "): "
//							+ blurImage.getRGB(x0 + m, y0 - n));
					quit = true;
					break;
				}
			}
			if (quit) {
				break;
			}
		}
		// 第二象限，左上
		quit = false;
		for (int m = 0; m < radius; m++) {
			for (int n = 0; n < radius; n++) {
				if (rgbSimilar(blurImage[x0 - m][y0 - n], r, g, b, range)) {
					count++;
					finished[x0 - m][y0 - n] = 1;// 已经检测
					blob[radius - m][radius - n] = 1;// 与中心点颜色相同
//					blobs[x0 - m][y0 - n] = blobColor;
				} else {
//					System.out.println(x0 + ", " + y0 + ": " + rgb + ", (" + m + "," + n + "): "
//							+ blurImage.getRGB(x0 + m, y0 - n));
					quit = true;
					break;
				}
			}
			if (quit) {
				break;
			}
		}
		// 第三象限，左下
		quit = false;
		for (int m = 0; m < radius; m++) {
			for (int n = 0; n < radius; n++) {
				if (rgbSimilar(blurImage[x0 - m][y0 + n], r, g, b, range)) {
					count++;
					finished[x0 - m][y0 + n] = 1;// 已经检测
					blob[radius - m][radius + n] = 1;// 与中心点颜色相同
//					blobs[x0 - m][y0 + n] = blobColor;
				} else {
//					System.out.println(x0 + ", " + y0 + ": " + rgb + ", (" + m + "," + n + "): "
//							+ blurImage.getRGB(x0 + m, y0 - n));
					quit = true;
					break;
				}
			}
			if (quit) {
				break;
			}
		}
		// 第四象限，右下
		quit = false;
		for (int m = 0; m < radius; m++) {
			for (int n = 0; n < radius; n++) {
				if (rgbSimilar(blurImage[x0 + m][y0 + n], r, g, b, range)) {
					count++;
					finished[x0 + m][y0 + n] = 1;// 已经检测
					blob[radius + m][radius + n] = 1;// 与中心点颜色相同
//					blobs[x0 + m][y0 + n] = blobColor;
				} else {// 发现色差，跳出循环
//					System.out.println(x0 + ", " + y0 + ": " + rgb + ", (" + m + "," + n + "): "
//							+ blurImage.getRGB(x0 + m, y0 - n));
					quit = true;
					break;
				}
			}
			if (quit) {
				break;
			}
		}

		if (count <=  2 * radius * radius) {// TODO 小于5个的blob过滤掉，避免在大量非块区域形成非必要的计算
//			System.out.println("blob less than 5: " + count);
			return;
		}

//		blobColor.setMinBrightness(minBrightness);
//		blobColor.setMaxBrightness(maxBrightness);

		// 重新计算颜色

		// 取颜色平均值
//		int sumR = 0;
//		int sumG = 0;
//		int sumB = 0;
		Blob blobColor = new Blob(x0, y0, radius, radius, blurImage[x0][y0], blob);// 中心点颜色
		blobColor.setColorRange(range);
//		List<Blob> list = blobColumn.get(rgb);
//		if (list == null) {
//			list = new ArrayList<>();
//			blobColumn.put(rgb, list);
//		}
//		list.add(blobColor);
		for (int m = 0; m < blob.length; m++) {
			for (int n = 0; n < blob[m].length; n++) {
				if (blob[m][n] > 0) {
					blobs[x0 - radius + m][y0 - radius + n] = blobColor;
//					int c = blurImage[x0 - radius + m][y0 - radius + n];
//					sumR += (c & 0xff0000) >> 16;
//					sumG += (c & 0xff00) >> 8;
//					sumB += (c & 0xff);
				}
			}
		}

//		blobColor.setColor((sumR / count) << 16 + (sumG / count) << 8 + (sumB / count));

		// TODO 考虑颜色饱和度（饱和度取决于该色中含色成分和消色成分（灰色）的比例，含色成分越大，饱和度越大；）
		// 各种单色光是最饱和的色彩，对于人的视觉，每种色彩的饱和度可分为20个可分辨等级。
		// 纯的颜色都是高度饱和的，如鲜红，鲜绿。混杂上白色，灰色或其他色调的颜色，是不饱和的颜色，如绛紫，粉红，黄褐等。完全不饱和的颜色根本没有色调，如黑白之间的各种灰色。

//		if (rgb == 0 || rgbSimilar(rgb, 255, 255, 255)) {// 透明或白色
//			System.out.println(x0 + ", " + y0 + ": " + rgb);
//			resultImage.setRGB(x0, y0, -66055);
//		} else {
//			Color randomColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
//			for (int m = 0; m < blob.length; m++) {
//				for (int n = 0; n < blob[m].length; n++) {
//					if (blob[m][n] > 0) {
//						resultImage.setRGB(x0 - radius + m, y0 - radius + n, randomColor.getRGB());
//					}
//				}
//			}
//		}
	}

	/**
	 * 中值滤波 <Point> 取滤波器的各个像素点的中值。如3*3的滤波器就取排列后的第5个数
	 */
	public static int getMidColor(int x, int y, BufferedImage bi) {
		int color = 0;
		int m = 0;
		int a[] = new int[9];
		for (int i = x - 1; i <= x + 1; i++)
			for (int j = y - 1; j <= y + 1; j++) {
				color = bi.getRGB(i, j);
				a[m] = color;
				m++;
			}
		Arrays.sort(a);
		color = a[5];
		return color;
	}

	// 均值滤波，滤波器的各个像素点相加取平均
	public static int getAVEColor(int x, int y, BufferedImage bi) {
		int color = 0;
		int r = 0, g = 0, b = 0;
		for (int i = x - 1; i <= x + 1; i++)
			for (int j = y - 1; j <= y + 1; j++) {
				color = bi.getRGB(i, j);
				r += (color >> 16) & 0xff;
				g += (color >> 8) & 0xff;
				b += color & 0xff;
			}
		int ia = 0xff;
		int ir = (int) (r / 9);
		int ig = (int) (g / 9);
		int ib = (int) (b / 9);
		color = (ia << 24) | (ir << 16) | (ig << 8) | ib;
		return color;
	}

	public static int brightness(int rgb) {
		// 亮度公式 Brightness = ((R*299)+(G*587)+(B*114))/1000
		return ((((rgb & 0xff0000) >> 16) * 299) + (((rgb & 0xff00) >> 8) * 587) + ((rgb & 0xff) * 114)) / 1000;
	}

	public static int brightness(int r, int g, int b) {
		// 亮度公式 Brightness = ((R*299)+(G*587)+(B*114))/1000
		return ((r * 299) + (g * 587) + (b * 114)) / 1000;
	}

	/**
	 * 高斯滤波
	 * 
	 * @param image
	 * @param g
	 * @return
	 */
	protected static int[][] filteringGaussian(BufferedImage image, double g) {
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

	/**
	 * luminance(r,g,b)是将RGB颜色空间下的颜色值转换为亮度值，采用的是sRGB彩色空间中的标准转换公式。对于每个颜色通道的值r、g、b，首先将它们归一化到0~1的范围内，然后根据其大小关系，采用不同的公式进行转换。最终将R、G、B三个通道的亮度值按一定的比例相加，得到一个灰度值，表示该像素的亮度信息。
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @return
	 */
	public static double luminanace(int r, int g, int b) {
		double[] a = { r / 255.0, g / 255.0, b / 255.0 };
		for (int i = 0; i < a.length; i++) {
			a[i] = a[i] <= 0.03928 ? a[i] / 12.92 : Math.pow((a[i] + 0.055) / 1.055, 2.4);
		}
		return a[0] * 0.2126 + a[1] * 0.7152 + a[2] * 0.0722;
	}

	/**
	 * 计算颜色对比度的公式，即将两个像素的亮度值进行比较，得到最亮的和最暗的像素的亮度值，并计算它们的比值，用来表示颜色对比度的强弱程度。其中，为了防止计算过程中出现0的情况，加上了一个较小的常数0.05作为平滑处理。
	 * 
	 * @param image
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getContrast(BufferedImage image, int x1, int y1, int x2, int y2) {
		int pixel1 = image.getRGB(x1, y1);
		int r1 = (pixel1 & 0xff0000) >> 16;
		int g1 = (pixel1 & 0xff00) >> 8;
		int b1 = (pixel1 & 0xff);

		int pixel2 = image.getRGB(x2, y2);
		int r2 = (pixel2 & 0xff0000) >> 16;
		int g2 = (pixel2 & 0xff00) >> 8;
		int b2 = (pixel2 & 0xff);

		double lum1 = luminanace(r1, g1, b1);
		double lum2 = luminanace(r2, g2, b2);

		double brightest = Math.max(lum1, lum2);
		double darkest = Math.min(lum1, lum2);

		return (brightest + 0.05) / (darkest + 0.05);
	}

	private static int getGray(int pixel) {
		return pixel & 0xff;
	}

//	public static boolean hsbSimilar(int rgb, int color) {// TODO 应该改为HSV
//		// 色调（H），饱和度（S），亮度（V）
//		float[] hsb = Color.RGBtoHSB((rgb & 0xff0000) >> 16, (rgb & 0xff00) >> 8, rgb & 0xff, null);
//		float[] hsb2 = Color.RGBtoHSB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, color & 0xff, null);
//		return true;
//	}

	public static boolean contrastSimilar(int rgb, int r0, int g0, int b0) {

		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
		double lum1 = luminanace(r, g, b);
		double lum2 = luminanace(r0, g0, b0);
		return Math.abs(lum1 - lum2) < 100;
//		return (Math.max(lum1, lum2) + 0.001) / (Math.min(lum1, lum2) + 0.001) < 1.09;
	}

	public static boolean rgbSimilar(int rgb, int color, int contrast) {
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
		return rgbSimilar(color, r, g, b, contrast);
	}

//	public static boolean contrastSimilar(int rgb, int r0, int g0, int b0) {
//		return Math.abs(rgb & 0xFF - b0 & 0xFF) < RANGE;
//	}

	public static boolean hsvSimilar(int hsv, int avg) {// TODO 应当与平均值比较，还应当与相邻的像素比较（渐变）
//		return contrastSimilar(rgb, r0, g0, b0);
		return Math.abs(hsv - avg) < 10;
//		return (Math.max(hsv, avg) + 0.001) / (Math.min(hsv, avg) + 0.001) < 1.05;
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

	static Random RANDOM = new Random();
	/**
	 * 像素RGB值大于30，则认为是边缘。
	 * 
	 * TODO 对比度，在对比度高的地方，梯度应该小。
	 */
	public static int RANGE = 12;// 像素梯度，当前感受野存在亮度差异。黑暗环境下对比度小。

	public static int radius = 3;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	public static void main(String[] args) throws IOException {
		String im = "D:/file/05.jpg";
		String ot = "D:/file/05-blob.jpg";
		

//		String im = "D:/file/IMG_20150222_090728.jpg";
//		String ot = "D:/file/temp/IMG_20150222_090728-blob.jpg";
		
		long t0 = System.currentTimeMillis();
		BufferedImage image = ImageIO.read(new File(im));
		FileOutputStream output = new FileOutputStream(new File(ot));

		BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		VisualBlob.colorReceptiveField(resultImage, image);
		
		ImageIO.write(resultImage, "jpg", output);
		long t1 = System.currentTimeMillis();
		System.out.println("Visual Blob, cost: " + (t1 - t0) + "ms.");

		output.flush();
		output.close();

		// TODO 从表面亮度分块（经过马赫带亮度处理），然后标记颜色。
		// TODO 如果两个blob之间差异不大，但存在边缘，则强化这个边缘两侧blob的对比度。
	}

}
