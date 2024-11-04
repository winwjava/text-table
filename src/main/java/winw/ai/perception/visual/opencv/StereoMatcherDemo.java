package winw.ai.perception.visual.opencv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.calib3d.StereoSGBM;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class StereoMatcherDemo {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) throws IOException {
//		1. 加载两幅图像，并将它们转换为灰度图像。
		BufferedImage image = ImageIO.read(new File("D:/file/05.jpg"));
		BufferedImage leftImage = image.getSubimage(0, 0, image.getWidth() / 2, image.getHeight());
		BufferedImage rightImage = image.getSubimage(image.getWidth() / 2, 0, image.getWidth() - image.getWidth() / 2,
				image.getHeight());
		ImageIO.write(leftImage, "jpg", new File("D:/file/05L.jpg"));
		ImageIO.write(rightImage, "jpg", new File("D:/file/05R.jpg"));

		Mat imgL = Imgcodecs.imread("D:/file/05L.jpg");
		Mat imgR = Imgcodecs.imread("D:/file/05R.jpg");

		Mat grayL = new Mat();
		Mat grayR = new Mat();
		Imgproc.cvtColor(imgL, grayL, Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(imgR, grayR, Imgproc.COLOR_BGR2GRAY);

		// 矫正畸变 cv2.remap

//		2. 使用OpenCV的StereoSGBM类来创建一个stereo matching实例，并设置参数。

		int blockSize = 3;// #块大小必须为奇数(3-11)
		int img_channels = 3;
		StereoSGBM stereoSGBM = StereoSGBM.create(//63, // 映射滤波器大小，默认15
				0, // #最小视差
				16 * 8, // 视差的搜索范围，16的整数倍
				blockSize, // #块大小必须为奇数(3-11)
				10, // #唯一检测性参数，匹配区分度不够，则误匹配(5-15)
				0, // #视差连通区域像素点个数的大小（噪声点）(50-200)或用0禁用斑点过滤
				1, // #认为不连通(1-2)
				2, // #左右一致性检测中最大容许误差值
				8 * img_channels * blockSize * 2, // #值越大，视差越平滑，相邻像素视差+/-1的惩罚系数
				32 * img_channels * blockSize * 2 // #同上，相邻像素视差变化值>1的惩罚系数
		);

//		3. 调用StereoSGBM类的compute()方法来计算视差图。
		Mat disparity = new Mat();
		stereoSGBM.compute(grayL, grayR, disparity);
//		disparity.convertTo(disparity, CvType.CV_32F, 1.0 / 16);
//		4. 使用OpenCV的可视化函数来显示视差图。
//		Mat disp8U = new Mat(disparity.rows(), disparity.cols(), CvType.CV_8UC1); //显示

		Mat disp8U = new Mat();
		disparity.convertTo(disp8U, CvType.CV_8U, 0);
//		5. 使用OpenCV的可视化函数来显示视差图的深度图。

//		# 得到深度图
//		Core.normalize(disparity, disp8U, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);

		
		Mat result = new Mat();
		Mat xyz = new Mat();
		
		// Q 投影矩阵
//		Calib3d.reprojectImageTo3D(disparity, xyz, result);

		Mat img_pseudocolor = new Mat();
		int tmp=0;
	     for (int y=0;y<disp8U.rows();y++)//转为伪彩色图像的具体算法
	     {
	            for (int x=0;x<disp8U.cols();x++)
	            {
	            	
	            	double[] ds = img_pseudocolor.get(y, x);
	            	
//	            	img_pseudocolor.at<Vec3b>(y,x)[0] = abs(255-tmp); //blue
//	                   img_pseudocolor.at<Vec3b>(y,x)[1] = abs(127-tmp); //green
//	                    img_pseudocolor.at<Vec3b>(y,x)[2] = abs( 0-tmp); //red
//	                   img_pseudocolor.at<Vec3b>(y,x)[0] = abs(255-tmp); //blue
//	                   img_pseudocolor.at<Vec3b>(y,x)[1] = abs(127-tmp); //green
//	                    img_pseudocolor.at<Vec3b>(y,x)[2] = abs( 0-tmp); //red
	            }
	     }
		
		HighGui.imshow("StereoSGBM", disp8U);
		HighGui.waitKey(0);
	}
}
