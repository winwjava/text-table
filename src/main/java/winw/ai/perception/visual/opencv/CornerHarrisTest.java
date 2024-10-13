package winw.ai.perception.visual.opencv;

import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CornerHarrisTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) throws IOException {
		Mat src=Imgcodecs.imread("D:/file/05.jpg");
		Mat gray=new Mat();
		Mat dst=new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

		Imgproc.cornerHarris(gray, dst, 8,7,0.04);
		Core.normalize(dst, dst, 0, 255,Core.NORM_MINMAX,-1,new Mat());
		Core.convertScaleAbs(dst, dst);

		Mat result=src.clone();
		for (int i = 0,row=result.rows(); i < row; i++) {
			for (int j = 0,col=result.cols(); j < col; j++) {
				if (dst.get(i,j).clone()[0]>130) {
		        	Imgproc.circle(src, new Point(i-3,j+2), 5, new Scalar(0, 0, 255));
//					Imgproc.circle(result, new Point(i-3,j+2), 5, new Scalar(0,0,255),Imgproc.FILLED);
				}
			}
		}

		HighGui.imshow("cornerHarris", result);
		HighGui.waitKey(0);
	}
	

	public static void main1(String[] args) throws IOException {
		Mat src=Imgcodecs.imread("D:/file/05.jpg");
    	Mat gray = new Mat();
    	
    	//转成灰度图
    	Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
    	MatOfPoint corners = new MatOfPoint();
    	//角点检测
    	Imgproc.goodFeaturesToTrack(gray, corners, 100, 0.01, 0.04, new Mat(), 3, false, 0.04);
    	
    	 //绘制角点
        for (int i = 0; i < corners.rows(); i++) {
        	double[] points = corners.get(i,0);
        	Imgproc.circle(src, new Point(points[0], points[1]), 5, new Scalar(0, 0, 255));

		}

		HighGui.imshow("goodFeaturesToTrack", src);
		HighGui.waitKey(0);
	}
}
