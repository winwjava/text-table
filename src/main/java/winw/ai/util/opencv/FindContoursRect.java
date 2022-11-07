package winw.ai.util.opencv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FindContoursRect {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        contour();
    }

    /**
     * OpenCV-4.0.0  轮廓发现
     *
     * @return: void
     * @date: 2019年12月10日20:17:11
     */
    public static void contour() {
        // 1 获取原图
        Mat src = Imgcodecs.imread("e:\\ww.png", Imgcodecs.IMREAD_ANYCOLOR);
        // 2 图片灰度化
        Mat gary = new Mat();
        Imgproc.cvtColor(src, gary, Imgproc.COLOR_RGB2GRAY);
        // 3 图像边缘处理
        Mat edges = new Mat();
        Imgproc.Canny(gary, edges, 200, 500, 3, false);
        // 4 发现轮廓
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat(edges.size(), CvType.CV_32S);
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // 5 绘制轮廓
        for (int i = 0, len = contours.size(); i < len; i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            Imgproc.rectangle(src, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255), 1, Imgproc.LINE_AA);
        }
        // 6 缩小图片
        Imgproc.resize(src, src, new Size(src.width() / 2, src.height() / 2));
        // 7 显示结果
        HighGui.imshow("结果", src);
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
    }
    
}
