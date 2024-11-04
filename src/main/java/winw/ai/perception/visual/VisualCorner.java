package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * 
 * 根据边缘，组成线段或角，或弧线。 线段、角探测器，是组成形状的关键。
 * <p>
 * 3．超复杂细胞。能从几种复杂细胞中接受兴奋性和抑制性的输入信息。反应特点同复杂细胞，也反应特殊方位的线性刺激，但有明显的端点抑制，这种刺激不能超过某种长度。
 * 》》超复杂型：感受野的反应特征与复杂型相似，但有明显的终端抑制，即长方形的长度超过一定的限度则有抑制效应（检测端点）。
 * 4．极高度复杂细胞。反应移过视野的边，只要是这边有一特定的宽度。有些极高度复杂细胞特别反应两个边形成的直角，这种细胞也称为角探测器。
 * <p>
 * 复杂型感受野较简单型大，呈长方形且不能区分出开反应与闭反应区，可以看成是由直线型简单感受野平行移动而成；
 * 
 * 超复杂型感受野的反应特性与复杂型相似，但有明显的终端抑制，即长方形的长度超过一定限度则有抑制效应。
 * 
 * 总之，简单型的细胞感受野是直线形，与图形边界线的觉察有关；复杂型和超复杂型细胞为长方形感受野，与对图形的边角或运动感知觉有关。
 * 
 */
public class VisualCorner {// 角感受器，线条中断感受器，短线段、长曲线、物体的角、圆弧


	// 应该建立一种网络模型。分布式计算、分布式存储

	/**
	 * 末端抑制感受野（End-inhibited receptive fields）
	 * 
	 * @param image
	 * @return
	 */
	public static int[][] endInhibitedReceptiveField(BufferedImage image) {// 用Blob实现对角度检测

		// 线段、

		// 可能还是通过线，找到角。

		return null;
	}

	static Random RANDOM = new Random();

