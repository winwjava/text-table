package winw.ai.perception.visual.recognition;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.opencv.core.Mat;
 
public class ImageUtils {
 
	/**
	 * 几种常见的图片格式
	 */
	public static String IMAGE_TYPE_GIF = "gif";// 图形交换格式
	public static String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
	public static String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
	public static String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
	public static String IMAGE_TYPE_PNG = "png";// 可移植网络图形
	public static String IMAGE_TYPE_PSD = "psd";// Photoshop的专用格式Photoshop
 
	/**
	 * 缩放图像（按高度和宽度缩放）
	 * 
	 * @param srcImageFile 源图像文件地址
	 * @param result       缩放后的图像地址
	 * @param height       缩放后的高度
	 * @param width        缩放后的宽度
	 * @param bb           比例不对时是否需要补白：true为补白; false为不补白;
	 */
	public final synchronized static Image scale2(Mat mat, int height, int width, boolean bb) throws Exception {
		// boolean flg = false;
		Image itemp = null;
		try {
			double ratio = 0.0; // 缩放比例
			// File f = new File(srcImageFile);
			// BufferedImage bi = ImageIO.read(f);
			BufferedImage bi = OpenCVUtil.matToBufferedImage(mat);
			itemp = bi.getScaledInstance(width, height, bi.SCALE_SMOOTH);
			// 计算比例
			// if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
			// flg = true;
			if (bi.getHeight() > bi.getWidth()) {
				ratio = Integer.valueOf(height).doubleValue() / bi.getHeight();
			} else {
				ratio = Integer.valueOf(width).doubleValue() / bi.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			itemp = op.filter(bi, null);
			// }
			if (bb) {// 补白
				BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null),
							itemp.getHeight(null), Color.white, null);
				g.dispose();
				itemp = image;
			}
			// if (flg)
			// ImageIO.write((BufferedImage) itemp, "JPEG", new File(result));
		} catch (Exception e) {
			throw new Exception("scale2 error: " + e.getMessage(), e);
		}
		return itemp;
	}
 
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
 
		// 此代码确保在图像的所有像素被载入
		image = new ImageIcon(image).getImage();
 
		// 如果图像有透明用这个方法
//		boolean hasAlpha = hasAlpha(image);
 
		// 创建一个可以在屏幕上共存的格式的bufferedimage
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// 确定新的缓冲图像类型的透明度
			int transparency = Transparency.OPAQUE;
			// if (hasAlpha) {
			transparency = Transparency.BITMASK;
			// }
 
			// 创造一个bufferedimage
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// 系统不会有一个屏幕
		}
 
		if (bimage == null) {
			// 创建一个默认色彩的bufferedimage
			int type = BufferedImage.TYPE_INT_RGB;
			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
			// if (hasAlpha) {
			type = BufferedImage.TYPE_INT_ARGB;
			// }
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
 
		// 把图像复制到bufferedimage上
		Graphics g = bimage.createGraphics();
 
		// 把图像画到bufferedimage上
		g.drawImage(image, 0, 0, null);
		g.dispose();
 
		return bimage;
	}
}