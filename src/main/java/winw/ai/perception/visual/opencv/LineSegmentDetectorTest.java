package winw.ai.perception.visual.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.LineSegmentDetector;

public class LineSegmentDetectorTest {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		Mat src=Imgcodecs.imread("D:/file/05.jpg");
		Mat gray=new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		
		LineSegmentDetector lineSegmentDetector = Imgproc.createLineSegmentDetector();
		
		Mat line=src.clone();
		lineSegmentDetector.detect(gray, line);
	 
		lineSegmentDetector.drawSegments(src, line);
		
		
		HighGui.imshow("LineSegmentDetector", src);
		HighGui.waitKey(0);
	}

}
