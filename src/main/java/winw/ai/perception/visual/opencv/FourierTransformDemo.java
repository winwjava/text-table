package winw.ai.perception.visual.opencv;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
 
public class FourierTransformDemo {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
 
    public static void main(String[] args) {
        // 读取图像
        Mat src = Imgcodecs.imread("D:/file/05.jpg", Imgcodecs.IMREAD_COLOR);
 
        // 将图像转换为灰度
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
 
        // 对灰度图像进行傅里叶变换
        Mat dft = new Mat();
        Mat dft_shift = new Mat();
        Mat complexI = new Mat();
 
        // 将图像转换为浮点型，进行傅里叶变换
        Core.dft(gray, dft); // FFT变换， dft需要一个2通道的Mat
 
        // 将结果转换为Logarithm scale
        Core.log(dft, dft);
 
        // rearrange the quadrants of the Fourier image 
        // so that the origin is at the image center
        int cx = dft.cols() / 2;
        int cy = dft.rows() / 2;
        Mat q0 = new Mat(dft, new org.opencv.core.Rect(0, 0, cx, cy));
        Mat q1 = new Mat(dft, new org.opencv.core.Rect(cx, 0, cx, cy));
        Mat q2 = new Mat(dft, new org.opencv.core.Rect(0, cy, cx, cy));
        Mat q3 = new Mat(dft, new org.opencv.core.Rect(cx, cy, cx, cy));
        Mat tmp = new Mat();
 
        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);
 
        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
 
        // 归一化
        Core.normalize(dft, dft, 0, 1, Core.NORM_MINMAX);
 
        // 使用IDFT进行逆变换，得到原始图像
        Mat idft = new Mat();
        Core.idft(dft, idft);
 
        // 将结果的最后一个通道去除
        Mat result = new Mat(idft, new org.opencv.core.Range(0, idft.rows()), new org.opencv.core.Range(0, idft.cols()/2));
 
        // 将结果标准化到[0,255]
        Core.normalize(result, result, 0, 255, Core.NORM_MINMAX);
 
        // 将结果转换为8位图像
        Imgcodecs.imwrite("result.jpg", result);
 
        // 清理资源
        src.release();
        gray.release();
        dft.release();
        dft_shift.release();
        complexI.release();
        idft.release();
        result.release();
    }
}