package winw.ai.util.opencv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import winw.ai.perception.visual.VisualFeature;
import winw.ai.perception.visual.VisualShapePanel;

public class ImageMatchTemplate {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private Mat src;
	private Mat srcBlur = new Mat();
	private Mat detectedEdges = new Mat();
	private Mat dst = new Mat();
	private static final Size BLUR_SIZE = new Size(3, 3);
	private int lowThresh = 0;
	private static final int RATIO = 3;
	private static final int KERNEL_SIZE = 3;

	public static List<VisualFeature> shapeList = new ArrayList<VisualFeature>();

	private synchronized void getShapeList() {
		String imagePath = "e:\\ww.png";
		src = Imgcodecs.imread(imagePath);
		// 高斯 滤波 降噪
		Imgproc.blur(src, srcBlur, BLUR_SIZE);// 先做模糊

		// Canny 算法 ，边缘检测。
		Imgproc.Canny(srcBlur, detectedEdges, lowThresh, lowThresh * RATIO, KERNEL_SIZE, false);

		// 膨胀，连接边缘
		Imgproc.dilate(srcBlur, srcBlur, new Mat(), new Point(-1, -1), 3, 1, new Scalar(1));

		dst = new Mat(src.size(), CvType.CV_8UC1, Scalar.all(0));
		src.copyTo(dst, detectedEdges);

		Mat hierarchy = new Mat();
		List<MatOfPoint> contours = new ArrayList<>();// 轮廓
		// 只提取外部的轮廓。
		Imgproc.findContours(detectedEdges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// 处理轮廓，匹配形状。
//		processContours(hierarchy, contours);

//	    Imgproc.findContours(detectedEdges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		System.out.println("contours size: " + contours.size());

		for (int i = 0, len = contours.size(); i < len; i++) {// 5 绘制轮廓
			VisualShapePanel.show("MatOfPoint", new VisualFeature(contours.get(i).toList()));
			shapeList.add(new VisualFeature(contours.get(i).toList()));
		}
		
		// 只能是边缘与边缘的形状比较。
	}

	public static void main(String[] args) {
		ImageMatchTemplate imageMatchTemplate = new ImageMatchTemplate();
		imageMatchTemplate.getShapeList();
		
		// 待匹配图片
		Mat src = Imgcodecs.imread("e:\\ww.png");// ,Imgcodecs.IMREAD_GRAYSCALE
		Mat src_img = src.clone();
		// 获取匹配模板
		
		MatOfPoint matOfPoint = new MatOfPoint();
		matOfPoint.fromList(shapeList.get(0).getEdgeList().get(0));
		
		Mat template = matOfPoint.t();
//		Mat template = Imgcodecs.imread("e:\\wwtest.png");// ,Imgcodecs.IMREAD_GRAYSCALE
		/**
		 * TM_SQDIFF = 0, 平方差匹配法，最好的匹配为0，值越大匹配越差 TM_SQDIFF_NORMED = 1,归一化平方差匹配法 TM_CCORR
		 * = 2,相关匹配法，采用乘法操作，数值越大表明匹配越好 TM_CCORR_NORMED = 3,归一化相关匹配法 TM_CCOEFF =
		 * 4,相关系数匹配法，最好的匹配为1，-1表示最差的匹配 TM_CCOEFF_NORMED = 5;归一化相关系数匹配法
		 */
		int method = Imgproc.TM_CCORR_NORMED;
		// 创建32位模板匹配结果Mat
		Mat result = new Mat(src.rows(), src.cols(), CvType.CV_32FC1);
		/*
		 * 将模板与重叠的图像区域进行比较。
		 * 
		 * @param image运行搜索的图像。 它必须是8位或32位浮点。
		 * 
		 * @param templ搜索的模板。 它必须不大于源图像并且具有相同的数据类型。
		 * 
		 * @param result比较结果图。 它必须是单通道32位浮点。 如果image是（W * H）并且templ是（w * h），则结果是（（W-w +
		 * 1）*（H-h + 1））。
		 * 
		 * @param方法用于指定比较方法的参数，请参阅默认情况下未设置的#TemplateMatchModes。
		 * 当前，仅支持#TM_SQDIFF和#TM_CCORR_NORMED方法。
		 */
		Imgproc.matchTemplate(src, template, result, method);
		// 归一化 详见https://blog.csdn.net/ren365880/article/details/103923813
		Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

		// 获取模板匹配结果 minMaxLoc寻找矩阵(一维数组当作向量,用Mat定义) 中最小值和最大值的位置.
		Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

		// 绘制匹配到的结果 不同的参数对结果的定义不同
		Point p = method == Imgproc.TM_SQDIFF_NORMED || method == Imgproc.TM_SQDIFF ? mmr.minLoc : mmr.maxLoc;
		System.out.println(mmr.minVal + ", " + mmr.maxVal);// 相似度
		System.out.println(mmr.minLoc.x + ", " + mmr.minLoc.y);
		System.out.println(mmr.maxLoc.x + ", " + mmr.maxLoc.y);
		System.out.println(template.cols() + ", " + template.rows());
		System.out.println(src.cols() + ", " + src.rows());
		System.out.println((result.cols()) + ", " + (result.rows()));
		System.out.println((src.cols() - result.cols()) + ", " + (src.rows() - result.rows()));

		// Imgproc.rectangle(src,new Point(x,y),new
		// Point(x+template.cols(),y+template.rows()),new Scalar( 0, 0,
		// 255),2,Imgproc.LINE_AA);
		Imgproc.rectangle(src, p, new Point(p.x + template.cols(), p.y + template.rows()), new Scalar(0, 0, 255), 2);

		HighGui.imshow("模板匹配结果", src);
		HighGui.imshow("模板", template);
		HighGui.imshow("原图像", src_img);
		HighGui.waitKey();
	}
}