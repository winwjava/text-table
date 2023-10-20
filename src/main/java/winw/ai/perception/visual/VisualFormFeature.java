package winw.ai.perception.visual;

import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import winw.ai.model.Graph;

/**
 * 视觉形状，纹理。
 * 
 * <Point>
 * 转换为 1/0 的二维矩阵。以便于做矩阵变换。
 * 
 * <Point>
 * 识别物体的形状，采用特征点的方式（类似人脸识别），例如人脸识别128个特征点。使用参考轴（对称轴、曲轴）和特征点构建形状和纹理特征，手势识别、肢体识别。
 * 
 * <Point>每一个像素点都可以作为特征点，特征角、特征线、特征点。尺度不变性，边缘点 突变点
 * <Point>共线的点，
 * <Point>射影变换，尺度不变特征变换 SIFT(Scale-invariant Feature Transform) 
 * <Point>视觉有：居中、方向、对齐
 * 
 * <Point>每一个特征点，建立置信度评分
 * 
 * 
 * <Point>视觉特征是视觉的辨识基础，形状是更高级的认知。
 * 
 * @author winw
 *
 */
public class VisualFormFeature extends Graph {// 需要支持三维形状

	/**
	 * <Point>
	 * 点
	 * <Point>
	 * 线，二维边缘，由点连接起来，直线一般是两个点，曲线由多个点连接起来（需要足够多的点）
	 * 
	 * <Point>
	 * 面，由若干个三维点组成，比如人脸，需要超过100个点，点越多越清楚。
	 * 
	 * <Point>
	 * 三维形状，需要双目摄像头。
	 */
	private List<List<Point>> edgeList;

	private SimpleMatrix matrix = new SimpleMatrix(210, 210);// 形状矩阵

	public VisualFormFeature() {
		super();
	}

	public VisualFormFeature(List<Point> edge) {
		this.edgeList = new ArrayList<List<Point>>();
		this.edgeList.add(edge);

		// 转换为矩阵
//		matrix.zero(); // 将矩阵元素都设置为0
//		for (List<Point> line : edgeList) {
//			for (Point p : line) {
//				matrix.set((int) p.x, (int) p.y, 1);
//			}
//		}
//		System.out.println(matrix.get(100, 100));
//		System.out.println(matrix);
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

	public Point getCenter(List<Point> ps) {
		double centerX = 0.0;
		double centerY = 0.0;
		for (Point p : ps) {
			centerX += p.x;
			centerY += p.y;
		}
		return new Point(centerX / ps.size(), centerY / ps.size());
	}

	/**
	 * 比较两个形状是否相同，或者是否相似。
	 * 
	 * <Point>
	 * 判断是否是人脸形状，然后判断是谁。
	 */
	public double match(VisualFormFeature other) {// 用平方差，相关系数等
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

	/**
	 * 形状相似度
	 * 
	 * @param other
	 * @return
	 */
	public double similar(VisualFormFeature other) {
//		在算法相似度量中又有以下几种计算方法：
//		1、同现相似度
//		2、欧式距离相似度
//		3、余弦相似度
//		4、皮尔逊相关系数（Pearson）
//		5、修正余弦相似度（Adjusted Cosine）
//		6、汉明距离（Hamming Distance）
//		7、曼哈顿距离（Manhattan Distance）
		return 0;
	}

	public void matrix() {
		SimpleMatrix matrix = new SimpleMatrix(8, 8);

		matrix.fill(4); // 将矩阵元素都设置为4
		matrix.zero(); // 将矩阵元素都设置为0
		matrix.set(1, 4, 3); // 将第一行第四列元素设置为3
		matrix.set(0, 8); // 将第零个元素设置为8

		matrix.rows(1, 2); // 获取矩阵的部分行，第一个数表示开始的那一行，第二个数表示结束那一行加一（这里表示获取第一行）
		matrix.cols(2, 8); // 获取矩阵的部分列，第一个数表示开始的那一列，第二个数表示结束那一列加一（这里表示获取第2,3,4,5,6,7行）
		matrix.get(2, 3); // 获取第二行第三个数
		matrix.get(2); // 获取第二个元素

		matrix.copy(); // 复制一个矩阵
		matrix.createLike(); // 创建一个与matrix相同行列数且元素相同的矩阵

		matrix.numRows(); // 获取矩阵的行数
		matrix.numCols(); // 获取矩阵的列数
		matrix.getNumElements(); // 获取矩阵的元素个数（行数乘以列数）

		matrix.elementMaxAbs(); // 获取矩阵中所有元素绝对值的最大值
		matrix.elementMinAbs(); // 获取矩阵中所有元素绝对值的最小值
		matrix.elementSum(); // 计算矩阵中所有元素的和

		matrix.normF(); // 计算矩阵的二范数（所有元素平方和开根号，可以理解成向量的模）
		matrix.determinant(); // 计算矩阵行列数值

		SimpleMatrix matrixB = new SimpleMatrix(4, 4); // 创建4x4的矩阵B
		SimpleMatrix matrixC = new SimpleMatrix(4, 4); // 创建5x5的矩阵C

		matrixC.fill(1);
		matrixC = matrix.plus(0.6); // A的所有元素乘以0.6
		System.out.println(matrixC);

		// ---------------------------------------------------------------------
		matrixC = matrix.elementDiv(matrixB); // 对应相除 C(i,j) = A(i,j) / B(i,j)
		matrixC = matrix.elementMult(matrixB); // 对应相乘 C(i,j) = A(i,j) * B(i,j)
		matrixC = matrix.elementExp(); // C(i,j) = e 的 A(i,j)次方
		matrixC = matrix.elementLog(); // C(i,j) = ln(C(i,j))
		matrixC = matrix.elementPower(2); // C(i,j) = A(i,j)^2 (对应元素的n次方)
		matrixC = matrix.elementPower(matrixB); // C(i,j) = A(i,j)^(B(i,j)) (对应元素的对应次方)
	}
}
