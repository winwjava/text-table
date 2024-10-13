package winw.ai.perception.visual.opencv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
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
		Mat src = Imgcodecs.imread("D:/file/05.jpg");
		drawContours(src);
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
		Imgproc.cvtColor(blur, hsv, Imgproc.COLOR_BGR2HSV);

		Mat mask = new Mat();
		// 将所有不在描述对象范围内的其他像素进行覆盖
		Core.inRange(hsv, new Scalar(0, 0, 0), new Scalar(0, 0, 0), mask);
		

//		Mat bitwise = new Mat();
//		Core.bitwise_and(mask, mask, bitwise);// 由Mask作为边界的图像
//		// 3 图像边缘处理
//		Mat edges = new Mat();
//		Mat gary = new Mat();
//		Imgproc.cvtColor(src, gary, Imgproc.COLOR_RGB2GRAY);
//		Imgproc.Canny(gary, edges, 200, 500, 3, false);
		
		

		HighGui.imshow("ImageSegmentation", hsv);
		HighGui.waitKey(0);
		
	}
	
	public static void drawContours(Mat src) {
		// 2 图片灰度化
		Mat gary = new Mat();
		Imgproc.cvtColor(src, gary, Imgproc.COLOR_RGB2GRAY);

		// 高斯滤波，对图像进行模糊操作，以减少图像中的细微差异
		Mat blur = new Mat();
		Imgproc.blur(src, blur, new Size(5, 5));
		Imgproc.GaussianBlur(src, blur, new Size(5, 5), 0);
		
		// 将图像从BGR（蓝绿色红色）转换为HSV（色相饱和度值）
//		Imgproc.cvtColor(src, hsv, cv.COLOR_BGR2HSV);
		
		// 3 图像边缘处理
		Mat edges = new Mat();
		Imgproc.Canny(gary, edges, 200, 500, 3, false);
		// 4 发现轮廓
		List<MatOfPoint> list = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(edges, list, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		// 形状匹配
//	    Imgproc.matchShapes(contour1, contour2, 1, 0.0);
		// 轮廓近似
//	    Imgproc.approxPolyDP(curve, approxCurve, epsilon, closed);
		// 参考：https://zhuanlan.zhihu.com/p/61328775

		// 颜色分割
//		Mat img; // img is given from previos code
//	    Mat hsv = img.clone();
//	    Imgproc.cvtColor(img, hsv, Imgproc.COLOR_BGR2HSV);    
//	    Core.inRange(img, lowerBlue, upperBlue, hsv); //img

		// 5 绘制轮廓
		System.out.println(list.size());
		
		for (int i = 0, len = list.size(); i < len; i++) {

			Imgproc.drawContours(src, list, i, new Scalar(Math.random() * 255,Math.random() * 255, Math.random() * 255), 1, Imgproc.LINE_AA);
//			Imgproc.drawContours(src, list, i, new Scalar(255,255,255), 1, Imgproc.LINE_AA);
		}
		HighGui.imshow("111", src);
//		HighGui.waitKey(0);
	}


}
