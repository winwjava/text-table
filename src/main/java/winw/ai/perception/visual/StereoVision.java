package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * 立体视觉，是视觉认知中识别物体的必要功能，高度真实的映射了真实世界。
 * 
 * <Point> 三维立体视觉感知，比二维平面多出的一维是深度（depth），即感知到的距离远近。 <Point>
 * 单目线索，可以通过单目视野物体的关联信息形成深度感知。
 * <li>透视 Perspective：线性透视（Linear perspective）、曲线透视（Curvilinear
 * perspective，又称为鱼眼透视）、空气透视（Aerial perspective）
 * <li>相对大小 Relative size
 * <li>相对高度 Relative height
 * <li>遮挡 Occlusion
 * <li>景深 Depth of field：前景清晰、背景较模糊的景深效果能很好地传达距离信息。
 * <li>光照和投影 Light &
 * shading：高光和阴影（shading）是感知物体的形状和位置的重要线索，离光源最近的物体表面往往最亮，远离光源或在暗面的物体亮度降低。
 * <li>纹理渐变 Texture
 * gradient：许多物体表面有一些细微结构，给人以不同材质和触感，我们称之为纹理。纹理随着距离远近产生密度变化的视觉效果：附近物体的精细细节清晰可见，而远处物体的纹理元素变小直到看不清。
 * <li>运动视差 Motion
 * parallax：当头部运动时感知到的差异，不同距离的物体以不同的速度移动，较近的物体似乎比远处的物体移动得更快。运动视差属于单眼线索。在坐火车时这种感觉非常明显：眼前的物体瞬间飞逝，而远处的景物看起来似乎静止不动。
 * <li>调节
 * Accommodation：为了使感知的图像清晰地出现在视网膜上，眼睛的睫状肌根据物体的距离变化而随时调整晶状体的形状（屈光率），从而改变焦距以保持清晰的焦点，这个过程被称为调节。
 * <Point> 双目线索，通过双目近距离的视野成像差异计算深度信息。
 * <li>会聚
 * Convergence：会聚是人在观察物体时两眼之间的夹角，常常又称为辐辏。当视线聚焦在同一个物体上，两个眼球需要相对向内旋转一定角度，以便双目视线相交。
 * <li>视差 Disparity：当双眼注视同一个物体时，左右眼因位置不同会产生视角的差异，左右视网膜成像也会略有不同。
 * 
 * <Point>
 * 
 * 深度线索汇总表 (Doerner 等, 2022)​
 * <table>
 * <tr>
 * <td>深度线索</td>
 * <td>英文术语</td>
 * <td>类型</td>
 * <td>主要影响距离</td>
 * <td>是否反映绝对距离</td>
 * </tr>
 * <tr>
 * <td>线性透视</td>
 * <td>Linear perspective</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>空气透视</td>
 * <td>Aerial perspective</td>
 * <td>单目</td>
 * <td>>30</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>相对大小</td>
 * <td>Relative size</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>相对高度</td>
 * <td>Relative height</td>
 * <td>单目</td>
 * <td>>30m</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>遮挡</td>
 * <td>Occlusion</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>景深</td>
 * <td>Depth of field</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>光照和投影</td>
 * <td>Light & shading</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>纹理渐变</td>
 * <td>Texture gradient</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>运动视差</td>
 * <td>Motion parallax</td>
 * <td>单目</td>
 * <td>>20m</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>调节</td>
 * <td>Accommodation</td>
 * <td>单目</td>
 * <td><2m</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>会聚</td>
 * <td>Convergence</td>
 * <td>双目</td>
 * <td><2m</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>视差</td>
 * <td>Disparity</td>
 * <td>双目</td>
 * <td><10m</td>
 * <td>相对</td>
 * </table>
 * 
 * 
 * 单眼视觉信息线索
 * 运动视差：运动视差即观察者移动时会发现近处的物体看起来比远处的物体移动得更快，这是一种强大的、相对运动的信息线索。
 * 遮挡：当一个物体阻挡了观察者对另一个物体的视线时，便形成遮蔽（又称为干涉）信息线索。此时观察者会感觉到处于阻挡位置的物体比被阻挡的物体更近。
 * 删除和增强：在现实和虚拟的环境中，如果近视野中的物体或表面比远视野中的物体或表面相对于观察者的距离要小很多，则当你移动时远处物体的删除或增强的速度会更快
 * 线性透视：线性透视线条会聚集在远处的某个单一的点，是一种单眼视觉信息线索。
 * 动态深度效应（源于运动的视觉结构）：动态深度效应是由物体的运动形成的对物体的复杂三维结构的感知。没有移动介质很难解释或展示，但你可以想象一个悬空在光和墙壁之间的立方体。
 * 尺寸经验：如果知道远处某个物体的大小，我们的大脑能根据这方面的理解估测绝对距离。
 * 相对尺寸：如果两个物体的尺寸相仿，但因为相对于观察者的距离不同而看起来尺寸不一样，我们便能感知在视网膜上成像比较小的距离较远，而成像较大的则距离较近。这种深度信息线索主要是基于个人经验。
 * 立体透视：立体透视（又称为空气透视）指光因远处物体或场景与观察者之间的空气中有颗粒物（比如水蒸气和烟尘）而发生散射的效应。
 * 纹理递变：纹理递变是指物体的纹理和图案随着相对于观察者的距离增加看起来发生逐渐变化，即从粗大变得细小（或变得越来越不清楚）。
 * 照明/影线/阴影：照明、影线和阴影是感知场景深度和物体几何尺寸的有力信息线索，其效果的变化范围很大。阴影的角度和对比度影响所感知的深度。一个物体因另一个物体而形成的阴影和反射可提供关于距离和位置的信息。如果一个物体的阴影较小、较清晰，则一般表明该物体至阴影投影所在物体或平面的距离较小。类似地，如果增大阴影面积并使阴影的轮廓边缘模糊，在视觉上会感觉深度更大。光与非平整表面的交互方式可在很大程度上显示其几何尺寸和纹理的信息。
 * 
 * 视像扩大：将你的手臂平直伸出，手掌向上，然后将你的手慢慢向脸移动。当你的手越来越靠近，投射于你的视网膜上的图像相应变得越来越大、遮蔽的背景也越来越多。这种视觉信息线索称为视像扩大，这不仅可以帮助观察者感觉物体的移动，还有助于观察者感觉距离。
