package winw.ai.perception.opencv;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class OpenCVTest {

	// 图像腐蚀/膨胀处理
	public void erodeImg() {
		Mat mat = new Mat();
		Mat outImage = new Mat();

		// size 越小，腐蚀的单位越小，图片越接近原图
		Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 2));

		/**
		 * 图像腐蚀 腐蚀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`小`值并赋值给指定区域。 腐蚀可以理解为图像中`高亮区域`的'领域缩小'。
		 * 意思是高亮部分会被不是高亮部分的像素侵蚀掉，使高亮部分越来越少。
		 */
		Imgproc.erode(mat, outImage, structImage);
		mat = outImage;

		/**
		 * 膨胀 膨胀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`大`值并赋值给指定区域。 膨胀可以理解为图像中`高亮区域`的'领域扩大'。
		 * 意思是高亮部分会侵蚀不是高亮的部分，使高亮部分越来越多。
		 */
		Imgproc.dilate(mat, outImage, structImage);
		mat = outImage;

	}

}
