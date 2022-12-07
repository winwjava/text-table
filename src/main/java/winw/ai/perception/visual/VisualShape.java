package winw.ai.perception.visual;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import winw.ai.model.Graph;

/**
 * 视觉形状，纹理
 * 
 * @author winw
 *
 */
public class VisualShape extends Graph {// 需要支持三维形状

	/**
	 * <p>
	 * 点
	 * <p>
	 * 线，二维边缘，由点连接起来，直线一般是两个点，曲线由多个点连接起来（需要足够多的点）
	 * 
	 * <p>
	 * 面，由若干个三维点组成，比如人脸，需要超过100个点，点越多越清楚。
	 * 
	 * <p>
	 * 三维形状，需要双目摄像头。
	 */
	private List<List<Point>> edgeList;

	public VisualShape() {
		super();
	}

	public VisualShape(List<Point> edge) {
		this.edgeList = new ArrayList<List<Point>>();
		this.edgeList.add(edge);
	}

	public List<List<Point>> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(List<List<Point>> edgeList) {
		this.edgeList = edgeList;
	}

	public static int[] getXPoints(List<Point> edge) {
		int[] pointX = new int[edge.size()];
		for (int i = 0; i < edge.size(); i++) {
			pointX[i] = (int) edge.get(i).x;
		}
		return pointX;
	}

	public static int[] getYPoints(List<Point> edge) {
		int[] pointY = new int[edge.size()];
		for (int i = 0; i < edge.size(); i++) {
			pointY[i] = (int) edge.get(i).y;
		}
		return pointY;
	}

	public Point getCenter(List<Point> points) {
		double centerX = 0.0;
		double centerY = 0.0;
		for (Point point : points) {
			centerX += point.x;
			centerY += point.y;
		}
		return new Point(centerX / points.size(), centerY / points.size());
	}

	/**
	 * 比较两个形状是否相同，或者是否相似。
	 * 
	 * <p>
	 * 判断是否是人脸形状，然后判断是谁。
	 */
	public double match(VisualShape other) {// 用平方差，相关系数等
		return 0;
	}

	// 矩阵变换：缩放、旋转、拉伸

	// 缩放：中心点、缩放尺度

	// 旋转: 中心点、旋转角度

	// 形状比较：是否相似，差别是在哪里？

	// 子集
	// 顶点、凹点、边

	private double leftTopX = 0;
	private double leftTopY = 0;
	private double rightTopX = 1;
	private double rightTopY = 0;

	private double leftBottomX = 0;
	private double leftBottomY = 1;

	public void affine() {// 形状变换

		MatOfPoint srcMat = new MatOfPoint();
		srcMat.fromList(edgeList.get(0));
		int cols = srcMat.cols();
		int rows = srcMat.rows();

		Point[] srcPoints = new Point[3];
		srcPoints[0] = new Point(0, 0); // 左上角
		srcPoints[1] = new Point(cols - 1, 0); // 右上角
		srcPoints[2] = new Point(0, rows - 1); // 左下角
		MatOfPoint2f srcTri = new MatOfPoint2f();
		srcTri.fromArray(srcPoints);

		Point[] dstPoints = new Point[3];
		dstPoints[0] = new Point(cols * leftTopX, rows * leftTopY);
		dstPoints[1] = new Point(cols * rightTopX, rows * rightTopY);
		dstPoints[2] = new Point(cols * leftBottomX, rows * leftBottomY);
		MatOfPoint2f dstTri = new MatOfPoint2f();
		dstTri.fromArray(dstPoints);

		Mat wrapMat = Imgproc.getAffineTransform(srcTri, dstTri); // 根据输入图像的三点坐标计算输出图形的仿射变换矩阵
		Mat dstMat = new Mat(rows, cols, srcMat.type());
		Imgproc.warpAffine(srcMat, dstMat, wrapMat, dstMat.size()); // 根据仿射变换矩阵，从src计算得到dst

//		dstMat
	}
}
