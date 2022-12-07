package winw.ai.util.opencv;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

/**
 * HOGDescriptor 自带行人检测
 */
public class HOGDescriptorDemo {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		runner();
	}

	/**
	 * OpenCV-4.1.0 HOGDescriptor 自带行人检测
	 * @return: void  
	 * @date: 2019年8月4日10:09:58
	 */
	public static void runner() {
		Mat src=Imgcodecs.imread("E:\\6C0608F0E81D321BF0328075B5CC5619_0.jpg");
		Mat gary=new Mat();
		Imgproc.cvtColor(src, gary, Imgproc.COLOR_BGR2GRAY);

		HOGDescriptor hog=new HOGDescriptor();
		hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());

		MatOfRect rect=new MatOfRect();
		hog.detectMultiScale(gary, rect, new MatOfDouble(),1.05,new Size(4,4),new Size(32,32));

		Rect[] rects = rect.toArray();

		for (int i = 0; i < rects.length; i++) {
			Imgproc.rectangle(src, new Point(rects[i].x,rects[i].y), new Point(rects[i].x+rects[i].width,rects[i].y+rects[i].height), new Scalar(0,0,255), 2, Imgproc.LINE_AA);
		}

		HighGui.imshow("HOG行人检测", src);
		HighGui.waitKey(1);
	}
}
