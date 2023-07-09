package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CannyTest {

	public static void main(String[] args) throws Exception {
		BufferedImage bufImage = ImageIO.read(new File("E:/IMG/0612.jpg"));
		BufferedImage cannyImg = getCannyImg(bufImage, 0.08, 0.4, 2);
		ImageIO.write(cannyImg, "JPEG", new File("E:/IMG/0612-canny.jpg"));
		System.out.println("Main: Successfully!");
	}

	static int edge[][];
	static int find[][];
	static double vmax = 0;
	static BufferedImage cannyImg;

	public static BufferedImage getCannyImg(BufferedImage image, double a, double b, double g) throws IOException {
		// 灰度化
		BufferedImage grayImg = gray(image);
		// 高斯滤波
		grayImg = filteringGaussian(grayImg, g);
		// 计算梯度幅度值和梯度方向
		double grad[][][] = getGradient(grayImg);
		// double nms[][]=NMS(grad);
		// 非极大值抑制处理(插值法)
		double nms[][] = NMS_2(grad);
		// 双阈值处理
		double thresh = vmax * 0.08;
		double_threshold(nms, b * thresh, thresh);
		// 边界跟踪处理
		boundary_tracking();
		BufferedImage cannyImg = showCannyImg();
		return cannyImg;
	}

	/**
	 * 灰度化
	 * 
	 * @param image
	 * @return
	 */
	private static BufferedImage gray(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage grayImg = new BufferedImage(w, h, image.getType());

		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) {
				int pixel = image.getRGB(x, y);
				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = (pixel & 0xff);
				int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
				grayImg.setRGB(x, y, new Color(gray, gray, gray).getRGB());
			}
		return grayImg;
	}

	private static int getGray(int pixel) {
		return pixel & 0xff;
	}

	/**
	 * 高斯滤波
	 * 
	 * @param image
	 * @param g
	 * @return
	 */
	private static BufferedImage filteringGaussian(BufferedImage image, double g) {
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
			}
		}
		return gaussianImg;
	}

	/**
	 * 计算梯度幅度值和梯度方向
	 * 
	 * @param image
	 * @return
	 */
	private static double[][][] getGradient(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		int step = 1;
		double[][][] grad = new double[w][h][2];
		for (int x = step; x < w - step; x++) {
			for (int y = step; y < h - step; y++) {
				int gx = -getGray(image.getRGB(x - step, y - step)) - getGray(image.getRGB(x - step, y)) * 2
						- getGray(image.getRGB(x - step, y + step)) + getGray(image.getRGB(x + step, y - step))
						+ getGray(image.getRGB(x + step, y)) * 2 + getGray(image.getRGB(x + step, y + step));
				int gy = getGray(image.getRGB(x - step, y + step)) + getGray(image.getRGB(x, y + step)) * 2
						+ getGray(image.getRGB(x + step, y + step)) - getGray(image.getRGB(x - step, y - step))
						- getGray(image.getRGB(x, y - step)) * 2 - getGray(image.getRGB(x + step, y - step));
				grad[x][y][0] = Math.sqrt(gx * gx + gy * gy);
				if (gx == 0)
					grad[x][y][1] = 90;
				else
					grad[x][y][1] = Math.toDegrees(Math.atan(1.0 * gy / gx));
			}
		}

		return grad;
	}

	public static double[][] NMS(double[][][] grad) {
		int w = grad.length;
		int h = grad[0].length;
		double[][] nms = new double[w][h];
		for (int x = 1; x < w - 1; x++) {
			for (int y = 1; y < h - 1; y++) {
				nms[x][y] = grad[x][y][0];
				if (grad[x][y][0] == 0)
					continue;
				int a = (int) Math.round(grad[x][y][1] / 45);
				if (a == -2 || a == 2) {
					if (grad[x][y][0] < grad[x][y - 1][0] || grad[x][y][0] < grad[x][y + 1][0])
						nms[x][y] = 0;
				} else if (a == -1) {
					if (grad[x][y][0] < grad[x - 1][y - 1][0] || grad[x][y][0] < grad[x + 1][y + 1][0])
						nms[x][y] = 0;
				} else if (a == 0) {
					if (grad[x][y][0] < grad[x - 1][y][0] || grad[x][y][0] < grad[x + 1][y][0])
						nms[x][y] = 0;
				} else if (a == 1) {
					if (grad[x][y][0] < grad[x + 1][y - 1][0] || grad[x][y][0] < grad[x - 1][y + 1][0])
						nms[x][y] = 0;
				}
				if (nms[x][y] > vmax)
					vmax = nms[x][y];
			}
		}

		return nms;
	}

	/**
	 * 非极大值抑制处理(插值法)
	 * 
	 * @param grad
	 * @return
	 */
	private static double[][] NMS_2(double[][][] grad) {
		int w = grad.length;
		int h = grad[0].length;
		double[][] nms = new double[w][h];
		for (int x = 1; x < w - 1; x++) {
			for (int y = 1; y < h - 1; y++) {
				nms[x][y] = grad[x][y][0];
				if (grad[x][y][0] == 0)
					continue;
				int x1, y1, x2, y2;
				double weight = Math.tan(Math.toRadians(grad[x][y][1]));
				if (grad[x][y][1] > 45) {
					x1 = 0;
					y1 = 1;
					x2 = 1;
					y2 = 1;
					weight = 1.0 / weight;
				} else if (grad[x][y][1] > 0) {
					x1 = 1;
					y1 = 0;
					x2 = 1;
					y2 = 1;
					weight *= 1;
				} else if (grad[x][y][1] > -45) {
					x1 = 1;
					y1 = 0;
					x2 = 1;
					y2 = -1;
					weight *= -1;
				} else {
					x1 = 0;
					y1 = -1;
					x2 = 1;
					y2 = -1;
					weight = -1.0 / weight;
				}
				double g1 = grad[x + x1][y + y1][0];
				double g2 = grad[x + x2][y + y2][0];
				double g3 = grad[x - x1][y - y1][0];
				double g4 = grad[x - x2][y - y2][0];
				double grad_count_1 = g1 * weight + g2 * (1 - weight);
				double grad_count_2 = g3 * weight + g4 * (1 - weight);
				if (grad[x][y][0] < grad_count_1 || grad[x][y][0] < grad_count_2)
					nms[x][y] = 0;
				if (nms[x][y] > vmax)
					vmax = nms[x][y];
			}
		}
		return nms;
	}

	/**
	 * 双阈值处理
	 * 
	 * @param nms
	 * @param low_th
	 * @param high_th
	 */
	private static void double_threshold(double[][] nms, double low_th, double high_th) {
		int w = nms.length;
		int h = nms[0].length;
		edge = new int[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (nms[x][y] >= high_th) {
					edge[x][y] = 2;
				} else if (nms[x][y] >= low_th) {
					edge[x][y] = 1;
				}
			}
		}
	}

	/**
	 * 边界跟踪处理
	 * 
	 * @return
	 */
	private static int[][] boundary_tracking() {
		int w = edge.length;
		int h = edge[0].length;

		find = new int[w][h];

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (find[x][y] == 1)
					continue;
				if (edge[x][y] == 2) {
					dfs(x, y, w, h);
				} else if (edge[x][y] == 0) {
					find[x][y] = 1;
				}
			}
		}
		return edge;
	}

	private static void dfs(int x, int y, int w, int h) {
		if (x < 0 || x >= w || y < 0 || y >= h || find[x][y] == 1)
			return;
		find[x][y] = 1;
		if (edge[x][y] >= 1) {
			edge[x][y] = 2;// 边缘
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					dfs(x + i, y + j, w, h);
				}
			}
		}
	}

	private static BufferedImage showCannyImg() throws IOException {
		int w = edge.length;
		int h = edge[0].length;

		BufferedImage cannyImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (edge[x][y] == 2) {// 边缘
					cannyImg.setRGB(x, y, new Color(255, 255, 255).getRGB());// 白色
				} else {
					cannyImg.setRGB(x, y, new Color(0, 0, 0).getRGB());// 黑色
				}
			}
		}
		return cannyImg;
	}
}