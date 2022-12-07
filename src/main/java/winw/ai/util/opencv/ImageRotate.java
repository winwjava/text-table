package winw.ai.util.opencv;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
 
/**
 * 图片翻转、旋转、拉伸
 */
public class ImageRotate extends JFrame {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel imageView;
 
    private double leftTopX = 0;
    private double leftTopY = 0;
    private double rightTopX = 1;
    private double rightTopY = 0;
    private double leftBottomX = 0;
    private double leftBottomY = 1;
 
    private Mat srcMat;
 
    public ImageRotate(String img) {
 
        JLabel resizeLabel = new JLabel("镜像：");
        resizeLabel.setBounds(15, 10, 40, 25);
        resizeLabel.setForeground(Color.BLUE);
 
        JRadioButton resetBtn = new JRadioButton("复位");
        resetBtn.setBounds(80, 10, 65, 25);
        resetBtn.setForeground(Color.BLUE);
        resetBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setIcon(srcMat);
            }
        });
        JRadioButton updownBtn = new JRadioButton("上下");
        updownBtn.setBounds(150, 10, 65, 25);
        updownBtn.setForeground(Color.BLUE);
        updownBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                remapPic(1);
            }
        });
        JRadioButton leftrightBtn = new JRadioButton("左右");
        leftrightBtn.setBounds(215, 10, 65, 25);
        leftrightBtn.setForeground(Color.BLUE);
        leftrightBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                remapPic(2);
            }
        });
        JRadioButton updownleftrightBtn = new JRadioButton("上下左右");
        updownleftrightBtn.setBounds(285, 10, 130, 25);
        updownleftrightBtn.setForeground(Color.BLUE);
        updownleftrightBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                remapPic(3);
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(resetBtn);
        group.add(updownBtn);
        group.add(leftrightBtn);
        group.add(updownleftrightBtn);
 
        JLabel angleLabel = new JLabel("角度:" + 0);
        angleLabel.setBounds(15, 50, 60, 25);
        angleLabel.setForeground(Color.GREEN);
        JSlider angleBar = new JSlider(0, 360);
        angleBar.setValue(0);
        angleBar.setBounds(80, 50, 220, 25);
        angleBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = angleBar.getValue();
                angleLabel.setText("角度:" + value);
                anglePic(value);
            }
        });
 
        JLabel leftTopXLabel = new JLabel("左上X:" + 0);
        leftTopXLabel.setBounds(15, 90, 65, 25);
        JSlider leftTopXBar = new JSlider(0, 10);
        leftTopXBar.setValue(0);
        leftTopXBar.setBounds(70, 90, 100, 25);
        leftTopXBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                leftTopX = leftTopXBar.getValue() / 10d;
                leftTopXLabel.setText("左上X:" + leftTopX);
                wrapPic();
            }
        });
        JLabel leftBottomXLabel = new JLabel("左下X:" + 0);
        leftBottomXLabel.setBounds(205, 90, 65, 25);
        JSlider leftBottomXBar = new JSlider(0, 10);
        leftBottomXBar.setValue(0);
        leftBottomXBar.setBounds(260, 90, 100, 25);
        leftBottomXBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                leftBottomX = leftBottomXBar.getValue() / 10d;
                leftBottomXLabel.setText("左下X:" + leftBottomX);
                wrapPic();
            }
        });
        JLabel rightTopXLabel = new JLabel("右上X:" + 0);
        rightTopXLabel.setBounds(400, 90, 65, 25);
        JSlider rightTopXBar = new JSlider(0, 10);
        rightTopXBar.setValue(10);
        rightTopXBar.setBounds(460, 90, 100, 25);
        rightTopXBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                rightTopX = rightTopXBar.getValue() / 10d;
                rightTopXLabel.setText("右上X:" + rightTopX);
                wrapPic();
            }
        });
 
        JLabel leftTopYLabel = new JLabel("左上Y:" + 0);
        leftTopYLabel.setBounds(15, 120, 65, 25);
        JSlider leftTopYBar = new JSlider(0, 10);
        leftTopYBar.setValue(0);
        leftTopYBar.setBounds(70, 120, 100, 25);
        leftTopYBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                leftTopY = leftTopYBar.getValue() / 10d;
                leftTopYLabel.setText("左上Y:" + leftTopY);
                wrapPic();
            }
        });
        JLabel leftBottomYLabel = new JLabel("左下Y:" + 0);
        leftBottomYLabel.setBounds(205, 120, 65, 25);
        JSlider leftBottomYBar = new JSlider(0, 10);
        leftBottomYBar.setValue(10);
        leftBottomYBar.setBounds(260, 120, 100, 25);
        leftBottomYBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                leftBottomY = leftBottomYBar.getValue() / 10d;
                leftBottomYLabel.setText("左下Y:" + leftBottomY);
                wrapPic();
            }
        });
        JLabel rightTopYLabel = new JLabel("右上Y:" + 0);
        rightTopYLabel.setBounds(400, 120, 65, 25);
        JSlider rightTopYBar = new JSlider(0, 10);
        rightTopYBar.setValue(0);
        rightTopYBar.setBounds(460, 120, 100, 25);
        rightTopYBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                rightTopY = rightTopYBar.getValue() / 10d;
                rightTopYLabel.setText("右上Y:" + rightTopY);
                wrapPic();
            }
        });
 
        srcMat = Imgcodecs.imread(img);
        int width = srcMat.width();
        int height = srcMat.height();
 
        imageView = new JLabel("");
        imageView.setBounds(40, 150, width, height);
 
        this.setTitle("图片翻转、旋转、拉伸");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(width + 100, height + 180);
        this.getContentPane().setLayout(null);
        this.getContentPane().add(resizeLabel);
        this.getContentPane().add(resetBtn);
        this.getContentPane().add(updownBtn);
        this.getContentPane().add(leftrightBtn);
        this.getContentPane().add(updownleftrightBtn);
        this.getContentPane().add(angleLabel);
        this.getContentPane().add(angleBar);
        this.getContentPane().add(leftTopXLabel);
        this.getContentPane().add(leftTopXBar);
        this.getContentPane().add(leftBottomXLabel);
        this.getContentPane().add(leftBottomXBar);
        this.getContentPane().add(rightTopXLabel);
        this.getContentPane().add(rightTopXBar);
        this.getContentPane().add(leftTopYLabel);
        this.getContentPane().add(leftTopYBar);
        this.getContentPane().add(leftBottomYLabel);
        this.getContentPane().add(leftBottomYBar);
        this.getContentPane().add(rightTopYLabel);
        this.getContentPane().add(rightTopYBar);
        this.getContentPane().add(imageView);
 
        setIcon(srcMat);
    }
 
    private void remapPic(int direction) {
        int rows = srcMat.rows();
        int cols = srcMat.cols();
        int type = srcMat.type();
 
        if (1 == 1) { // 方法1
            Mat destMat = new Mat(rows, cols, type);
            if (1 == direction) {
                Core.flip(srcMat, destMat, 0); // 垂直镜像
            } else if (2 == direction) {
                Core.flip(srcMat, destMat, 1); // 水平镜像
            } else {
                Core.flip(srcMat, destMat, -1); // 上下左右镜像
            }
            setIcon(destMat);
            return;
        }
 
        // 方法2
        Mat xMat = new Mat(rows, cols, CvType.CV_32FC1); // 可选：CV_16SC2、CV_32FC1、CV_32FC2 。
        Mat yMat = new Mat(rows, cols, CvType.CV_32FC1); // 可选：CV_16UC1、CV_32FC1 。
        for (int i = 0; i < rows; i++) { // 行
            for (int j = 0; j < cols; j++) { // 列
                if (1 == direction) {                // 上下镜像
                    // int row, int col, double... data
                    xMat.put(i, j, j);               // 行还是原来的行
                    yMat.put(i, j, rows - i); // 列所在的行进行了翻转
                } else if (2 == direction) {             // 左右镜像
                    xMat.put(i, j, cols - j);     // 行所在的列进行了翻转
                    yMat.put(i, j, i);            // 列还是原来的列
                } else { // 上下左右镜像
                    xMat.put(i, j, cols - j);
                    yMat.put(i, j, rows - i);
                }
            }
        }
        Mat destMat = new Mat(rows, cols, type);
        Imgproc.remap(srcMat, destMat, xMat, yMat, 0); // 重映射。根据xMat和yMat将src拷贝到dest。插值方式Imgproc.INTER_NEAREST-0。
        setIcon(destMat);
    }
 
    private void anglePic(int angle) {
        int rows = srcMat.rows();
        int cols = srcMat.cols();
        int type = srcMat.type();
        int scale = 1; // 缩放值
        Mat destMat = new Mat(rows, cols, type);
        Point center = new Point(cols / 2, rows / 2);
        Mat matrix = Imgproc.getRotationMatrix2D(center, angle, scale);
        Imgproc.warpAffine(srcMat, destMat, matrix, destMat.size());
        setIcon(destMat);
    }
 
    private void wrapPic() {
        int cols = srcMat.cols();
        int rows = srcMat.rows();
 
        Point[] srcPoints = new Point[3];
        srcPoints[0] = new Point(0, 0);        // 左上角
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
 
        setIcon(dstMat);
    }
 
    private void setIcon(Mat mat) {
        BufferedImage image = matToBufferedImage(mat); // CVUtil 见 http://www.gaohaiyan.com/3229.html
        imageView.setIcon(new ImageIcon(image));
    }

    /**
     * opencv的像素数据mat转为javaswing可用的image
     *
     * @param mat
     * @return
     */
    public static BufferedImage matToBufferedImage(Mat mat) {
        int cols = mat.cols();
        int rows = mat.rows();
        int elemSize = (int) mat.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type = BufferedImage.TYPE_BYTE_GRAY;
        mat.get(0, 0, data);
        if (3 == mat.channels()) {
            type = BufferedImage.TYPE_3BYTE_BGR;
            byte b;
            for (int i = 0; i < data.length; i = i + 3) { // opencv加载图片像素存在mat中的格式bgr 转为 rgb  
                b = data[i];
                data[i] = data[i + 2];
                data[i + 2] = b;
            }
        }
        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);
        return image;
    }


    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
 
    public static void main(String[] args) {
    	ImageRotate brightness = new ImageRotate("e://bird.png");
        brightness.setVisible(true);
    }
}