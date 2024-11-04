package winw.ai.perception.visual.opencv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @Description: 傅里叶变换
 * @Author: Dai.GuoWei
 * @Date: 2020/3/3
 */
public class FourierTransformTest {

    public Mat dftStart(Mat img) {
        img.convertTo(img, CvType.CV_32FC1);
        System.out.println("img类型： " + img.type() + " " + img.channels());
        int M = Core.getOptimalDFTSize(img.rows()); // 获得最佳DFT尺寸，为2的次方
        int N = Core.getOptimalDFTSize(img.cols()); // 同上
        Mat padded = new Mat();
        System.out.println("padded 类型： " + padded.size() + " " + padded.type() + " " + padded.channels());
        Core.copyMakeBorder(img, padded, 0, M - img.rows(), 0, N - img.cols(), Core.BORDER_CONSTANT, new Scalar(0)); // opencv中的边界扩展函数，提供多种方式扩展
        System.out.println("padded 类型： " + padded.size() + " " + padded.type() + " " + padded.channels());
        System.out.println("padded 类型： " + padded.size() + " " + padded.type() + " " + padded.channels());
        List<Mat> planes = new ArrayList<Mat>(); // Mat 数组，第一个为扩展后的图像，一个为空图像，
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32FC1));
        Mat complexImg = new Mat();

        System.out
                .println("complexImg 类型： " + complexImg.size() + " " + complexImg.type() + " " + complexImg.channels());
        Core.merge(planes, complexImg); // 合并成一个Mat
        System.out
                .println("complexImg 类型： " + complexImg.size() + " " + complexImg.type() + " " + complexImg.channels());

        Core.dft(complexImg, complexImg); // FFT变换， dft需要一个2通道的Mat

        // compute log(1 + sqrt(Re(DFT(img))**2 + Im(DFT(img))**2))
        Core.split(complexImg, planes); // 分离通道， planes[0] 为实数部分，planes[1]为虚数部分
        Core.magnitude(planes.get(0), planes.get(1), planes.get(0)); // 求模
        Mat mag = planes.get(0);
        Core.add(mag, new Scalar(1), mag);
//        mag += new Scalar(1);                                                                                            
        Core.log(mag, mag); // 模的对数

        // crop the spectrum, if it has an odd number of rows or columns
        mag = new Mat(mag, new Rect(0, 0, mag.cols() & -2, mag.rows() & -2)); // 保证偶数的边长

        int cx = mag.cols() / 2;
        int cy = mag.rows() / 2;

        // rearrange the quadrants of Fourier image //对傅立叶变换的图像进行重排，4个区块，从左到右，从上到下
        // 分别为q0, q1, q2, q3
        // so that the origin is at the image center // 对调q0和q3, q1和q2
        Mat tmp = new Mat();
        Mat q0 = new Mat(mag, new Rect(0, 0, cx, cy));
        Mat q1 = new Mat(mag, new Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(mag, new Rect(0, cy, cx, cy));
        Mat q3 = new Mat(mag, new Rect(cx, cy, cx, cy));

        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);

        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);

       // Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX); // 规范化值到 0~1 显示图片的需要 归一化
        Core.normalize(mag,mag, 0, 255, Core.NORM_MINMAX,CvType.CV_8UC1,new Mat());
        System.out.println("mag 类型： " + mag.size() + " " + mag.type() + " " + mag.channels());
        mag.convertTo(mag, CvType.CV_8U);
        return mag;

    }

    static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat img = Imgcodecs.imread("D:/file/05.jpg");
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_RGB2GRAY);
        FourierTransformTest t = new FourierTransformTest();
        Mat dst = t.dftStart(gray);
        HighGui.imshow("原图", img);
        HighGui.imshow("dft效果图", dst);
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
        System.exit(0);
    }
}