package winw.ai.util.opencv;


import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FindContoursDemo {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		contour();
	}

	/**
	 * OpenCV-4.0.0  轮廓发现
	 *
	 * @return: void
	 * @date: 2019年12月10日20:17:11
	 */
	public static void contour() {
	    //1 获取原图
	    Mat src = Imgcodecs.imread("e:\\cards.png");
	    //2 图片灰度化
	    Mat gary = new Mat();
	    Imgproc.cvtColor(src, gary, Imgproc.COLOR_RGB2GRAY);
	    //3 图像边缘处理
	    Mat edges = new Mat();
	    Imgproc.Canny(gary, edges, 200, 500, 3, false);
	    //4 发现轮廓
	    List<MatOfPoint> list = new ArrayList<MatOfPoint>();
	    Mat hierarchy = new Mat();
	    Imgproc.findContours(edges, list, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
	    //5 绘制轮廓
	    for (int i = 0, len = list.size(); i < len; i++) {
	        Imgproc.drawContours(src, list, i, new Scalar(0, 255, 0), 1, Imgproc.LINE_AA);
	    }
	    HighGui.imshow("111", src);
	    HighGui.waitKey(0);
	}

}
