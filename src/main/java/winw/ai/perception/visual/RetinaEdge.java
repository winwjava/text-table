package winw.ai.perception.visual;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import winw.ai.model.Graph;

/**
 * 边缘识别。基于边缘可以得出形状，再根据双眼视差和深度，得到立体的三维模型。
 * 
 * <p>
 * 模拟控制眼睛变焦，将图像缩小
 * 
 * <p>
 * 普通的照片，只是景象的投影，要想构建景象的模型，则应当使用立体景象（双眼视差）。
 * 
 * @author winw
 *
 */
public class RetinaEdge {
	/**
	 * 像素RGB值大于30，则认为是边缘。
	 */
	public static int range = 10;// 像素梯度

	public static BufferedImage generateGraph(BufferedImage image) throws IOException {
//		Graph graph = new Graph();
		BufferedImage edgeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);

		for (int i = image.getMinX(); i < image.getWidth(); i++) {
			for (int j = image.getMinY(); j < image.getHeight(); j++) {
				// 循环X和Y坐标，逐个像素比较。

				if (i == image.getMinX() || j == image.getMinY() || i == image.getWidth() - 1
						|| j == image.getHeight() - 1) {
					continue;// 四条边的像素点不用比较。
				}

				int[] rgb = rgbValue(image.getRGB(i, j));
				int[] rgbR = rgbValue(image.getRGB(i + 1, j));
				int[] rgbL = rgbValue(image.getRGB(i - 1, j));
				int[] rgbT = rgbValue(image.getRGB(i, j + 1));
				int[] rgbD = rgbValue(image.getRGB(i, j - 1));

				// 检查当前像素点与上下左右像素比较，是否是边缘。
				if (rgbSimilar(rgb, rgbR) || rgbSimilar(rgb, rgbL) || rgbSimilar(rgb, rgbT) || rgbSimilar(rgb, rgbD)) {

//					graph.addNode(i, j);
					
					edgeImage.setRGB(i, j, 6522);
				}else {
					edgeImage.setRGB(i, j, image.getRGB(i, j));
				}
			}
		}
		return edgeImage;
	}

	public static boolean rgbSimilar(int[] a, int[] b) {
		return a[0] - b[0] >= range || a[1] - b[1] >= range || a[2] - b[2] >= range;
	}

	/**
	 * 返回RGB的R、G、B的数组。
	 * 
	 * @param rgb
	 * @return
	 */
	public static int[] rgbValue(int rgb) {
		int[] rgbValue = new int[3];
		rgbValue[0] = (rgb & 0xff0000) >> 16;
		rgbValue[1] = (rgb & 0xff00) >> 8;
		rgbValue[2] = (rgb & 0xff);
		return rgbValue;
	}

	/**
	 * 图像聚焦，将焦点放到其中一小部分。
	 * 
	 */
//	public static BufferedImage imageSeparation(Image img, int w, int h) {
//		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
////		Graphics g = result.getGraphics();
////		g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
////		g.dispose();
//		return result;
//	}

	/**
	 * 缩图，按比例缩小？
	 */
//	public static BufferedImage imageThumbnail(Image img, int w, int h) {
//		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//		Graphics g = result.getGraphics();
//		g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
//		g.dispose();
//		return result;
//	}

	public static void main(String[] args) throws IOException {
		File file = new File("E:\\2016.jpg");
		BufferedImage bufferedImage = ImageIO.read(file);
		BufferedImage graph = generateGraph(bufferedImage);
		FileOutputStream ops = new FileOutputStream(new File("E:\\2016_edge4s_" + file.getName()));
		ImageIO.write(graph, "jpg", ops);
		ops.flush();
		ops.close();
	}
}
