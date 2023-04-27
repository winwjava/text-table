package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * V1区的色觉细胞集群形成一种"斑块"结构并位于眼优势柱中心，主要位于第2、3层(也有位于第5、6层)，这种斑块结构形成了V1区标志性的纹状表征。
 * <p>
 * V1区的初级视觉信号还需要传到其他脑区进行进一步的联合处理后形成其他与视觉相关的复杂感觉，这些脑区被称为视觉联合区(V2~V5，其中V5又称为MT区即中颞区)。V2区与V1区是这些视觉联合区中唯一能在形态学意义上区分的，V2区表面为大细胞深层为粗斜有髓纤维形成标志性的条纹，V1区的标志则是色觉细胞形成的斑块。V1区4Cβ层的信号传至第2、3层后，斑块区的信号外送至V2区的细条纹区，斑块间区的信号外送至V2区的浅条纹区，V1区4B和4Cα层大细胞的信号则传至V2区的粗条纹区。细条纹区与色觉相关，浅条纹区与形状的感知(形觉)有关，粗条纹区与运动视觉和立体视觉有关。从V2区发出的信号再分别传送至其他脑区进行更多更复杂的处理，其中细条纹区和浅条纹区与V4区有联结，粗条纹区与V5区有联结。这些视觉联合皮层出现损伤都会导致人所感知到的视觉的功能缺失，如V4区损伤可能导致色觉全部丧失，而V5区的损伤则对应着选择性的、特定方向上运动觉和深度知觉的受损或缺失。
 * 链接：https://www.zhihu.com/question/406919670/answer/1410089852
 * <p>
 * 在视网膜中神经节细胞分为两类，小细胞（也称侏儒节细胞，midget cells)和大细胞（伞状节细胞，parasol
 * cells）。他们的形态不一样，midget cells尺寸很小，突触短；而parasol
 * cells尺寸大，神经突出长，如一个降落伞的形状。这些细胞对应了相应的大小的反应野。大细胞具有很大的感受野，小细胞的感受野相对小了很多。两种细胞在尺寸、感受野和反应速度上都各不相同。大细胞的尺寸大（3倍midget），反应野大，反应速度快。
 * <p>
 * Midget and parasol systems分别对应了parvo cellular 和 magno
 * cellular两个通路。这是视觉信息传输的最主要两个通道。两个通道对相应视觉信息的处理和信息传输，最终决定了眼睛对各类视觉信息的感知功能。
 * <p>
 * 在解剖学上分析，可以看到大小细胞的突触分别连接了不同数量的的视网膜感光细胞和双极细胞。Midget细胞和红、黄及蓝/黄拮抗系统连接，接收单个视锥信号。在视网膜中央，感受野接受到小细胞信号并传输至外膝体（LGN）。Pararol细胞与多个视锥细胞通过双极性细胞形成连接，在中央和周边都能接受到视锥和视杆信号的输入。在这样“聪明”的wiring模式下，Midget
 * 和Parasol的反应野形成了层层重叠。midget
 * 存在典型的on-off的反应，而parasol对瞬间改变的光有快速的短脉冲反应。在暗视觉状态下，由于连接更多的rods，大细胞的感受野变得更大。
 * <p>
 * 视觉系统中，大细胞损伤影响运动和频闪的探测；小细胞损伤严重影响对颜色、材质、图案、精细的形状、对比度和立体视的感知。视亮度和暗视觉接受两个系统的信号，单独破坏其中管一个系统，相应视觉功能不受影响。
 * <p>
 * 总体而言，对于空间频率的探测功能，小细胞的系统更为强大；并包括了颜色和细节的信息。对于时间频率的探测，大细胞系统具有更为强大的功能。
 * 
 * <p>
 * 参考：https://wap.sciencenet.cn/blog-1197804-1335303.html
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
	public static BufferedImage brightnessReceptiveField(BufferedImage image) {

		BufferedImage blurImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);
		for (int i = image.getMinX(); i < image.getWidth(); i++) {
			for (int j = image.getMinY(); j < image.getHeight(); j++) {
				blurImage.setRGB(i, j, image.getRGB(i, j));// 滤波处理
//				blurImage.setRGB(i, j, getAVEColor(i, j, image));// 滤波处理
			}
		}

		BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);
		for (int i = image.getMinX(); i < image.getWidth(); i++) {
			for (int j = image.getMinY(); j < image.getHeight(); j++) {
				resultImage.setRGB(i, j, blurImage.getRGB(i, j));
			}
		}

		int[][] finished = new int[image.getWidth()][image.getHeight()];

		// 斑块数据，值是VisualBlobColor，则表示这个点周围组成了斑块
		VisualBlobColor[][] blobs = new VisualBlobColor[image.getWidth()][image.getHeight()];

