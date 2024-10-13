package winw.ai.perception.visual.opencv;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 找出每个方向线条，像素更多（为了为了辨识贴合线条的边缘）的算法；
 * @author winw
 *
 */
public class RoundTest {

	public static void main(String[] args) throws IOException {
		BufferedImage result = ImageIO.read(new File("D:/file/05-StereoVision.jpg"));
		int x0 = 200;
		int y0 = 200;
		int radius = 10;
		int x1, y1, x2, y2;
		for (int i = 0; i < 180; i++) {
			// 外环两个点
			x1 = (int) (x0 - radius * Math.sin(Math.PI * (i - 90) / 180));
			y1 = (int) (y0 + radius * Math.cos(Math.PI * (i - 90) / 180));// - radius

			x2 = (x1 > x0) ? x0 - (x1 - x0) : x0 + (x0 - x1);
			y2 = (y1 > y0) ? y0 - (y1 - y0) : y0 + (y0 - y1);

			int count = 0;

			if (i > 45 && i < 135) {// 将Y轴与X轴调换，为了获得更多的像素点
				double slope = ((double) (x2 - x1)) / ((double) (y2 - y1));
				int from = Math.min(y1, y2);
				int to = Math.max(y1, y2);
				for (int y = from; y <= to; y++) {
					// 当斜率等于0，平行与X轴
					int x = (int) ((y - y1) * slope + x1);
					result.setRGB(x, y, Color.RED.getRGB());
					count++;
				}

			} else {
				double slope = ((double) (y2 - y1)) / ((double) (x2 - x1));
				int from = Math.min(x1, x2);
				int to = Math.max(x1, x2);
				for (int x = from; x <= to; x++) {
					// 当斜率等于0，平行与X轴
					int y = (int) ((x - x1) * slope + y1);
					result.setRGB(x, y, Color.RED.getRGB());
					count++;
				}
			}

			// TODO 在靠近X轴时，应该用Y，作为
			System.out.println("角度：" + i + "，经过像素：" + count);
		}
		FileOutputStream output = new FileOutputStream(new File("D:/file/05-round.jpg"));
		ImageIO.write(result, "jpg", output);
		output.flush();
		output.close();
	}

}
