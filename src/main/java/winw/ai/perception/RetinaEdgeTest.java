package winw.ai.perception;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

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
public class RetinaEdgeTest {
	public static List<Integer> xArr = new ArrayList<>();
	public static List<Integer> yArr = new ArrayList<>();

	public static Map<Integer, List<Integer>> resultMap = new HashMap<>();

	/**
	 * 像素RGB值大于30，则认为是边缘。
	 */
	public static int range = 10;// 像素梯度

	public static BufferedImage drawEdge(BufferedImage image) throws IOException {
		for (int i = image.getMinX(); i < image.getWidth(); i++) {
			for (int j = image.getMinY(); j < image.getHeight(); j++) {
				// 循环X和Y坐标，逐个像素比较。

				if (i == image.getMinX() || j == image.getMinY() || i == image.getWidth() - 1
						|| j == image.getHeight() - 1) {
					continue;// 四条边的像素点不用比较。
				}

				int rgb = image.getRGB(i, j);

				int rgb1 = image.getRGB(i + 1, j);
				int rgb2 = image.getRGB(i - 1, j);
				int rgb3 = image.getRGB(i, j + 1);
				int rgb4 = image.getRGB(i, j - 1);
				// 检查当前像素点与上下左右像素比较，是否是边缘。
				if (checkImage(rgb, rgb1, rgb2, rgb3, rgb4)) {
					List<Integer> arrayList = resultMap.getOrDefault(i, new ArrayList<Integer>());
					arrayList.add(j);
					resultMap.put(i, arrayList);
				}
			}
		}

		for (Integer key : resultMap.keySet()) {
			List<Integer> integers = resultMap.get(key);
			for (int i = 0; i < integers.size(); i++) {
//				Integer max = Collections.max(integers);
//				Integer min = Collections.min(integers);
				Integer y = integers.get(i);

				image.setRGB(key, y, 65281);
			}
		}

		return image;
	}

	public static boolean aAndBArr(int[] aArr, int[] bArr) {
		int i1 = aArr[0];
		int i2 = aArr[1];
		int i3 = aArr[2];

		int j1 = bArr[0];
		int j2 = bArr[1];
		int j3 = bArr[2];

		return i1 - j1 >= range || i2 - j2 >= range || i3 - j3 >= range;
	}

	public static boolean checkImage(int rgb, int rgb1, int rgb2, int rgb3, int rgb4) {

		int[] ints = rgbValue(rgb);
		int[] ints1 = rgbValue(rgb1);
		int[] ints2 = rgbValue(rgb2);
		int[] ints3 = rgbValue(rgb3);
		int[] ints4 = rgbValue(rgb4);

		return aAndBArr(ints, ints1) || aAndBArr(ints, ints2) || aAndBArr(ints, ints3) || aAndBArr(ints, ints4);

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
	public static BufferedImage imageSeparation(Image img, int w, int h) {
		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//		Graphics g = result.getGraphics();
//		g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
//		g.dispose();
		return result;
	}

	/**
	 * 缩图，按比例缩小？
	 */
	public static BufferedImage imageThumbnail(Image img, int w, int h) {
		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = result.getGraphics();
		g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
		g.dispose();
		return result;
	}

	public static void main(String[] args) throws IOException {

		File file = new File("E:\\tt.jpg");
		BufferedImage bufferedImage = ImageIO.read(file);
		bufferedImage = drawEdge(bufferedImage);

		FileOutputStream ops = new FileOutputStream(new File("E:\\\\new_tt_" + file.getName()));
		ImageIO.write(bufferedImage, "jpg", ops);
		ops.flush();
		ops.close();
	}
}
