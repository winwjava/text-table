package winw.ai.perception.visual.occ;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 实现思路：用卷积，比较图片中的像素块。
 * 
 * <p>
 * 双目摄像头像素：1380×480，640×480，拆分为10×10个像素的小图片，大约64×48个图片。
 * <p>
 * 将图片划分为9×9=81个像素的小方格（划分为100×80）。每个方格去和视差上另外一个视角比较。
 * 
 * <p>
 * 有的物体差1个像素，有的物体差3个像素
 */
public class StereoVisionDemo2 {

	// 把R的像素，画到L上。

	public static void main(String[] args) throws IOException {

		BufferedImage l = ImageIO.read(new File("D:/file/05L.jpg"));
		BufferedImage r = ImageIO.read(new File("D:/file/05R.jpg"));

		FileOutputStream output = new FileOutputStream(new File("D:/file/05LR.jpg"));

		BufferedImage result = new BufferedImage(l.getWidth(), l.getHeight(), l.getType());

		for (int j = 30; j < l.getWidth()-30; j += 1) {
			for (int k = 30; k < l.getHeight()-30; k += 1) {
				if (j % 2 == 1 || k % 2 == 1) {
					result.setRGB(j, k, l.getRGB(j, k));
				} else {
					result.setRGB(j, k, brightness(r.getRGB(j, k)));// 右侧
				}
			}
		}

		ImageIO.write(result, "jpg", output);
		output.flush();
		output.close();
	}

	public static int brightness(int rgb) {
		int brightnessIncrement = 10;
	    int red = (rgb >> 16) & 0xFF;
	    int green = (rgb >> 8) & 0xFF;
	    int blue = rgb & 0xFF;
	 
	    red = Math.min(255, red + brightnessIncrement);
	    green = Math.min(255, green + brightnessIncrement);
	    blue = Math.min(255, blue + brightnessIncrement);
	 
	    return 0xFF000000 | (red << 16) | (green << 8) | blue;
	}
}
