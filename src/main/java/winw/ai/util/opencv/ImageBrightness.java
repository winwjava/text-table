package winw.ai.util.opencv;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 亮度、灰阶、反相
 */
public class ImageBrightness extends JFrame {
	private static final long serialVersionUID = 1L;

	private JLabel imageView;

    private double alpha = 8; // 主调，"粗略"调整
    private double beta = 50; // 微调，"细微"调整
    private double gamma = 1; // 微调，"细微"调整
    private Mat srcMat;

    private int pattern = 0;
    private boolean isPixel = false; // 部分像素调整
    private boolean isGray = false;  // 灰阶图
    private boolean isInvert = false; // 反相图

    public ImageBrightness(String path) {

        JLabel lblAlpha = new JLabel("Alpha:" + alpha);
        lblAlpha.setBounds(15, 10, 80, 15);
        JLabel lblBeta = new JLabel("Beta:" + beta);
        lblBeta.setBounds(15, 40, 80, 15);
        JLabel lblGamma = new JLabel("Gamma:" + gamma);
        lblGamma.setBounds(15, 70, 80, 15);

        JSlider alphaBar = new JSlider(1, 50);
        alphaBar.setValue((int) alpha);
        alphaBar.setBounds(110, 5, 180, 25);
        alphaBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                alpha = alphaBar.getValue() * 1.0d;
                lblAlpha.setText("Alpha:" + alpha);
                setPic();
            }
        });

        JSlider betaBar = new JSlider(1, 300);
        betaBar.setValue((int) beta);
        betaBar.setBounds(110, 35, 180, 25);
        betaBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                beta = betaBar.getValue() * 1.0d;
                lblBeta.setText("Beta:" + beta);
                setPic();
            }
        });

        JSlider gammaBar = new JSlider(0, 200);
        gammaBar.setValue((int) beta);
        gammaBar.setBounds(110, 65, 180, 25);
        gammaBar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gamma = gammaBar.getValue() * 1.0d;
                lblGamma.setText("Gamma:" + gamma);
                setPic();
            }
        });

        JRadioButton alphaBtn = new JRadioButton("仅使用alpha");
        alphaBtn.setBounds(320, 5, 130, 25);
        alphaBtn.setSelected(true);
        alphaBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                pattern = 0;
            }
        });
        JRadioButton alphaBetaBtn = new JRadioButton("alpha + beta");
        alphaBetaBtn.setBounds(320, 35, 130, 25);
        alphaBetaBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                pattern = 1;
            }
        });
        JRadioButton alphaGammaBtn = new JRadioButton("alpha + gamma");
        alphaGammaBtn.setBounds(320, 65, 130, 25);
        alphaGammaBtn.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                pattern = 2;
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(alphaBtn);
        group.add(alphaBetaBtn);
        group.add(alphaGammaBtn);

        JCheckBox pixelBtn = new JCheckBox("部分像素");
        pixelBtn.setBounds(460, 35, 120, 25);
        pixelBtn.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                isPixel = !isPixel;
            }
        });
        JCheckBox grayBtn = new JCheckBox("灰阶");
        grayBtn.setBounds(15, 95, 60, 25);
        grayBtn.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                isGray = !isGray;
                setPic();
            }
        });
        JCheckBox invertBtn = new JCheckBox("反相");
        invertBtn.setBounds(75, 95, 60, 25);
        invertBtn.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                isInvert = !isInvert;
                setPic();
            }
        });

        srcMat = Imgcodecs.imread(path);
        int width = srcMat.width();
        int height = srcMat.height();

        imageView = new JLabel("");
        imageView.setBounds(0, 140, width, height);

        this.setTitle("图片亮度调整");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(width + 100, height + 180);
        this.getContentPane().setLayout(null);
        this.getContentPane().add(lblAlpha);
        this.getContentPane().add(lblBeta);
        this.getContentPane().add(lblGamma);
        this.getContentPane().add(alphaBtn);
        this.getContentPane().add(alphaBetaBtn);
        this.getContentPane().add(alphaGammaBtn);
        this.getContentPane().add(pixelBtn);
        this.getContentPane().add(grayBtn);
        this.getContentPane().add(invertBtn);
        this.getContentPane().add(alphaBar);
        this.getContentPane().add(betaBar);
        this.getContentPane().add(gammaBar);
        this.getContentPane().add(imageView);

        setPic();
    }

    private void setPic() {
        int rowSize = srcMat.rows();
        int colSize = srcMat.cols();
        int type = srcMat.type();

        if (isInvert) {
            Mat mat = invert(srcMat);
            setIcon(mat);
            return;
        }

        if (isGray) {
            Mat mat = new Mat(rowSize, colSize, type);
            Imgproc.cvtColor(srcMat, mat, Imgproc.COLOR_RGB2GRAY);
            setIcon(mat);
            return;
        }

        if (0 == pattern) { // 仅alpha主调值
            Mat mat = new Mat(rowSize, colSize, type);
            srcMat.convertTo(mat, type, alpha / 10);
            setIcon(mat);
            return;
        }

        if (1 == pattern) { // alpha + beta增益
            Mat mat = new Mat(rowSize, colSize, type);
            if (isPixel) { // 部分像素操作
                mat = convertPixels(srcMat, alpha, -beta);
            } else {
                srcMat.convertTo(mat, type, alpha / 10, -beta); // beta使用正值则累加到alpha提高亮度；反之使用负值则降低亮度
                // 新像素[x,y] = 像素[x,y] * alpha + beta; x,y即像素的位置
            }
            setIcon(mat);
            return;
        }

        if (2 == pattern) { // alpha + 伽马值
            Mat mat = new Mat(rowSize, colSize, type);
            Core.addWeighted(srcMat, alpha, srcMat, 0, gamma, mat);
            setIcon(mat);
            return;
        }
    }

    private void setIcon(Mat mat) {
        BufferedImage image = matToBufferedImage(mat);
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

    /**
     * 部分像素的亮度调整
     *
     * @param srcMat
     * @param alpha
     * @param beta
     * @return
     */
    public static Mat convertPixels(Mat srcMat, double alpha, double beta) {
        int rowSize = srcMat.rows();
        int colSize = srcMat.cols();
        int type = srcMat.type();
        Mat mat = new Mat(rowSize, colSize, type);
        for (int j = 0; j < rowSize; j++) {
            for (int i = 0; i < colSize; i++) {
                double[] temp = srcMat.get(j, i);
                if (j >= 50 && j <= rowSize - 50 && i >= 50 && i <= colSize - 50) { // 排除外围50像素
                    temp[0] = temp[0] * alpha + beta;
                    temp[1] = temp[1] * alpha + beta;
                    temp[2] = temp[2] * alpha + beta;
                }
                mat.put(j, i, temp);
            }
        }

        return mat;
    }

    /**
     * 图片反相
     *
     * @param srcMat
     * @return
     */
    public static Mat invert(Mat srcMat) {
        int rows = srcMat.rows();
        int cols = srcMat.cols();
        int type = srcMat.type();
//        Mat mat = new Mat(rows, cols, type, new Scalar(255, 255, 255)); // 算子。全白(255, 255, 255)
        Mat dst = new Mat(rows, cols, type);
        // Core.bitwise_xor(srcMat, mat, dst); // 布尔运算。
        // Core.subtract(mat, srcMat, dst); // 相减
        Core.bitwise_not(srcMat, dst); // 取反
        return dst;
    }
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
 
    public static void main(String[] args) {
        ImageBrightness imageBrightness = new ImageBrightness("e://bird.png");
        imageBrightness.setVisible(true);
    }
}