//		LinkedHashMap<int[][], Integer> blobMap = new LinkedHashMap<int[][], Integer>();
		for (int i = image.getMinX() + radius + 1; i < image.getWidth() - radius - 1; i++) {
			for (int j = image.getMinY() + radius + 1; j < image.getHeight() - radius - 1; j++) {
				// 循环X和Y坐标，逐个像素比较。
				if (finished[i][j] <= 0) {
					int[][] blob = brightnessReceptiveField(resultImage, image, i, j, radius, finished);
					blobs[i][j] = new VisualBlobColor(i, j, blob, blurImage.getRGB(i, j));

//					System.out.println(i + ", " + j + ": " + blurImage.getRGB(i, j));

				}
			}
		}

		int total = 0, notnull = 0;
		// TODO 将斑块区 合并为条纹区
		// 相似颜色，并且相邻区域则合并成更大的区域。
		for (int m = 0; m < blobs.length; m++) {
			for (int n = 0; n < blobs[m].length; n++) {
				total++;
				VisualBlobColor colorBlob = blobs[m][n];
				if (colorBlob != null) {// 找到一个中心点
					notnull++;
//					System.out.println(colorBlob.getX0() + ", " + colorBlob.getY0() + ": " + colorBlob.getColor());

					int[][] blob = colorBlob.getBlob();

					for (int j = 0; j < blob.length; j++) {// 从左向右，找到左边界
						for (int k = 0; k < blob[j].length; k++) {// 从上往下找到边界。
							if (blob[j][k] == 1) {// 说明是边界，则向左查找合并相同颜色区域。
								// 向左查找
								for (int x = j; m - x > 0; x++) {
//									System.out.println(image.getHeight() +", getHeight: "+(n + k));
									VisualBlobColor neighbor = blobs[m - x][n - radius + k];// Y轴不变 - radius + k

//									System.out.println(image.getWidth() +", "+m + x);
									if (neighbor != null && rgbSimilar(colorBlob.getColor(), neighbor.getColor())) {
										// 相邻，并且颜色相似，可以合并。
										System.out.println("相邻，并且颜色相似，可以合并。");

										// 向左合并
										int[][] mergeBlob = mergeBlob(neighbor.getX0(), neighbor.getY0(),
												neighbor.getBlob(), m, n, blob);
										neighbor.setBlob(mergeBlob);
										blobs[m][n] = null;
									}
								}
							}
						}
					}
				}

				// 向右扫描 1个感受野半径

				// 从左向右，从上向下 遍历。
				// 把所有
				// 向右合并？向下合并
			}
		}
		System.out.println(total + ", notnull: " + notnull);

		for (int j = 0; j < blobs.length; j++) {
			for (int k = 0; k < blobs[j].length; k++) {

				VisualBlobColor colorBlob = blobs[j][k];
				if (colorBlob != null) {// 找到一个中心点

					int[][] blob = colorBlob.getBlob();
					Color randomColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
					for (int m = 0; m < blob.length; m++) {
						for (int n = 0; n < blob[m].length; n++) {
							if (blob[m][n] > 0) {
//								resultImage.setRGB(x0 - radius + m, y0 - radius + n, rgb);
								resultImage.setRGB(colorBlob.getX0() - radius + m, colorBlob.getY0() - radius + n,
										randomColor.getRGB());
							}
						}
					}

				}
			}
		}

		return resultImage;
	}

	public static int[][] mergeBlob(int x0, int y0, int[][] blob, int rightX0, int rightY0, int[][] rightBlob) {
		// 合并到右边
		// 总宽度，总高度
		// 中心点是（radius，radius）

		// TODO 不规则区域，需要重新计算，中心点不一定在中心，需要看中心点的上下左右分别有多少像素。
		int xLength = radius + Math.abs(rightX0 - x0) + radius + 1;
		int yLength = radius + Math.abs(rightY0 - y0) + radius + 1;

		int[][] merged = new int[xLength][yLength];

		// 将blob复制进来
		for (int m = 0; m < blob.length; m++) {
			for (int n = 0; n < blob[m].length; n++) {
				if (blob[m][n] > 0) {
					merged[m][n] = blob[m][n];
				}
			}
		}

		// 将右边的blob复制进来
		for (int m = 0; m < rightBlob.length; m++) {
			for (int n = 0; n < rightBlob[m].length; n++) {
				if (rightBlob[m][n] > 0) {
					try {
						merged[rightX0 - x0 + m][rightY0 - y0 + n] = rightBlob[m][n];
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(m + ", " + n + " ->" + (rightX0 - x0 + m) + "," + (rightY0 - y0 + n));
						System.out.println(rightY0 +" : "+y0);
						throw e;
					}
				}
			}
		}
		return merged;
	}

	public static int[][] brightnessReceptiveField(BufferedImage resultImage, BufferedImage blurImage, int x0, int y0,
			int radius, int[][] finished) {// 视网膜上的视锥细胞只是采集了颜色。

		// 斑块（Blobs）由若干个不等长的长条组合而成。
		// 类似卷积的方式。

		// 以感受野中心，向四周扩散到每一度空间的最大范围，并标出这个颜色。

		int rgb = blurImage.getRGB(x0, y0);// 中心
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
		int[][] blob = new int[radius + radius][radius + radius];// 中心点是（radius，radius）

		// 向4个象限扩大。
		// 第一象限，右上
		boolean quit = false;
		quit = false;
		for (int m = 0; m < radius; m++) {
			for (int n = 0; n < radius; n++) {
				if (rgbSimilar(blurImage.getRGB(x0 + m, y0 - n), r, g, b)) {

					finished[x0 + m][y0 - n] = 1;// 已经检测
					blob[radius + m][radius - n] = 1;// 与中心点颜色相同
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
				if (rgbSimilar(blurImage.getRGB(x0 - m, y0 - n), r, g, b)) {
					finished[x0 - m][y0 - n] = 1;// 已经检测
					blob[radius - m][radius - n] = 1;// 与中心点颜色相同
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
				if (rgbSimilar(blurImage.getRGB(x0 - m, y0 + n), r, g, b)) {
					finished[x0 - m][y0 + n] = 1;// 已经检测
					blob[radius - m][radius + n] = 1;// 与中心点颜色相同
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
				if (rgbSimilar(blurImage.getRGB(x0 + m, y0 + n), r, g, b)) {
					finished[x0 + m][y0 + n] = 1;// 已经检测
					blob[radius + m][radius + n] = 1;// 与中心点颜色相同
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

		// TODO 考虑颜色饱和度（饱和度取决于该色中含色成分和消色成分（灰色）的比例，含色成分越大，饱和度越大；）
		// 各种单色光是最饱和的色彩，对于人的视觉，每种色彩的饱和度可分为20个可分辨等级。
		// 纯的颜色都是高度饱和的，如鲜红，鲜绿。混杂上白色，灰色或其他色调的颜色，是不饱和的颜色，如绛紫，粉红，黄褐等。完全不饱和的颜色根本没有色调，如黑白之间的各种灰色。

		if (rgb == 0 || rgbSimilar(rgb, 255, 255, 255)) {// 透明或白色
//			System.out.println(x0 + ", " + y0 + ": " + rgb);
//			resultImage.setRGB(x0, y0, -66055);
		} else {
//			Color randomColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
//			for (int m = 0; m < blob.length; m++) {
//				for (int n = 0; n < blob[m].length; n++) {
//					if (blob[m][n] > 0) {
//						resultImage.setRGB(x0 - radius + m, y0 - radius + n, randomColor.getRGB());
//					}
//				}
//			}
		}
		return blob;
	}

	/**
	 * 中值滤波
	 * <p>
	 * 取滤波器的各个像素点的中值。如3*3的滤波器就取排列后的第5个数
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

	public static boolean rgbSimilar(int rgb, int color) {
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
		return rgbSimilar(color, r, g, b);
	}

	public static boolean rgbSimilar(int rgb, int r0, int g0, int b0) {
		int r = (rgb & 0xff0000) >> 16;
		int g = (rgb & 0xff00) >> 8;
		int b = (rgb & 0xff);
		return Math.abs(r - r0) < RANGE && Math.abs(g - g0) < RANGE && Math.abs(b - b0) < RANGE;
	}

	static Random RANDOM = new Random();
	/**
	 * 像素RGB值大于30，则认为是边缘。
	 */
	public static int RANGE = 10;// 像素梯度，当前感受野存在亮度差异。黑暗环境下对比度小。

	public static int radius = 10;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	public static void main(String[] args) throws IOException {

		File file = new File("E:\\ww.png");
		BufferedImage image = ImageIO.read(file);

		BufferedImage bufferedImage = brightnessReceptiveField(image);

		FileOutputStream ops = new FileOutputStream(new File("E:\\blob-ww.png"));
		ImageIO.write(bufferedImage, "png", ops);
		ops.flush();
		ops.close();
	}

}
