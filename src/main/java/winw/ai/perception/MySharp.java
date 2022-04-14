package winw.ai.perception;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class MySharp {
	public static void main(String[] args) throws Exception {
		getImageValues();
	}

	static List<Integer> getImageValues() throws Exception {
		List<Integer> list = new ArrayList<>();
		BufferedImage bi = ImageIO.read(new File("e://ww.png"));// 读取一个图片
		int w, h, r, a, g, b, rgb;// 定义图片的高宽 已经 rgb 变量
		w = bi.getWidth();// 获取图片的高宽
		h = bi.getHeight();
		BufferedImage newBi = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);

		int[] drawPoint = new int[w]; // 这个数字是用来记录 每次投影的点的

		for (int i = 0; i < w; ++i) {// 因为是横着扫描
			drawPoint[i] = h - 1;
		}

		for (int x = 0; x < w; ++x) {// 遍历每个点
			for (int y = 0; y < h; ++y) {
				rgb = bi.getRGB(x, y); // 获取颜色rgb
				a = (rgb >> 24) & 0xff;
				r = (rgb >> 16) & 0xff;
				g = (rgb >> 8) & 0xff;
				b = rgb & 0xff;

				if (r > 100 && g < 60 && b < 60) {// 如果趋近于红色 就把这个颜色设置为正红色
					r = 255;
					g = 0;
					b = 0;
				} else {
					r = g = b = 255;// 白色
				}

				rgb = (a << 24) | (r << 16) | (g << 8) | b;

				if (r == 255 && g == 0 && b == 0) {
					newBi.setRGB(x, drawPoint[x]--, rgb);// 从下到上绘制这个点
				}
			}
		}

		for (int i = 0; i < drawPoint.length; ++i) {
			System.out.print(h - 1 - drawPoint[i] + " ");// 减去无用点 把下面的点移动上来
		}

		ImageIO.write(newBi, "jpg", new File("e://wwaa.jpg"));
		return list;
	}
}