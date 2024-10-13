package winw.ai.perception.visual.occ;

import java.util.ArrayList;
import java.util.List;
import org.opencv.calib3d.Calib3d;
import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class StereoVisionDemo1 {

	static {
//		System.load("E:/projects/demo_3D/lib/opencv_java460.dll");
	}


	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		Mat matR = Imgcodecs.imread("D://file//05R.jpg");
		Mat matL = Imgcodecs.imread("D://file//05L.jpg");

		// 1 图片灰度化
		Mat garyR = new Mat();
		Mat garyL = new Mat();
		Imgproc.cvtColor(matR, garyR, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(matL, garyL, Imgproc.COLOR_BGR2GRAY);

		// 2使用 opencv 的 SIFT 特征检测器来从图像中提取所需的特征点
		SIFT sift = SIFT.create(74);
		MatOfKeyPoint pointR = new MatOfKeyPoint();
		Mat descriptorsR = new Mat();
		sift.detect(garyR, pointR);
		sift.compute(matR, pointR, descriptorsR);

		MatOfKeyPoint pointL = new MatOfKeyPoint();
		sift.detect(garyL, pointL);
		Mat descriptorsL = new Mat();
		sift.compute(matL, pointL, descriptorsL);

		// 3 BFMatcher获取第一组中一个特征的描述符，并使用一些阈值距离计算与第二组中的所有其他特征匹配，并返回最接近的一个。我们将 BFMatches
		// 返回的所有匹配项存储在matches类型的输出匹配变量中。
		BFMatcher matcher = new BFMatcher();
		List<MatOfDMatch> matches = new ArrayList();
		matcher.knnMatch(descriptorsR, descriptorsL, matches, 10);// TODO

		// 4
		// 获取的关键点首先需要转换为cv::Point2f类型，以便与cv::findFundamentalMat一起使用，我们将使用该函数使用我们抽象的这些特征点来计算基本矩阵。两个结果向量Points1和Points2包含两个图像中的对应点坐标。
		MatOfPoint2f pointRs = new MatOfPoint2f();
		MatOfPoint2f pointLs = new MatOfPoint2f();
		for (MatOfDMatch a : matches) {
			MatOfPoint2f ar = getMatOfPoint2fFromDMatchesQuery(a, pointR);
			MatOfPoint2f al = getMatOfPoint2fFromDMatchesQuery(a, pointL);
			pointRs.push_back(ar);
			pointLs.push_back(al);
		}

		// 5
		// 获取的关键点首先需要转换为cv::Point2f类型，以便与cv::findFundamentalMat一起使用，我们将使用该函数使用我们抽象的这些特征点来计算基本矩阵。两个结果向量Points1和Points2包含两个图像中的对应点坐标。
//         匹配像素点   匹配像素点  匹配状态（ inlier 或 outlier)  RANSAC 算法     到对极线的距离  置信度
		Mat fundamental = Calib3d.findFundamentalMat(pointRs, pointLs, Calib3d.FM_RANSAC, 1, 0.98);// TODO

		// 6正如我之前在教程中解释的那样，在实际世界中，获得理想的相机配置而没有任何错误是非常困难的，因此 opencv
		// 提供了一个校正功能，该功能应用单应变换将每个相机的图像平面投影到完美对齐的虚拟平面上. 这种变换是根据一组匹配点和基本矩阵计算得出的。
		Mat h1 = new Mat();
		Mat h2 = new Mat();
		Calib3d.stereoRectifyUncalibrated(pointRs, pointLs, fundamental, matR.size(), h1, h2);
		Mat rectified1 = new Mat();
		Imgproc.warpPerspective(matR, rectified1, h1, matR.size());
		Mat rectified2 = new Mat();
		Imgproc.warpPerspective(matL, rectified2, h2, matL.size());

		// 7最后，我们计算了视差图。
		Mat disparity = new Mat();
		StereoSGBM ss = org.opencv.calib3d.StereoSGBM.create(0, 32, 5);
		ss.compute(rectified1, rectified2, disparity);
		Imgcodecs.imwrite("D://file/demo9.jpg", disparity);
	}

	@SuppressWarnings("unused")
	private static MatOfPoint2f getMatOfPoint2fFromDMatchesTrain(MatOfDMatch matches2, MatOfKeyPoint prevKP2) {
		DMatch dm[] = matches2.toArray();
		List<Point> lp1 = new ArrayList<Point>(dm.length);
		KeyPoint tkp[] = prevKP2.toArray();
		for (int i = 0; i < dm.length; i++) {
			DMatch dmm = dm[i];
			if (dmm.trainIdx < tkp.length)
				lp1.add(tkp[dmm.trainIdx].pt);
		}
		return new MatOfPoint2f(lp1.toArray(new Point[0]));
	}

	private static MatOfPoint2f getMatOfPoint2fFromDMatchesQuery(MatOfDMatch matches2, MatOfKeyPoint actKP2) {
		DMatch dm[] = matches2.toArray();
		List<Point> lp2 = new ArrayList<Point>(dm.length);
		KeyPoint qkp[] = actKP2.toArray();
		for (int i = 0; i < dm.length; i++) {
			DMatch dmm = dm[i];
			if (dmm.queryIdx < qkp.length)
				lp2.add(qkp[dmm.queryIdx].pt);
		}
		return new MatOfPoint2f(lp2.toArray(new Point[0]));
	}
}
