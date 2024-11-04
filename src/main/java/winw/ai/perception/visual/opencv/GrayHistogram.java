package winw.ai.perception.visual.opencv;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @program: learn-opencv
 * @description: 绘制图片直方图
 * @author: Mr.Dai
 * @create: 2020-03-03 16:30
 **/
public class GrayHistogram {
    static{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        Mat imread = Imgcodecs.imread("D:/file/05.jpg");
        HighGui.imshow(" 原图像",imread);
        plotGrayHistogram(imread);
        // 无限等待按键按下
        HighGui.waitKey(0);
    }

    public static void plotGrayHistogram(Mat img) {
        java.util.List<Mat> images = new ArrayList<>();
        images.add(img);
        MatOfInt channels = new MatOfInt(0); // 图像通道数，0表示只有一个通道
        MatOfInt histSize = new MatOfInt(256); // CV_8U类型的图片范围是0~255，共有256个灰度级
        Mat histogramOfGray = new Mat(); // 输出直方图结果，共有256行，行数的相当于对应灰度值，每一行的值相当于该灰度值所占比例
        MatOfFloat histRange = new MatOfFloat(0, 255);
        Imgproc.calcHist(images, channels, new Mat(), histogramOfGray, histSize, histRange, false);  // 计算直方图
        // 按行归一化
        Core.normalize(histogramOfGray, histogramOfGray, 0, histogramOfGray.rows(), Core.NORM_MINMAX, -1, new Mat());

        // 创建画布
        int histImgRows = 300;
        int histImgCols = 300;
        int colStep = (int) Math.floor(histImgCols / histSize.get(0, 0)[0]);
        Mat histImg = new Mat(histImgRows, histImgCols, CvType.CV_8UC3, new Scalar(255,255,255));  // 重新建一张图片，绘制直方图
        for (int i = 0; i < histSize.get(0, 0)[0]; i++) {  // 画出每一个灰度级分量的比例，注意OpenCV将Mat最左上角的点作为坐标原点
            Imgproc.line(histImg,
                    new org.opencv.core.Point(colStep * i, histImgRows - 20),
                    new org.opencv.core.Point(colStep * i, histImgRows - Math.round(histogramOfGray.get(i, 0)[0]) - 20),
                    new Scalar(0, 0,0), 2,8,0);
            if (i%50 == 0) {
                Imgproc.putText(histImg, Integer.toString(i), new org.opencv.core.Point(colStep * i, histImgRows - 5), 1, 1, new Scalar(0, 0, 0));  // 附上x轴刻度
            }
        }
        //显示出来  对namedWindos 与cv::imshow 封装
        HighGui.imshow("Gray Histogram",histImg);
    }
}