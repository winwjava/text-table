package winw.ai.perception.visual;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 在V1区一个感受野中，统一计算对比度、颜色斑块、空间频率、明暗边界等。
 * 
 * <p>
 * 感受野与感受野之间存在重叠（人在用眼过度会有重影），重叠部分在V1区重新合并计算时去掉重叠部分。大外周（寿天德）或整合野（李朝义）的观点可靠吗？
 */
public class VisualV1 {

	// 总共640 × 480 像素，72度，每度大约6.6-8.8个像素，半径是3-5个像素。但每度像素太少也计算不出结果。
	public static int radius = 3;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	public static void v1(BufferedImage result, BufferedImage image) {
		ReceptiveField[][] retinaReceptiveField = VisualRetina.retinaReceptiveField(result, image, radius);
	}

	public static void main(String[] args) throws IOException {// 空间密度，与空间频率
		long t0 = System.currentTimeMillis();
		BufferedImage image = ImageIO.read(new File("D:/file/data/leuvenB.jpg"));
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		VisualTexture.spatialFrequencyAnalysis(result, image);
		ImageIO.write(result, "jpg", new File("D:/file/temp/leuvenB-sf.jpg"));

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Blob, cost: " + (t1 - t0) + "ms.");

		// TODO 从表面亮度分块（经过马赫带亮度处理），然后标记颜色。
		// TODO 如果两个blob之间差异不大，但存在边缘，则强化这个边缘两侧blob的对比度。
	}

}
