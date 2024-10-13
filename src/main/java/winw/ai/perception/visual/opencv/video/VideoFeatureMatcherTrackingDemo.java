package winw.ai.perception.visual.opencv.video;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 * 特征匹配，ORB（Oriented FAST and Rotated BRIEF）算法
 * 
 * <p>
 * 参考：https://blog.csdn.net/matt45m/article/details/137400217
 */
public class VideoFeatureMatcherTrackingDemo {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		Mat src = Imgcodecs.imread("D:/file/0511orb.jpg");
		Mat gray = new Mat();
		Mat rgb = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(src, rgb, Imgproc.COLOR_BGR2RGB);

//    	# 这是目标图像
//    	image = cv2.imread("helicopter_roi.png")
//    	gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
//    	rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
//    	plt.imshow(rgb_image)
//
//    	# 初始化ORB
//    	orb = cv2.ORB_create()

		ORB orbDetector = ORB.create();
		MatOfKeyPoint keyPointsPrev = new MatOfKeyPoint();
		MatOfKeyPoint keyPointsCurr = new MatOfKeyPoint();
		Mat descriptorsPrev = new Mat();
		MatOfDMatch matches = new MatOfDMatch();
		orbDetector.detectAndCompute(gray, new Mat(), keyPointsPrev, matches);
//        orbDetector.detectAndCompute(gray, gray, null, rgb);
//
//    	# 使用ORB检测关键点
//    	keypoints_1, descriptors_1 = orb.detectAndCompute(gray_image, None)
//
//    	# 仅绘制关键点位置，不包括大小和方向
//    	img2 = cv2.drawKeypoints(rgb_image, keypoints, None, color=(0, 255, 0), flags=0)
		// 绘制匹配
		MatOfDMatch goodMatches = new MatOfDMatch();
		// 绘制匹配结果
		Features2d.drawMatches(gray, keyPointsPrev, gray, keyPointsCurr, matches, goodMatches, new Scalar(0, 255, 0),
				new Scalar(255, 0, 0));

		//Features2d.drawMatches(rgb, keyPointsPrev, gray, keyPointsCurr, matches, rgb);// , new Scalar(0, 255, 0), new
																						// Scalar(255, 0, 0),
																						// Imgcodecs.IMWRITE_JPEG_QUALITY
		// 显示图像
		HighGui.imshow("Good Matches", rgb);
		HighGui.waitKey(1);

		// 初始化ORB检测器
		ORB orb = ORB.create();

		BFMatcher bfMatcher = BFMatcher.create();
//        for(;;) {
//        	
//        }

		// 创建描述符匹配器
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

		// 读取视频
        VideoCapture video = new VideoCapture("D:/filemda-jcdn2ibidgawc4jy.mp4"); // 替换为视频路径

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
//        MatOfKeyPoint keyPointsPrev = new MatOfKeyPoint();
//        MatOfKeyPoint keyPointsCurr = new MatOfKeyPoint();
//        Mat descriptorsPrev = new Mat();
//        Mat descriptorsCurr = new Mat();
//        MatOfDMatch matches = new MatOfDMatch();
// 
//        // 读取第一帧
//        if (video.read(frame)) {
//            // 转换为灰度
//            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
// 
//            // 检测关键点并计算描述符
//            orbDetector.detectAndCompute(grayFrame, new Mat(), keyPointsPrev, descriptorsPrev);
// 
//            // 对于视频的其他帧
//            while (video.read(frame)) {
//                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
// 
//                // 检测当前帧的关键点
//                orbDetector.detect(grayFrame, keyPointsCurr);
// 
//                // 计算当前帧的描述符
//                orbDetector.compute(grayFrame, keyPointsCurr, descriptorsCurr);
// 
//                // 匹配描述符
//                descriptorMatcher.match(descriptorsPrev, descriptorsCurr, matches);
// 
//                // 绘制匹配
//                Mat goodMatches = new Mat();
//                Features2d.drawMatches(grayFrame, keyPointsPrev, grayFrame, keyPointsCurr, matches, goodMatches);//, new Scalar(0, 255, 0), new Scalar(255, 0, 0), Imgcodecs.IMWRITE_JPEG_QUALITY
// 
//                // 更新上一帧数据
//                keyPointsPrev = keyPointsCurr;
//                descriptorsPrev = descriptorsCurr;
//                matches.copyTo(goodMatches);
// 
//                // 显示图像
//                HighGui.imshow("Good Matches", goodMatches);
//                HighGui.waitKey(1);
//            }
//        }
// 
//        // 释放资源
//        video.release();
//        frame.release();
//        grayFrame.release();
//        keyPointsPrev.release();
//        keyPointsCurr.release();
//        descriptorsPrev.release();
//        descriptorsCurr.release();
	}

	public static void main1(String[] args) {
		// 读取模板图像和待检测图像
		Mat templateImage = Imgcodecs.imread("D:/file/05t.jpg", Imgcodecs.IMREAD_GRAYSCALE);
		Mat image = Imgcodecs.imread("D:/file/05.jpg", Imgcodecs.IMREAD_GRAYSCALE);

		// 创建ORB检测器
		ORB orb = ORB.create();

		// 检测关键点
		MatOfKeyPoint templateKeyPoints = new MatOfKeyPoint();
		MatOfKeyPoint keyPoints = new MatOfKeyPoint();
		orb.detect(templateImage, templateKeyPoints);
		orb.detect(image, keyPoints);

		// 计算描述符
		Mat templateDescriptors = new Mat();
		Mat descriptors = new Mat();
		orb.compute(templateImage, templateKeyPoints, templateDescriptors);
		orb.compute(image, keyPoints, descriptors);

		// 创建描述符匹配器
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

		// 执行匹配
		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(descriptors, templateDescriptors, matches);

		// 绘制匹配结果
		Mat outputImage = new Mat();

		Features2d.drawMatches(image, keyPoints, templateImage, templateKeyPoints, matches, outputImage);

		// 显示或保存结果
//        Imgcodecs.imwrite("output.jpg", outputImage);

		HighGui.imshow("LineSegmentDetector", outputImage);
		HighGui.waitKey(0);
		// 释放资源
		templateImage.release();
		image.release();
		templateKeyPoints.release();
		keyPoints.release();
		templateDescriptors.release();
		descriptors.release();
		matches.release();
		outputImage.release();
	}
}
