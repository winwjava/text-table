package winw.ai.perception.visual.opencv.video;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class TrackFeatures {
    private Mat src = new Mat();
    private Mat srcGray = new Mat();
    private JFrame frame;
    private JLabel imgLabel;
    private static final int MAX_THRESHOLD = 100;
    private int maxCorners = 23;
    private Random rng = new Random(12345);

    public static void main(String[] args) {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TrackFeatures(args);
            }
        });
    }
    public TrackFeatures(String[] args) {
        /// Load source image and convert it to gray
        String filename = args.length > 0 ? args[0] : "D:/file/05.jpg";
        src = Imgcodecs.imread(filename);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }

        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);

        // Create and set up the window.
        frame = new JFrame("Shi-Tomasi corner detector demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set up the content pane.
        Image img = HighGui.toBufferedImage(src);
        addComponentsToPane(frame.getContentPane(), img);
        // Use the content pane's default BorderLayout. No need for
        // setLayout(new BorderLayout());
        // Display the window.
        frame.pack();
        frame.setVisible(true);
        update();
    }

    private void addComponentsToPane(Container pane, Image img) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));

        sliderPanel.add(new JLabel("Max  corners:"));
        JSlider slider = new JSlider(0, MAX_THRESHOLD, maxCorners);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                maxCorners = source.getValue();
                update();
            }
        });
        sliderPanel.add(slider);
        pane.add(sliderPanel, BorderLayout.PAGE_START);

        imgLabel = new JLabel(new ImageIcon(img));
        pane.add(imgLabel, BorderLayout.CENTER);
    }

    private void update() {
        /// Parameters for Shi-Tomasi algorithm
        maxCorners = Math.max(maxCorners, 1);
        MatOfPoint corners = new MatOfPoint();
        double qualityLevel = 0.01;
        double minDistance = 10;
        int blockSize = 3, gradientSize = 3;
        boolean useHarrisDetector = false;
        double k = 0.04;

        /// Copy the source image
        Mat copy = src.clone();

        /// Apply corner detection
        Imgproc.goodFeaturesToTrack(srcGray, corners, maxCorners, qualityLevel, minDistance, new Mat(),
                blockSize, gradientSize, useHarrisDetector, k);

        /// Draw corners detected
        System.out.println("** Number of corners detected: " + corners.rows());
        int[] cornersData = new int[(int) (corners.total() * corners.channels())];
        corners.get(0, 0, cornersData);
        int radius = 4;
        for (int i = 0; i < corners.rows(); i++) {
            Imgproc.circle(copy, new Point(cornersData[i * 2], cornersData[i * 2 + 1]), radius,
                    new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256)), Imgproc.FILLED);
        }

        imgLabel.setIcon(new ImageIcon(HighGui.toBufferedImage(copy)));
        frame.repaint();
    }
}

