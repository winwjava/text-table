package winw.ai.perception.visual.opencv;

import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class HoughLines {
	static {// 在使用OpenCV前必须加载Core.NATIVE_LIBRARY_NAME类,否则会报错
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		houghLines();
	}
	

	static Random RANDOM = new Random();
	
	public static void houghLines() {
		Mat src = Imgcodecs.imread("D:/file/05.jpg");
		Mat gary = new Mat();
		Mat lines = new Mat();
		// 1.边缘处理
		Imgproc.Canny(src, gary, 100, 200);
		// 2.霍夫变换-直线检测
		Imgproc.HoughLinesP(gary, lines, 1, Imgproc.HOUGH_GRADIENT / 180.0, 100, 0, 0);
		double[] data;
		for (int i = 0, len = lines.rows(); i < len; i++) {
			data = lines.get(i, 0).clone();
			
			Imgproc.line(src, new Point((int) data[0], (int) data[1]), new Point((int) data[2], (int) data[3]),
					new Scalar(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)), 2, Imgproc.LINE_AA);
		}
		for (int i = 0, len = lines.cols(); i < len; i++) {
			data = lines.get(0, i).clone();
			
			Imgproc.line(src, new Point((int) data[0], (int) data[1]), new Point((int) data[2], (int) data[3]),
					new Scalar(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)), 2, Imgproc.LINE_AA);
		}
		HighGui.imshow("直线检测", src);
		HighGui.waitKey(0);
	}
}
