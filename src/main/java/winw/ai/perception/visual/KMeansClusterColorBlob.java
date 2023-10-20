package winw.ai.perception.visual;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * KMeans聚类颜色分割
 * 
 * @author winw
 *
 */
public class KMeansClusterColorBlob {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) throws IOException {
		Mat src = Imgcodecs.imread("E:/IMG/0612.jpg");

		getKmeans(src);
	}

	public static void getKmeans(Mat image) throws IOException {
		int width = image.width();
		int height = image.height();
		int pointCount = width * height;

		Mat points = image.reshape(image.channels(), pointCount);
		points.convertTo(points, CvType.CV_32F);

		Mat bestLabels = new Mat();

		TermCriteria criteria = new TermCriteria(TermCriteria.COUNT + TermCriteria.EPS, 10, 0.1);
		Core.kmeans(points, 7, bestLabels, criteria, 3, Core.KMEANS_RANDOM_CENTERS);

		double[][] color = { { 0, 0, 255 }, { 0, 255, 0 }, { 255, 0, 0 }, { 0, 255, 255 }, { 0, 128, 128 },
				{ 128, 128, 0 }, { 128, 128, 128 } };

		Mat result = Mat.zeros(image.size(), image.type());

		int index = 0;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				index = i * width + j;
				// 获取聚类标记的点
				int lable = (int) bestLabels.get(index, 0)[0];
				// 为聚类相同的像素点填充颜色
				result.put(i, j, color[lable]);
			}
		}

		BufferedImage img = (BufferedImage) HighGui.toBufferedImage(result);
		FileOutputStream output = new FileOutputStream(new File("E:/IMG/202308-kmeans.jpg"));
		ImageIO.write(img, "jpg", output);
		HighGui.imshow("zero", result);
	}
}
