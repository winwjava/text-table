package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * 视觉感受野
 * 
 * <p>
 * 视网膜上接受光信号的细胞叫Photoreceptor（感光细胞），并且分为两种类型，Rods（视杆细胞）和Cones（视锥细胞）。
 * Rods只有在非常低的光照下才启动，Cones在较亮的光照下工作，并且获得颜色信息。Cones的大小在1000nm左右，很接近
 * 最大可见波长（这暗示着人类可见光最大波长，或者说Cones不能再小了）。每个人的视网膜上包含120 million的Rods和6
 * million的Cones，但千万不要认为人感知颜色的能力就弱于感知亮度的能力。因为6 million
 * Cones绝大部分集中在Fovea（中央窝），我们可以通过转动眼睛将中央窝对准感兴趣的区域。
 * 
 * 根据亮度、颜色、划分区域。
 * 
 * <p>
 * 亮度区域、亮度边缘
 * 
 * <p>
 * TODO 马赫带
 * 
 * <p>
 * https://zhuanlan.zhihu.com/p/20579210 https://zhuanlan.zhihu.com/p/186999395
 * https://zhuanlan.zhihu.com/p/150301718
 * https://foundationsofvision.stanford.edu/chapter-5-the-retinal-representation/
 * https://foundationsofvision.stanford.edu/chapter-10-motion-and-depth/
 * https://foundationsofvision.stanford.edu/chapter-9-color/
 * 
 * @author winw
 *
 */
public class VisualEdge {

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

		BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);

		for (int i = image.getMinX(); i < image.getWidth(); i++) {
			for (int j = image.getMinY(); j < image.getHeight(); j++) {
				grayImage.setRGB(i, j, gray(image.getRGB(i, j)));// 灰度处理
			}
		}

		BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);
		for (int i = image.getMinX(); i < image.getWidth(); i++) {
			for (int j = image.getMinY(); j < image.getHeight(); j++) {
				resultImage.setRGB(i, j, grayImage.getRGB(i, j));
			}
		}

		int[][] finished = new int[image.getWidth()][image.getHeight()];

		for (int i = image.getMinX() + radius + 1; i < image.getWidth() - radius - 1; i++) {
			for (int j = image.getMinY() + radius + 1; j < image.getHeight() - radius - 1; j++) {
				// 循环X和Y坐标，逐个像素比较。

				if (finished[i][j] <= 0) {
					brightnessReceptiveField(resultImage, grayImage, i, j, radius, finished);
				}
			}
		}

		return resultImage;
	}

	public static void brightnessReceptiveField(BufferedImage resultImage, BufferedImage blurImage, int x0, int y0,
			int radius, int[][] finished) {// 第一层，只做简单聚类
		// 亮度分界
		// 区域汇聚

		int rgb = blurImage.getRGB(x0, y0);// 中心

		int rgbR = blurImage.getRGB(x0 + 1, y0);
		int rgbL = blurImage.getRGB(x0 - 1, y0);
		int rgbT = blurImage.getRGB(x0, y0 + 1);
		int rgbD = blurImage.getRGB(x0, y0 - 1);

		// 如果中心点周围4个点存在亮度差异，则开始找边缘。
		if (Math.abs(rgbR - rgb) < RANGE && Math.abs(rgb - rgbL) < RANGE && Math.abs(rgb - rgbT) < RANGE
				&& Math.abs(rgb - rgbD) < RANGE) {
			return;
		}
		Graphics graphics = resultImage.getGraphics();
		graphics.setColor(Color.GREEN);
		int x1, y1, x2, y2, x3, y3, x4, y4, x5, y5, x6, y6;
		List<Integer> centerPoint = new ArrayList<Integer>();
		List<Integer> rightPoint = new ArrayList<Integer>();
//		List<Integer> leftPoint = new ArrayList<Integer>();

		// 用外环线绕180度，看每个角度的直线。
		// 方位选择性（orientation selectivity），也叫做“空间朝向”（orientation），指的是一条短线的倾斜角度（范围是0-180°）
		// 绝大部分初级视觉皮层细胞，都有一个“最喜欢的方位”（preferred orientation，中文习惯翻译成“最优方位”）
		// 类似的，还存在“运动方向选择性”、“空间频率/时间频率选择性”（很快会讲到）、“颜色选择性”等等。
		for (int i = 0; i < 180; i++) {
			centerPoint.clear();
			rightPoint.clear();
//			leftPoint.clear();
			// 外环两个点
			x1 = (int) (x0 - radius * Math.sin(Math.PI * (i - 90) / 180));
			y1 = (int) (y0 + radius * Math.cos(Math.PI * (i - 90) / 180));// - radius

			x2 = (x1 > x0) ? x0 - (x1 - x0) : x0 + (x0 - x1);
			y2 = (y1 > y0) ? y0 - (y1 - y0) : y0 + (y0 - y1);

			// 内环两个点
			x3 = (x0 + x1) / 2;
			y3 = (y0 + y1) / 2;
			x4 = (x0 + x2) / 2;
			y4 = (y0 + y2) / 2;

			// x1 x5 x3 x0 x4 x6 x2
			x5 = (x1 + x3) / 2;
			y5 = (y1 + y3) / 2;
			x6 = (x2 + x4) / 2;
			y6 = (y2 + y4) / 2;

//			leftPoint.add(blurImage.getRGB(x0, y0));
			centerPoint.add(blurImage.getRGB(x1, y1));
			centerPoint.add(blurImage.getRGB(x2, y2));
			centerPoint.add(blurImage.getRGB(x3, y3));
			centerPoint.add(blurImage.getRGB(x4, y4));
			centerPoint.add(blurImage.getRGB(x5, y5));
			centerPoint.add(blurImage.getRGB(x6, y6));

			// 用外环上的两个点，和内环上的两个点判断是否是边缘。
			// TODO 考虑再增加一些点，增大准确度
			// 分为6个区域
			if (i < 30 || i >= 150) {// 靠近X轴
//				rightPoint.add(blurImage.getRGB(x0, y0 + 1));
				rightPoint.add(blurImage.getRGB(x1, y1 + 2));
				rightPoint.add(blurImage.getRGB(x2, y2 + 2));
				rightPoint.add(blurImage.getRGB(x3, y3 + 2));
				rightPoint.add(blurImage.getRGB(x4, y4 + 2));
				rightPoint.add(blurImage.getRGB(x5, y5 + 2));
				rightPoint.add(blurImage.getRGB(x6, y6 + 2));

				if (isLeftEdge(centerPoint, rightPoint) || isRightEdge(centerPoint, rightPoint)) {
//					if(y0 == 118) {
//					}else {
//					}
//					System.out.println(x0 +","+ y0);
					graphics.drawLine(x1, y1, x2, y2);
//					graphics.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
					for (int m = Math.min(x3, x4); m < Math.max(x3, x4); m++) {// 跳过这个区域的点
						for (int n = y0 - 2; n < y0 + 2; n++) {
							finished[m][n] = 1;
//							edgeImage.setRGB(m, n, Color.RED.getRGB());
						}
					}
					return;
				}

			} else if (i >= 60 && i < 120) {// 靠近Y轴
//				rightPoint.add(blurImage.getRGB(x0+ 1, y0 ));
				rightPoint.add(blurImage.getRGB(x1 + 2, y1));
				rightPoint.add(blurImage.getRGB(x2 + 2, y2));
				rightPoint.add(blurImage.getRGB(x3 + 2, y3));
				rightPoint.add(blurImage.getRGB(x4 + 2, y4));
				rightPoint.add(blurImage.getRGB(x5 + 2, y5));
				rightPoint.add(blurImage.getRGB(x6 + 2, y6));

				if (isLeftEdge(centerPoint, rightPoint) || isRightEdge(centerPoint, rightPoint)) {
					graphics.drawLine(x1, y1, x2, y2);
//					graphics.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
					for (int m = x0 - 2; m < x0 + 2; m++) {// 跳过这个区域的点
						for (int n = Math.min(y3, y4); n < Math.max(y3, y4); n++) {
							finished[m][n] = 1;
//							edgeImage.setRGB(m, n, Color.RED.getRGB());
						}
					}
					return;
				}
			} else if (i >= 30 && i < 60) {// 靠近45度斜线，10到11点钟方向
				rightPoint.add(blurImage.getRGB(x1 + 2, y1 - 2));
				rightPoint.add(blurImage.getRGB(x2 + 2, y2 - 2));
				rightPoint.add(blurImage.getRGB(x3 + 2, y3 - 2));
				rightPoint.add(blurImage.getRGB(x4 + 2, y4 - 2));
				rightPoint.add(blurImage.getRGB(x5 + 2, y5 - 2));
				rightPoint.add(blurImage.getRGB(x6 + 2, y6 - 2));

				if (isLeftEdge(centerPoint, rightPoint) || isRightEdge(centerPoint, rightPoint)) {
					graphics.drawLine(x1, y1, x2, y2);
//					graphics.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);

					for (int m = Math.min(x3, x4); m < Math.max(x3, x4); m++) {// 跳过这个区域的点
						for (int n = Math.min(y3, y4); n < Math.max(y3, y4); n++) {
							finished[m][n] = 1;
//							edgeImage.setRGB(m, n, Color.RED.getRGB());
						}
					}
					return;
				}
			} else if (i >= 120 && i < 150) {// 靠近45度斜线，1-2点钟方向
				rightPoint.add(blurImage.getRGB(x1 + 2, y1 + 2));
				rightPoint.add(blurImage.getRGB(x2 + 2, y2 + 2));
				rightPoint.add(blurImage.getRGB(x3 + 2, y3 + 2));
				rightPoint.add(blurImage.getRGB(x4 + 2, y4 + 2));
				rightPoint.add(blurImage.getRGB(x5 + 2, y5 + 2));
				rightPoint.add(blurImage.getRGB(x6 + 2, y6 + 2));

				if (isLeftEdge(centerPoint, rightPoint) || isRightEdge(centerPoint, rightPoint)) {
					graphics.drawLine(x1, y1, x2, y2);
//					graphics.drawOval(x0 - radius, y0 - radius, radius * 2, radius * 2);
//					System.out.println("斜线角度："+i);

					for (int m = Math.min(x3, x4); m < Math.max(x3, x4); m++) {// 跳过这个区域的点
						for (int n = Math.min(y3, y4); n < Math.max(y3, y4); n++) {
							finished[m][n] = 1;
//							edgeImage.setRGB(m, n, Color.RED.getRGB());
						}
					}
					return;
				}
			}
		}

		// 中心环绕型，可以检测出亮点。或亮环

		// 检测线条。

		// 中心环，外环

		// 斜线，用椭圆形，像哈密瓜一样。

		// 点、环

		// 中心区域的大小？外围环的大小？大小自适应？

		// 分为中心区域，和环绕区域。
		// 中心区域中有边界穿过
		// 灰度处理，或二值化
//		int brightness = brightness(rgb);
		// 中心圆亮，环绕区域暗
		// 通过卷积，更高效？
		// 只考虑一半亮，一半暗

		// TODO 弧线、夹角、多线相交

		// 穿过中心点，另外

		// TODO 夹角，找出两根线。在线的尽头找夹角？

		// TODO 感受野面积、形状、颜色
	}

	/**
	 * leftPoint 亮 rightPoint 暗
	 * 
	 * @param centerPoint
	 * @param rightPoint
	 * @return
	 */
	private static boolean isLeftEdge(List<Integer> centerPoint, List<Integer> rightPoint) {// , List<Integer> leftPoint
		for (int k = 0; k < centerPoint.size(); k++) {
			if (centerPoint.get(k) - rightPoint.get(k) < RANGE) {// || leftPoint.get(k) - rightPoint.get(k) < RANGE
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * leftPoint 暗 rightPoint 亮
	 * 
	 * @param centerPoint
	 * @param rightPoint
	 * @return
	 */
	private static boolean isRightEdge(List<Integer> centerPoint, List<Integer> rightPoint) {// , List<Integer>
																								// leftPoint
		for (int k = 0; k < centerPoint.size(); k++) {
			if (rightPoint.get(k) - centerPoint.get(k) < RANGE) {// || rightPoint.get(k) - leftPoint.get(k) < RANGE
				return false;
			}
		}
		return true;
	}

	public void onCenterField() {

	}

	public void offCenterField() {

	}

	public static int brightness(int rgb) {
		// 亮度公式是
		// Brightness = ((R*299)+(G*587)+(B*114))/1000
		return ((((rgb & 0xff0000) >> 16) * 299) + (((rgb & 0xff00) >> 8) * 587) + ((rgb & 0xff) * 114)) / 1000;
	}

	public static int gray(int rgb) {// 灰度处理
		int brightness = brightness(rgb);
		return (brightness << 16) | (brightness << 8) | brightness;
//		return new Color(brightness,brightness,brightness).getRGB();
	}

	public static double brightness(int r, int g, int b) {
		// 亮度公式是 Brightness = 0.3 * R + 0.6 * G + 0.1 * B，
		// Y = ((R*299)+(G*587)+(B*114))/1000
		return ((r * 299) + (g * 587) + (b * 114)) / 1000;
	}

	/**
	 * 像素RGB值大于30，则认为是边缘。
	 */
	public static int RANGE = 20 * 256 * 256;// 像素梯度，当前感受野存在亮度差异。黑暗环境下对比度小。

	public static int radius = 10;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	public static void main(String[] args) throws IOException {
		BufferedImage result = brightnessReceptiveField(ImageIO.read(new File("E:\\201502201529.jpg")));
		FileOutputStream output = new FileOutputStream(new File("E:\\201502201529-blob.jpg"));
		ImageIO.write(result, "jpg", output);
		output.flush();
		output.close();

		// TODO 两个区域的亮度有较小差异，需要将边界处对比度增强，马赫带
	}
}
