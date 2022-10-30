package winw.ai.util.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class HeadImage {

	/**
	 * 
	 * 将Image图像中的透明/不透明部分转换为Shape图形
	 */
	public static Shape getImageShape(Image img, boolean transparent) throws InterruptedException {
		ArrayList<Integer> x = new ArrayList<Integer>();
		ArrayList<Integer> y = new ArrayList<Integer>();
		int width = img.getWidth(null);
		int height = img.getHeight(null);

		// 首先获取图像所有的像素信息
		PixelGrabber pgr = new PixelGrabber(img, 0, 0, -1, -1, true);
		pgr.grabPixels();
		int pixels[] = (int[]) pgr.getPixels();

		// 循环像素
		for (int i = 0; i < pixels.length; i++) {
			// 筛选，将不透明的像素的坐标加入到坐标ArrayList x和y中
			int alpha = (pixels[i] >> 24) & 0xff;
			if (alpha == 0) {
				continue;
			} else {
				x.add(i % width > 0 ? i % width - 1 : 0);
				y.add(i % width == 0 ? (i == 0 ? 0 : i / width - 1) : i / width);
			}
		}

		// 建立图像矩阵并初始化(0为透明,1为不透明)
		int[][] matrix = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				matrix[i][j] = 0;
			}
		}

		// 导入坐标ArrayList中的不透明坐标信息
		for (int c = 0; c < x.size(); c++) {
			matrix[y.get(c)][x.get(c)] = 1;
		}

		/*
		 * 逐一水平"扫描"图像矩阵的每一行，将透明（这里也可以取不透明的）的像素生成为Rectangle，
		 * 再将每一行的Rectangle通过Area类的rec对象进行合并， 最后形成一个完整的Shape图形
		 */
		Area rec = new Area();
		int temp = 0;
		// 生成Shape时是1取透明区域还是取非透明区域的flag
		int flag = transparent ? 0 : 1;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (matrix[i][j] == flag) {
					if (temp == 0)
						temp = j;
					else if (j == width) {
						if (temp == 0) {
							Rectangle rectemp = new Rectangle(j, i, 1, 1);
							rec.add(new Area(rectemp));
						} else {
							Rectangle rectemp = new Rectangle(temp, i, j - temp, 1);
							rec.add(new Area(rectemp));
							temp = 0;
						}
					}
				} else {
					if (temp != 0) {
						Rectangle rectemp = new Rectangle(temp, i, j - temp, 1);
						rec.add(new Area(rectemp));
						temp = 0;
					}
				}
			}
			temp = 0;
		}
		return rec;
	}

	public void composePic(String back, String head, String out) {
		try {
			// 带人物轮廓的背景图(人物轮廓透明)
			File backFile = new File(back);
			Image backImg = ImageIO.read(backFile);
			int bw = backImg.getWidth(null);
			int bh = backImg.getHeight(null);

			// 人物的head图
			File headFile = new File(head);
			Image headImg = ImageIO.read(headFile);
			int lw = headImg.getWidth(null);
			int lh = headImg.getHeight(null);

			// 得到透明的区域(人物轮廓)
			Shape shape = getImageShape(ImageIO.read(new File(back)), true);

			// 合成后的图片
			BufferedImage img = new BufferedImage(bw, bh, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = img.createGraphics();
			// 设置画布为透明
			img = g2d.getDeviceConfiguration().createCompatibleImage(bw, bh, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = img.createGraphics();

			// 取交集(限制可以画的范围为shape的范围)
			g2d.clip(shape);

			// 这里的坐标需要根据实际情况进行调整
			g2d.drawImage(headImg, 98, 10, lw, lh, null);

			g2d.dispose();

			ImageIO.write(img, "png", new File(out));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws IOException {
		String basePath = "E:/test/";
		HeadImage pic = new HeadImage();
		pic.composePic(basePath + "1.png", basePath + "2.png", basePath + "result.png");
	}
}
