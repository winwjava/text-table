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
 
public class DetectEye {
	private static Mat detectEye(Mat src) {
		Mat dst = src.clone();
 
		CascadeClassifier objDetector = new CascadeClassifier(
				"D:\\opencv\\opencv3.4.3\\install-mk-pc-with-xfeatures2d\\etc\\haarcascades\\haarcascade_eye.xml");
		MatOfRect objDetections = new MatOfRect();
		objDetector.detectMultiScale(dst, objDetections);
		if (objDetections.toArray().length <= 0) {
			return src;
		}
		for (Rect rect : objDetections.toArray()) {
			Imgproc.rectangle(dst, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 0, 255), 2);
		}
		return dst;
	}
 
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\opencv-demo\\anold.jpg");
		if (src.dataAddr() == 0) {
			System.out.println("打开文件出错");
		}
		Mat eyeDtMat = detectEye(src);
		ImageViewer imageViewer = new ImageViewer(eyeDtMat, "第一幅图片");
		imageViewer.imshow();
	}
}