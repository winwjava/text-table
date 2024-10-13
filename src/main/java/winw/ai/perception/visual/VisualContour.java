package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

/**
 * 视觉轮廓集成（Contour integration）
 * 
 * <h2>轮廓集成</h2>
 * <p>
 * 第一步是识别物体边界，由许多短线段表示，每一个线段都带有特定的方向。第二步是通过轮廓集成处理，识别为特定的物体。
 * <h2>对比度极性（Contrast polarity）</h2>
 * <p>
 * 阴影和遮挡在它们的边缘交叉处具有明显的对比关系，为了正确识别轮廓的尽头，比如轮廓分组和边界归属，大脑必须忽略由于非均匀照明引起的对比度变化，只对由轮廓结束引起的变化做出反应。对比度极性可以用来区分这些情况。
 * 
 * <p>
 * 如果对比度的变化是由于照明的改变，如阴影，则沿轮廓的对比度极性将保持不变。相反，沿轮廓的对比度极性反转通常表示轮廓的尽头。许多表面分层和边界归属感的情况，如阴影、透明性、遮挡和霓虹色扩散，在T形和X形交叉点处具有不同的序数对比度配置（图1）。一个长期存在的问题是视觉皮层如何使用对比度信息来区分这些交叉点奇异点内的不同序数关系。
 * 
 * <p>
 * 末端停止细胞区分对比保存和对比反转连接的能力，从而为深度分组和边界所有权提供必要的信息。 视觉皮层中有明确的交叉点检测器吗？
 * 
 */
public class VisualContour {// 由若干复杂细胞感受野（直线）、超复杂细胞感受野（末端抑制）组成。

	static Random RANDOM = new Random();
	public static int radius = 3;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	// TODO 由 Blob 和 Line 组成。

	public static void findContours(BufferedImage imageL, BufferedImage imageR) throws IOException {
		BufferedImage resultImageL = new BufferedImage(imageL.getWidth(), imageL.getHeight(), imageL.getType());
		BufferedImage resultImageR = new BufferedImage(imageL.getWidth(), imageL.getHeight(), imageL.getType());

		List<BlobArea> blobListL = VisualBlob.colorReceptiveField(resultImageL, imageL);
		List<BlobArea> blobListR = VisualBlob.colorReceptiveField(resultImageR, imageR);

		// 将每个 Blob 与 什么比较一下？

		// 轮廓 ， 形状 有 海马体参与

		// 二维矩阵
		// 用图来表示，这些图形会被展示在海马体中。也会在IT或者MT 中表示。

		// 怎么比较两个图

		// 有若干个图层表示，

		// 根据物体的长宽比例，存储比较，方便查找？方便在海马体中比对（以中轴分开，比较相似性，解决旋转问题）。
		// 比例确定好了之后，更好的去缩放，再比较。

		for (BlobArea blobAreaL : blobListL) {
			if (blobAreaL.getBlobSet().size() <= 1) {
				continue;
			}
			for (BlobArea blobAreaR : blobListR) {

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

								resultImageR.setRGB(blobAreaR.getMinX() + m, blobAreaR.getMinY() + n,
										randomColor.getRGB());
							}
						}
					}

					break;
				}

			}

		}
		FileOutputStream output = new FileOutputStream(new File("D:/file/05L-blob.jpg"));
		ImageIO.write(resultImageL, "jpg", output);
		output.flush();
		output.close();

		ImageIO.write(resultImageR, "jpg", new File("D:/file/05R-blob.jpg"));
	}

	public static void findContours(BlobArea blobArea) throws IOException {

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

	public static void main(String[] args) throws IOException {
//		String im = "D:/file/05.jpg";
//		String ot = "D:/file/05-blob.jpg";

//		String im = "D:/file/IMG_20150222_090728.jpg";
//		String ot = "D:/file/temp/IMG_20150222_090728-blob.jpg";

		long t0 = System.currentTimeMillis();
//		BufferedImage image = ImageIO.read(new File(im));
//		FileOutputStream output = new FileOutputStream(new File(ot));
		VisualContour.findContours(ImageIO.read(new File("D:/file/05L.jpg")),
				ImageIO.read(new File("D:/file/05R.jpg")));

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Blob, cost: " + (t1 - t0) + "ms.");

	}

}
