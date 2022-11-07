package winw.ai.perception.visual.recognition;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
 
public class DetectFace {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//Mat src = Imgcodecs.imread("/Users/chrishu123126.com/opt/img/detect-face-4.jpg");
		Mat src = Imgcodecs.imread("D:\\opencv-demo\\green-arrow.jpg");
		if (src.empty()) {
			System.out.println("图片路径不正确");
			return;
		}
		Mat dst = dobj(src);
		ImageViewer imageViewer = new ImageViewer(dst, "识脸");
		imageViewer.imshow();
	}
 
	private static Mat dobj(Mat src) {
		Mat dst = src.clone();
 
		CascadeClassifier objDetector = new CascadeClassifier(
				"D:\\opencvinstall\\build\\install\\etc\\lbpcascades\\lbpcascade_frontalface.xml");
 
		MatOfRect objDetections = new MatOfRect();
 
		objDetector.detectMultiScale(dst, objDetections);
 
		if (objDetections.toArray().length <= 0) {
			return src;
		}
 
		for (Rect rect : objDetections.toArray()) {
			Imgproc.rectangle(dst, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.width),
					new Scalar(0, 255, 0), 1); //new Scalar(0, 255, 0), 1)绿 //new Scalar(0, 0, 255), 1)红 //new Scalar(255, 0, 0), 1)蓝
		}
		return dst;
	}
}