	/**
	 * 检测端点、角探测器
	 * 
	 * 
	 * <p>
	 * 3．超复杂细胞。能从几种复杂细胞中接受兴奋性和抑制性的输入信息。反应特点同复杂细胞，也反应特殊方位的线性刺激，但有明显的端点抑制，这种刺激不能超过某种长度。
	 * 》》超复杂型：感受野的反应特征与复杂型相似，但有明显的终端抑制，即长方形的长度超过一定的限度则有抑制效应（检测端点）。
	 * 4．极高度复杂细胞。反应移过视野的边，只要是这边有一特定的宽度。有些极高度复杂细胞特别反应两个边形成的直角，这种细胞也称为角探测器。
	 * 
	 * @return
	 */
	public static LineGrid[][] cornerReceptiveField(BufferedImage image, int[][] grayImage, LineGrid[][] lineGrid) {
		// FIXME 错误方案：在长条矩形区域内，中心点经过了两个方向（方向在15度到170度之间）的边缘 相交，分为4等分，其中3个份都与这一份有较大对比度。

		int over = 0;
		Graphics graphics = image.getGraphics();
//		Line[][] lineGrid = new Line[lineGrid.length][lineGrid[0].length];
		int radius = 7;// 感受野半径（轴长），宽7个像素

		// 角检测，不能靠 blob，只能靠 edge，但感受野有两个edge ，并且相交时，就认为是角存在。

		for (int j = radius + 1; j < lineGrid.length - radius - 1; j++) {
			for (int k = radius + 1; k < lineGrid[0].length - radius - 1; k++) {

				// 当中心点有LineGrid，并且向周围方向去找
				LineGrid lineList = lineGrid[j][k];
				if (lineList == null || lineList.getLineList().size() <= 0) {
					continue;
				}
				// 查找感受野范围内的别的边缘

				for (Line line : lineList.getLineList()) {
					// FIXME 这里必须是Blob，因为用边缘，还需要重新计算周围的像素点。『不对』
//					double slope = line.getSlope();

					// 遍历周围7×7范围内是否有另外一个Line，并且 Orientation 不同。

//					System.out.println(line.getOrientation());
					for (int x = j - radius; x < j + radius && x != j; x++) {
						for (int y = k - radius; y < k + radius; y++) {

							LineGrid tempGrid = lineGrid[x][y];
							if (tempGrid != null && tempGrid.getLineList().size() > 0) {
								for (Line tempLine : tempGrid.getLineList()) {
									int corner = Math.abs(tempLine.getOrientation() - line.getOrientation());
									if (corner % 180 > 30) {
										System.out.println(line.getOrientation() + " - " + tempLine.getOrientation()
												+ " = " + (corner % 180));

										// 并且两条线形成的夹角，组成的区域属于末端抑制感受野：其中一个角围成区域与其他三个区域存在拮抗

										int[] intersection = getIntersection(line, tempLine);// 根据两条线求相交的点：

										int x0 = intersection[0];
										int y0 = intersection[1];

										// 两条边的两侧区域都形成拮抗，两条线的延伸线上，分别取5个像素。

										// 以这个点为中心，两条线为分界，一侧与另外一侧比较。

										// 找到距离最远的点
										int d1 = (x0 - line.getX1()) * (x0 - line.getX1())
												+ (y0 - line.getY1()) * (y0 - line.getY1());
										int d2 = (x0 - line.getX2()) * (x0 - line.getX2())
												+ (y0 - line.getY2()) * (y0 - line.getY2());

										int d3 = (x0 - tempLine.getX1()) * (x0 - tempLine.getX1())
												+ (y0 - tempLine.getY1()) * (y0 - tempLine.getY1());
										int d4 = (x0 - tempLine.getX2()) * (x0 - tempLine.getX2())
												+ (y0 - tempLine.getY2()) * (y0 - tempLine.getY2());

										int x1 = d1 > d2 ? line.getX1() : line.getX2();
										int y1 = d1 > d2 ? line.getY1() : line.getY2();

										int x2 = d3 > d4 ? tempLine.getX1() : tempLine.getX2();
										int y2 = d3 > d4 ? tempLine.getY1() : tempLine.getY2();

										long avgRgb0 = extracted(image, grayImage, lineGrid, x0, y0, x1, y1, x2, y2);

										int x3 = x0 + (x0 - x1);
										int y3 = y0 + (y0 - y1);
										if (x3 < 0 || y3 < 0 || x3 > lineGrid.length || y3 > lineGrid[0].length
												|| avgRgb0 < 0) {
											continue;
										}
										long avgRgb1 = extracted(image, grayImage, lineGrid, x0, y0, x3, y3, x2, y2);

										if (Math.abs(avgRgb0 - avgRgb1) < 25 && avgRgb1 >= 0) {
											continue;
										}

										int x4 = x0 + (x0 - x2);
										int y4 = y0 + (y0 - y2);

										if (x4 < 0 || y4 < 0 || x4 > lineGrid.length || y4 > lineGrid[0].length) {
											continue;
										}
										long avgRgb2 = extracted(image, grayImage, lineGrid, x0, y0, x1, y1, x4, y4);

										if (Math.abs(avgRgb0 - avgRgb2) > 25 && avgRgb2 >= 0) {
//											graphics.setColor(new Color(RANDOM.nextFloat(), RANDOM.nextFloat(),
//													RANDOM.nextFloat()));
//											graphics.drawOval(x0, y0, 15, 15);

//											graphics.fillPolygon(new int[] { x0, x1, x2 }, new int[] { y0, y1, y2 }, 3);
//											graphics.fillPolygon(intersection, intersection, k);

											graphics.setColor(Color.WHITE);
											graphics.drawLine(x0, y0, x1, y1);
											graphics.drawLine(x0, y0, x2, y2);
										}

										over++;
										if (over > 500000) {
											return null;
										}

									}

								}
							}

						}
					}

//			        double slope = 1/Math.tan( Math.toRadians(line.getOrientation()));// 计算斜率

					// 当斜率靠近X轴时，应该用Y计算X

					// 根据斜率，找这个范围的线。

					// 沿着这个方向查找。相差一个像素也没关系。

					// 根据这个Line，查找线上的点

					// 知道度数，沿着X，或Y方向上去找点。

				}

				// 用相邻的3个像素计算，

			}
		}
		return null;
	}

	private static long extracted(BufferedImage image, int[][] grayImage, LineGrid[][] lineGrid, int x0, int y0, int x1,
			int y1, int x2, int y2) {
		int minY = Math.min(Math.min(y1, y2), x0);
		int maxY = Math.max(Math.max(y1, y2), y0);

		long count0 = 0;
		long sumRgb0 = 0;
		for (int m = Math.min(x1, Math.min(x2, x0)); m <= Math.max(x1, Math.max(x2, x0)) && m < lineGrid.length; m++) {
			for (int n = minY; n <= maxY && n < lineGrid[0].length; n++) {
				if (isInsideTriangle(m, n, x1, y1, x2, y2, x0, y0)) {
//													System.out.println("Pixel: (" + m + ", " + n + ")");
//					image.setRGB(m, n, 0xFFFFFF);
					count0++;
					sumRgb0 += grayImage[m][n];
				}
			}
		}
		if (count0 <= 0) {
			return -1;
		}
		return (int) (sumRgb0 / count0);
	}

