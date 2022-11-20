package winw.ai.util.opencv;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import winw.ai.perception.visual.recognition.VideoPanel;

/**
 * https://blog.csdn.net/FRYAN28/article/details/107393139
 * @author winw
 *
 */
public class FaceCollect {

	private static int startFrom = 0;

	private static int sample = 0;

	//配置保存路径
	public static String path = "F:/face/imagedb";

	public static String id = "";

	static AtomicBoolean start = new AtomicBoolean(false);

	public static long startTime;

	public static void main(String[] args) throws IOException {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier faceCascade = new CascadeClassifier();
		faceCascade.load("F:/face/install/etc/haarcascades/haarcascade_frontalface_alt.xml");
		//控制台输入对象名
		System.out.println("Please input person name: ");
		Scanner scanner = new Scanner(System.in);
		id = scanner.next();
		System.out.println("collecting images for : " + id);
		VideoCapture capture = new VideoCapture();
		capture.open(0, Videoio.CAP_FFMPEG);

		scanner.close();
		// 新建窗口
		VideoPanel videoPanel = VideoPanel.show("Collect Data", 640, 480, 0);
		Mat img = new Mat();
		try {
			startTime = System.currentTimeMillis();
			while (true) {
				capture.read(img);
				detectAndCollect(img, faceCascade);
				videoPanel.setImageWithMat(img);
			}
		} finally {
			capture.release();
		}

	}

	/**
	 * 采集人脸并保存到本地的方法
	 **/
	public static void detectAndCollect(Mat frame, CascadeClassifier faceCascade) {
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();

		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

		// 采集人脸
		faceCascade.detectMultiScale(grayFrame, faces);

		Rect[] facesArray = faces.toArray();

// 连续采集50张，并保存
		if (facesArray.length >= 1) {
			if ((sample == 0 && System.currentTimeMillis() - startTime > 10000)
					|| (sample > 0 && sample < 50 && System.currentTimeMillis() - startTime > 300)) {
				startTime = System.currentTimeMillis();
				sample++;
				System.out.println("image: " + sample);
				Imgcodecs.imwrite(path + "/image." + id + "." + (startFrom + sample) + ".jpg",
						frame.submat(facesArray[0]));
			}
		}

		for (int i = 0; i < facesArray.length; i++) {
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 2);
		}
	}

}