人在很小的年纪便开始形成这种对动态刺激源的感知，比如，我们可以观察到婴儿在一个物体直接向其移动时会表现出协调一致的防御性反应。
相对高度：在一般的视觉条件下，对于处在同一平面的不同物体，处于近视野者的视网膜成像位于视网膜的下部区域，而处于远视野者的视网膜成像位于视网膜的上部区域。
 * 
 * <Point> 参考：
 * <li>Stefano. Stereo Vision:Algorithms and Applications
 * <li>https://cloud.tencent.com/developer/article/1966973
 * <li>https://zhuanlan.zhihu.com/p/593177344
 * <li>Goldstein, E. B., & Cacciamani, L. (2022). Sensation and perception (11th
 * edition). Cengage.
 * <li>Doerner, R., & Steinicke, F. (2022). Perceptual Aspects of VR. In R.
 * Doerner, W. Broll, P. Grimm, & B. Jung (Eds.), Virtual and Augmented Reality
 * (VR/AR): Foundations and Methods of Extended Realities (XR) (pp. 39–70).
 * Springer International Publishing.
 * https://doi.org/10.1007/978-3-030-79062-2_2
 * <li>Doerner, R., Geiger, C., Oppermann, L., Paelke, V., & Beckhaus, S.
 * (2022). Interaction in Virtual Worlds. In R. Doerner, W. Broll, P. Grimm, &
 * B. Jung (Eds.), Virtual and Augmented Reality (VR/AR): Foundations and
 * Methods of Extended Realities (XR) (pp. 201–244). Springer International
 * Publishing. https://doi.org/10.1007/978-3-0
 * 
 * @author winw
 *
 */