	public static int[] getIntersection(Line line1, Line line2) {
		return getIntersection(line1.getX1(), line1.getY1(), line1.getX2(), line1.getY2(), line2.getX1(), line2.getY1(),
				line2.getX2(), line2.getY2());
	}

	public static int[] getIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4,
			double y4) {
		double denominator = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if (denominator == 0) {
			return null; // Lines are parallel or coincide
		}
		double x = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denominator;
		double y = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denominator;
		return new int[] { (int) Math.round(x), (int) Math.round(y) };
	}

	public static void rasterizeTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		int minY = Math.min(Math.min(y1, y2), y3);
		int maxY = Math.max(Math.max(y1, y2), y3);

		for (int y = minY; y <= maxY; y++) {
			for (int x = Math.min(x1, Math.min(x2, x3)); x <= Math.max(x1, Math.max(x2, x3)); x++) {
				if (isInsideTriangle(x, y, x1, y1, x2, y2, x3, y3)) {
					System.out.println("Pixel: (" + x + ", " + y + ")");

				}
			}
		}
	}

	public static boolean isInsideTriangle(int x, int y, int x1, int y1, int x2, int y2, int x3, int y3) {
		boolean b1 = crossProductSign(x, y, x1, y1, x2, y2) < 0.0;
		boolean b2 = crossProductSign(x, y, x2, y2, x3, y3) < 0.0;
		boolean b3 = crossProductSign(x, y, x3, y3, x1, y1) < 0.0;
		return ((b1 == b2) && (b2 == b3));
	}

	public static double crossProductSign(int x1, int y1, int x2, int y2, int x3, int y3) {
		return (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
	}

	/**
	 * 超复杂细胞感受野，线条端点、拐角。
	 * 
	 * 简单细胞：特定位置，特定方向，复杂细胞：不管位置，特定方向，超复杂细胞：不论位置、方向，对角起反应
	 * 
	 * <Point> V1对简单的方向有反应，V2可以编码两个方向的夹角
	 */
	public static void hypercomplexCellReceptiveField() {// 分为低阶超复杂细胞和高阶超复杂细胞。类似简单细胞和复杂细胞的关系。

	}

	public static void main(String[] args) throws IOException {
		BufferedImage image = ImageIO.read(new File("D:/file/05.jpg"));
		long t0 = System.currentTimeMillis();

		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

		int[][] grayImage = VisualRetina.brightnessReceptiveField(image, 3);
		LineGrid[][] lineGridArray = VisualEdge.edgeReceptiveField(result, grayImage);

		// 角感受器
		cornerReceptiveField(result, grayImage, lineGridArray);

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Form, cost: " + (t1 - t0) + "ms.");
		FileOutputStream output = new FileOutputStream(new File("D:/file/05-contour.jpg"));

		ImageIO.write(result, "jpg", output);
		output.flush();
		output.close();

		// TODO 将亮暗边界的视野扩大，扩大到整个视野，将小视野的明暗边界连接为更大视野的明暗边界。

		// TODO V1到V2 明暗边界到物体形状，是拼凑的吗？
		// 非经典感受野的意义？

	}
	// 短线段：长条形感受野两侧是抑制区，沿着定向轴的抑制区域，存在末端抑制
	
	// 边缘角：长条形感受野，触发中心兴奋区，用Blob实现
	// 有一个BLOB，另外的区域

	public static void main1(String[] args) throws IOException {
//		BufferedImage bufferedImage = ImageIO.read(new File("D:/file/05.jpg"));
		long t0 = System.currentTimeMillis();

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Edge, cost: " + (t1 - t0) + "ms.");
		FileOutputStream output = new FileOutputStream(new File("D:/file/05-edge.jpg"));

//		ImageIO.write(result, "jpg", output);
		output.flush();
		output.close();

		// TODO 将亮暗边界的视野扩大，扩大到整个视野，将小视野的明暗边界连接为更大视野的明暗边界。

		// TODO V1到V2 明暗边界到物体形状，是拼凑的吗？
		// 非经典感受野的意义？
		
		// TODO 角，不能根据Blob 检测。必须单独扫描一遍。

	}
}
