package winw.ai.util.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 颜色区域划分。
 * 
 * @author winw
 *
 */
public class ImageSegmentationUseColor {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		regionDivision();
	}

	public static void regionDivision() {
		// 1 获取原图
		Mat src = Imgcodecs.imread("e:\\bird.png");
		// 2 图片灰度化
//		Mat gary = new Mat();
//		Imgproc.cvtColor(src, gary, Imgproc.COLOR_RGB2GRAY);

		// 高斯滤波，对图像进行模糊操作，以减少图像中的细微差异
		Mat blur = new Mat();
		Mat blur1 = new Mat();
		Mat blur2 = new Mat();
		Mat blur3 = new Mat();
		Imgproc.blur(src, blur, new Size(5, 5));
		Imgproc.medianBlur(blur, blur1, 5);
		Imgproc.GaussianBlur(blur1, blur2, new Size(5, 5), 0);
		Imgproc.bilateralFilter(blur2, blur3, 9,75,75);
		

		// 将图像从BGR（蓝绿色红色）转换为HSV（色相饱和度值）

		// 颜色分割
		Mat hsv = new Mat();
		Imgproc.cvtColor(blur3, hsv, Imgproc.COLOR_BGR2HSV);

		Mat mask = new Mat();
		// 将所有不在描述对象范围内的其他像素进行覆盖
		Core.inRange(hsv, new Scalar(55, 0, 0), new Scalar(118, 255, 255), mask);
		

		Mat bitwise = new Mat();
		Core.bitwise_and(mask, mask, bitwise);// 由Mask作为边界的图像
		// 3 图像边缘处理
//		Mat edges = new Mat();
//		Imgproc.Canny(gary, edges, 200, 500, 3, false);

		// 形状匹配
//	    Imgproc.matchShapes(contour1, contour2, 1, 0.0);
		// 轮廓近似
//	    Imgproc.approxPolyDP(curve, approxCurve, epsilon, closed);
		// 参考：https://zhuanlan.zhihu.com/p/61328775

		HighGui.imshow("ImageSegmentation", hsv);
		HighGui.waitKey(0);
	}

}
