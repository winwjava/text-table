package winw.ai.perception.visual.opencv;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class DetectFace extends JPanel {
	private static final long serialVersionUID = 1L;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

//	private static String rtsp_64 = "rtsp://账号:密码@192.168.0.64:554/stream0";
	private static String dir = "D:/file/cv";

	private BufferedImage mImg;

	public static void main(String[] args) {
		try {
			// 获取网络摄像头
			VideoCapture capture = new VideoCapture();
			capture.open(0);
//			capture.open("rtsp://账号:密码@192.168.0.64:554/stream0");

			int height = (int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
			int width = (int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
			if (height == 0 || width == 0) {
				throw new Exception("camera not found!");
			}

			// Java窗口容器
			JFrame frame = new JFrame("camera");
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			// Java画板容器
			DetectFace panel = new DetectFace();
			addMouseListener(panel);
			// 配置关系
			frame.setContentPane(panel);
			frame.setVisible(true);
			frame.setSize(width + frame.getInsets().left + frame.getInsets().right,
					height + frame.getInsets().top + frame.getInsets().bottom);
			int n = 1;
			Mat capImg = new Mat();
			Mat temp = new Mat();
			while (frame.isShowing()) {
				// 把摄像头数据读到Mat
				capture.read(capImg);
				// 彩色空间转换，把图像转换为灰度的、占用空间小的
				Imgproc.cvtColor(capImg, temp, Imgproc.COLOR_RGB2GRAY);
				// temp = capImg.clone();
				// 保存
				Imgcodecs.imwrite(dir + "/back" + n++ + ".png", temp);
				// 进行人脸识别
				Mat mat = detectFace(capImg);
				// 把识别画框图像放在画板上
				panel.mImg = panel.mat2BI(mat);
				// 绘制
				panel.repaint();
			}
			capture.release();
			frame.dispose();
		} catch (Exception e) {
			System.out.println("异常：" + e.getMessage() + " --- " + Arrays.toString(e.getStackTrace()));
		} finally {
			System.out.println("--done--");
		}
	}

	private BufferedImage mat2BI(Mat mat) {
		int dataSize = mat.cols() * mat.rows() * (int) mat.elemSize();
		byte[] data = new byte[dataSize];
		mat.get(0, 0, data);
		int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
		if (type == BufferedImage.TYPE_3BYTE_BGR) {
			for (int i = 0; i < dataSize; i += 3) {
				byte blue = data[i + 0];
				data[i + 0] = data[i + 2];
				data[i + 2] = blue;
			}
		}
		BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
		image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
		return image;
	}

	/**
	 * opencv实现人脸识别
	 *
	 * @param img
	 */
	public static Mat detectFace(Mat img) throws Exception {
		System.out.println("Running DetectFace ... ");
		// 从配置文件lbpcascade_frontalface.xml中创建一个人脸识别器，该文件位于opencv安装目录中
		// CascadeClassifier faceDetector = new
		// CascadeClassifier("D:\\TDDOWNLOAD\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
		CascadeClassifier faceDetector = new CascadeClassifier(
				"D:/Java/opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml");

		// 在图片中检测人脸
		MatOfRect faceDetections = new MatOfRect();

		faceDetector.detectMultiScale(img, faceDetections);

		// System.out.println(String.format("Detected %s faces",
		// faceDetections.toArray().length));

		Rect[] rects = faceDetections.toArray();
		if (rects != null && rects.length >= 1) {
			for (Rect rect : rects) {
				Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
						new Scalar(0, 0, 255), 2);
			}
		}
		return img;
	}

	/**
	 * opencv实现人型识别，hog默认的分类器。所以效果不好。
	 *
	 * @param img
	 */
	public static Mat detectPeople(Mat img) {
		// System.out.println("detectPeople...");
		if (img.empty()) {
			System.out.println("image is exist");
		}
		HOGDescriptor hog = new HOGDescriptor();
		hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
		System.out.println(HOGDescriptor.getDefaultPeopleDetector());
		// hog.setSVMDetector(HOGDescriptor.getDaimlerPeopleDetector());
		MatOfRect regions = new MatOfRect();
		MatOfDouble foundWeights = new MatOfDouble();
		// System.out.println(foundWeights.toString());
		hog.detectMultiScale(img, regions, foundWeights);
		for (Rect rect : regions.toArray()) {
			Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 0, 255), 2);
		}
		return img;
	}

	/**
	 * 画到容器
	 *
	 * @param g
	 */
	public void paintComponent(Graphics g) {
		if (mImg != null) {
			g.drawImage(mImg, 0, 0, mImg.getWidth(), mImg.getHeight(), this);
		}
	}

	/**
	 * 添加鼠标监听
	 *
	 * @param panel
	 */
	private static void addMouseListener(DetectFace panel) {
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("click");
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("mousePressed");
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				System.out.println("mouseReleased");

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				System.out.println("mouseEntered");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				System.out.println("mouseExited");
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("mouseWheelMoved");
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				System.out.println("mouseDragged");
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				System.out.println("mouseMoved");
			}
		});
	}
}