public class StereoVision {
	// 人眼是通过眼部肌肉调焦，计算深度信息？

	// 相机标定：获得相机的焦距、光心坐标、镜头的畸变系数、内参数矩阵、外参数矩阵
	// 用OpenCV来进行相机标定或Matlab中的标定工具箱进行标定

	// TODO 双眼视差，比较方便在10米内视野景物的三维重建。通过常见物体的模型训练学习。

	// 左右两幅图像上的匹配点Y值一定相等，X之间的差别就是视差D

	// 用方形或圆形视野，并根据边缘的分割块，去找到匹配点？

	// 在指定像素坐标周围可以找到和该像素同样视差的像素

	// Disparity-Defined Edges

	// 在V4脑区，用视差边界作为视觉刺激得到的朝向功能图和明暗边界的朝向功能图一致，提示V4对不同来源的边界信息进行了整合。
	// 与此对应的是，在较低级的脑区V1和V2却没有发现这种视差边界的朝向功能图。这表明V4在从视差信息到立体形状信息的
	// 转换过程中发挥了重要的作用。这是首次在灵长类视觉通路中发现基于视差形状信息（shape-from-disparity）的功能结构，
	// 为进一步研究立体形状感知奠定了基础。

	// 我们发现猕猴视觉皮层的第四区（V4）能检测到这种纯粹由视差信息构成的立体边界，而且检测同样边界朝向的神经元聚集在一起，
	// 形成一个朝向功能图。这个视差边界的朝向功能图和明暗边界的朝向功能图一致，提示V4对不同来源的边界信息进行了整合。
	// 与此对应的是，在较低级的脑区V2和V1却没有发现这种立体边界的朝向功能图。

	// 由明暗边界 和 视差信息 在V4脑区整合形成立体形状信息。

	// TODO 多元线性拟合；推测出三维平面或曲面；

	// 块匹配算法计算视差图
	public static int[][] blockMatching(int[][] leftImage, int[][] rightImage, int blockSize, int maxDisparity) {
		int rows = leftImage.length;
		int cols = leftImage[0].length;
		int[][] disparityMap = new int[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int minDiff = Integer.MAX_VALUE;
				int minDisparity = -1;

				for (int d = 0; d <= maxDisparity; d++) {
					int leftBlockSum = blockSum(leftImage, i, j, blockSize);
					int rightBlockSum = blockSum(rightImage, i - d, j, blockSize);

					int diff = Math.abs(leftBlockSum - rightBlockSum);

					if (diff < minDiff) {
						minDiff = diff;
						minDisparity = d;
					}
				}

				disparityMap[i][j] = minDisparity;
			}
		}

		return disparityMap;
	}

	// 计算块的像素和
	private static int blockSum(int[][] image, int row, int col, int blockSize) {
		int sum = 0;
		for (int i = row; i < row + blockSize; i++) {
			for (int j = col; j < col + blockSize; j++) {
				if (i < image.length && j < image[0].length) {
					sum += image[i][j];
				}
			}
		}
		return sum;
	}

	// 视差图转换为深度图
	public static float[][] disparityToDepth(int[][] disparityMap, float focalLength, float baseline) {
		int rows = disparityMap.length;
		int cols = disparityMap[0].length;
		float[][] depthMap = new float[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int disparity = disparityMap[i][j];
				if (disparity > 0) { // 确保视差值有效
					depthMap[i][j] = (focalLength * baseline) / disparity;
				}
			}
		}

		return depthMap;
	}

	/**
	 * 视差图同一个场景在两个相机下成像的像素的位置偏差，因为通常下两个双目相机是水平放置的，所以该位置偏差一般体现在水平方向。比如场景中的X点在左相机是x坐标，那么在右相机成像则是（x+d）坐标。d就是视差图中x坐标点的值。深度图是指场景中每个点离相机的距离。对双目成像来说，视差图和深度图在某种程度上是等价的，知道了两个相机的相关参数，是可以将视差图转换为深度图的。
	 */
	public void disparity() {

	}

	static Random RANDOM = new Random();
	public static int radius = 3;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	// TODO 由 Blob 和 Line 组成。

	public static void disparityImage(BufferedImage imageL, BufferedImage imageR) throws IOException {
		BufferedImage depthImage = new BufferedImage(imageL.getWidth(), imageL.getHeight(), BufferedImage.TYPE_INT_RGB);
		BufferedImage resultImageL = new BufferedImage(imageL.getWidth(), imageL.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		BufferedImage resultImageR = new BufferedImage(imageL.getWidth(), imageL.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		List<Area> blobListL = VisualBlob.colorReceptiveField(resultImageL, imageL);
		List<Area> blobListR = VisualBlob.colorReceptiveField(resultImageR, imageR);

		// 将每个 Blob 与 什么比较一下？

		// 轮廓 ， 形状 有 海马体参与

		// 二维矩阵
		// 用图来表示，这些图形会被展示在海马体中。也会在IT或者MT 中表示。

		// 怎么比较两个图

		// 有若干个图层表示，

		// 根据物体的长宽比例，存储比较，方便查找？方便在海马体中比对（以中轴分开，比较相似性，解决旋转问题）。
		// 比例确定好了之后，更好的去缩放，再比较。

		for (Area blobAreaL : blobListL) {
			if (blobAreaL.getBlobSet().size() <= 1) {
				continue;
			}
			for (Area blobAreaR : blobListR) {

				double aspectRatioDiff = blobAreaL.getAspectRatio() / blobAreaR.getAspectRatio();
				double blobSizeDiff = blobAreaL.getBlobSet().size() / blobAreaR.getBlobSet().size();

				double devX = Math.abs(blobAreaL.getCenterX() - blobAreaR.getCenterX());
				double devY = Math.abs(blobAreaL.getCenterY() - blobAreaR.getCenterY());

				if (devX > 15 && devY > 1) {// Y轴的中心点应该相同，X轴的最大视差不超过15个像素。
					continue;
				}

				if ((aspectRatioDiff > 0.9 && aspectRatioDiff < 1.1) || (blobSizeDiff > 0.9 && blobSizeDiff < 1.1)) {// &&
//					System.out.println("sharpe blob size: " + blobArea.getBlobSet().size() + ", aspect Ratio: " + blobArea.getAspectRatio());
					System.out.println("aspectRatio: " + blobAreaL.getAspectRatio() + "/" + blobAreaR.getAspectRatio()
							+ ", blobSize: " + blobAreaL.getBlobSet().size() + "/" + blobAreaR.getBlobSet().size()
							+ ", Width: " + blobAreaL.getWidth() + "/" + blobAreaR.getWidth() + ", Height: "
							+ blobAreaL.getHeight() + "/" + blobAreaR.getHeight());

					// 应该是Y轴的中心点相同，差1个像素在正常范围内，超过1个像素，说明对比失败。
					System.out.println("CenterX: " + blobAreaL.getCenterX() + "/" + blobAreaR.getCenterX()
							+ ", CenterY: " + blobAreaL.getCenterY() + "/" + blobAreaR.getCenterY() + ", Diff: "
							+ (blobAreaL.getCenterY() - blobAreaR.getCenterY()));

					// FIXME 应该用块？还是用点去比对？先用块试试。

					Color randomColor = new Color(RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());

					// 找到右侧轮廓
					Set<Place> locationSetL = new HashSet<Place>();
					for (Blob colorBlob : blobAreaL.getBlobSet()) {
						int[][] blobArray = colorBlob.getBlob();
						for (int m = 0; m < blobArray.length; m++) {
							for (int n = 0; n < blobArray[m].length; n++) {
								if (blobArray[m][n] > 0) {
									// 把所有点存放在HashSet中，用于找到边界点。
									locationSetL.add(
											new Place(colorBlob.getX0() - radius + m, colorBlob.getY0() - radius + n));
									resultImageL.setRGB(colorBlob.getX0() - radius + m, colorBlob.getY0() - radius + n,
											randomColor.getRGB());
								}
							}
						}
					}
					// 打印右侧轮廓
					List<Place> contoursL = new ArrayList<Place>();
					for (Place point : locationSetL) {
						// 如果一个点四周都有Point，则这个点不是边界。
						if (locationSetL.contains(new Place(point.getX() + 1, point.getY())) // 下
								&& locationSetL.contains(new Place(point.getX() - 1, point.getY())) // 上
								&& locationSetL.contains(new Place(point.getX(), point.getY() + 1))
								&& locationSetL.contains(new Place(point.getX(), point.getY() - 1))) {
							continue;
						} else {
							contoursL.add(point);
							resultImageL.setRGB(point.getX(), point.getY(), 0);
						}
					}

					// 找到右侧轮廓
					Set<Place> locationSet = new HashSet<Place>();
					for (Blob colorBlob : blobAreaR.getBlobSet()) {
						int[][] blobArray = colorBlob.getBlob();
						for (int m = 0; m < blobArray.length; m++) {
							for (int n = 0; n < blobArray[m].length; n++) {
								if (blobArray[m][n] > 0) {
									// 把所有点存放在HashSet中，用于找到边界点。
									locationSet.add(
											new Place(colorBlob.getX0() - radius + m, colorBlob.getY0() - radius + n));
									resultImageR.setRGB(colorBlob.getX0() - radius + m, colorBlob.getY0() - radius + n,
											randomColor.getRGB());
								}
							}
						}
					}

					// 打印右侧轮廓
					List<Place> contours = new ArrayList<Place>();
					for (Place place : locationSet) {
						// 如果一个点四周都有Point，则这个点不是边界。
						if (locationSet.contains(new Place(place.getX() + 1, place.getY())) // 下
								&& locationSet.contains(new Place(place.getX() - 1, place.getY())) // 上
								&& locationSet.contains(new Place(place.getX(), place.getY() + 1))
								&& locationSet.contains(new Place(place.getX(), place.getY() - 1))) {
							continue;
						} else {
							contours.add(place);
							resultImageR.setRGB(place.getX(), place.getY(), 0);
						}
					}

					// 把所有的点，拟合为直线或曲线，这些应该是物体识别的时候用的。

					// FIXME 把相对位置的点，与相对位置的点比较，将这个区块重新划分为若干区区块，按比例找相对位置的点。

					// 按比例找相对位置的点，任意的点都可以找到对应的镜像。
					// 纵轴总的长度与镜像对应的长度，一定是按照比例缩放的。

					for (int m = 0; m < blobAreaR.getBlob().length; m++) {
						for (int n = 0; n < blobAreaR.getBlob()[m].length; n++) {
							if (blobAreaR.getBlob()[m][n] > 0) {

								// 以右侧为主导眼，在右侧画视差。

								// 视差越大，说明越近，视差越小，说明越远。

								// 当前像素在另外一个视角中的位置的X轴差异（Y轴不变）。

								// FIXME 这里视差计算的不对，所有像素都有视差，参考系不对。
								// FIXME 但物体的一半在视野时，左右镜像是不同的，无法比对视差。
								double leftM = 1F * blobAreaL.getWidth() * m / blobAreaR.getWidth(); // X的总长度

								if (Math.abs(blobAreaL.getWidth() - blobAreaR.getWidth()) <= 1) {
									System.out.println("Width: " + blobAreaL.getWidth() + "/" + blobAreaR.getWidth()
											+ ", Height: " + blobAreaL.getHeight() + "/" + blobAreaR.getHeight());
								}

								// 视差

								// 近处的物体颜色更饱和。这是因为随着距离的增加，从物体到眼睛的光通量会减少，导致颜色看起来更淡。
								// FIXME 如果是平面，是按比例缩小的，如果是曲面，应该按照块划分？
								double disparity = Math.abs((blobAreaL.getMinX() + leftM) - (m + blobAreaR.getMinX()));

								// 被摄物体位于两个摄像头的正中间：当物体位于两个摄像头的基线（即两个摄像头中心之间的距离）的正中间时，物体在两个摄像头中的成像位置是相同的，因此视差为0。

								if (disparity < 2) {
									disparity = 2;
								}

								// 人的眼睛可以变焦，将焦距拉

								// 72度小畸变，焦距3.6mm 基线：61mm

								double focalLength = 36; // 相机焦距，单位：300.0F像素
								double baseline = 61; // 相机基线距离

								// 视差转深度，focalLength是焦距，baseline双眼之间的基线距离
								double depth = (focalLength * baseline) / disparity / 2;
								System.out.println(disparity + " -> " + depth);// 深度是3米-5米左右。
//								System.out.println(depth);
								int grayValue = (int) Math.round(depth / 700F * 255) & 0xFF;

								double maxDisparity = 20;// 最大视差。
//								int grayValue = (int) Math.round(255 - disparity / maxDisparity * 255);
								// 设置RGB值（灰度图）
								int rgb = (grayValue << 16) | (grayValue << 8) | grayValue;

								depthImage.setRGB(blobAreaR.getMinX() + m, blobAreaR.getMinY() + n, rgb);
								
								
								// 人类单个眼睛的视场总共是156度。双眼重复的视场总共约为124度。
							}
						}
					}

					break;
				}

			}

		}
		ImageIO.write(resultImageL, "jpg", new File("D:/file/10L-blob.jpg"));
		ImageIO.write(resultImageR, "jpg", new File("D:/file/10R-blob.jpg"));
		ImageIO.write(depthImage, "jpg", new File("D:/file/10D-blob.jpg"));
	}

	public static void findContours(Area blobArea) throws IOException {

	}

	/**
	 * 如果考虑PC1和PC2两个维度构成一个特征平面，那么如下图所示，我们可以认为第一象限对应Network
	 * X脑区，第二象限对应（已知的）Body脑区，第三象限对应（已知的）Face脑区。
	 * 
	 * 将stubby脑区最偏好的100张图片也标记在PC1/PC2平面上，发现它们确实分布在先前缺失的区域中。这很好地验证了IT区域与AlexNet
	 * fc6编码的相似性，验证了将物体编码在该二维平面上的假说。
	 */
	public void planes() {

	}

	public static void main1(String[] args) {

		// 块匹配算法计算视差图
//		int blockSize = 5; // 块的大小
//		int maxDisparity = 16; // 最大视差
//        int[][] disparityMap = blockMatching(preprocessedLeft, preprocessedRight, blockSize, maxDisparity);

		// 视差图转换为深度图
//		float focalLength = 800.0f; // 相机焦距，单位：像素
//		float baseline = 0.1f; // 相机基线距离，单位：米
//        float[][] depthMap = disparityToDepth(disparityMap, focalLength, baseline);

		// 输出深度图
//        for (float[] row : depthMap) {
//            for (float depth : row) {
//                System.out.printf("%.2f ", depth);
//            }
//        }
	}

	public static void main(String[] args) throws IOException {
//		String im = "D:/file/05.jpg";
//		String ot = "D:/file/05-blob.jpg";

//		String im = "D:/file/IMG_20150222_090728.jpg";
//		String ot = "D:/file/temp/IMG_20150222_090728-blob.jpg";

		long t0 = System.currentTimeMillis();
//		BufferedImage image = ImageIO.read(new File(im));
//		FileOutputStream output = new FileOutputStream(new File(ot));
		
		// FIXME 需要从两侧裁剪一段。视野未重叠部分不能做视差计算。
		StereoVision.disparityImage(ImageIO.read(new File("D:/file/05L.jpg")),
				ImageIO.read(new File("D:/file/05R.jpg")));

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Blob, cost: " + (t1 - t0) + "ms.");

	}

}
