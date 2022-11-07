package winw.ai.perception.visual.recognition;

import org.opencv.core.Mat;
import javax.swing.*;
import java.awt.*;
 
public class ImageViewer {
	private JLabel imageView;
	private Mat image;
	private String windowName;
 
	private JFrame frame = null;
 
	public ImageViewer() {
		frame = createJFrame(windowName, 800, 600);
	}
 
	public ImageViewer(Mat image) {
		this.image = image;
	}
 
	/**
	 * @param image      要显示的mat
	 * @param windowName 窗口标题
	 */
	public ImageViewer(Mat image, String windowName) {
		frame = createJFrame(windowName, 1024, 768);
		this.image = image;
		this.windowName = windowName;
	}
 
	public void setTitle(String windowName) {
		this.windowName = windowName;
	}
 
	public void setImage(Mat image) {
		this.image = image;
	}
 
	/**
	 * 图片显示
	 */
	public void imshow() {
		setSystemLookAndFeel();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 用户点击窗口关闭
		if (image != null) {
			Image loadedImage = OpenCVUtil.matToImage(image);
			// JFrame frame = createJFrame(windowName, image.width(), image.height());
			imageView.setIcon(new ImageIcon(loadedImage));
			frame.pack();
			// frame.setLocationRelativeTo(null);
			// frame.setVisible(true);
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 用户点击窗口关闭
		}
	}
 
	private void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
 
	private JFrame createJFrame(String windowName, int width, int height) {
		JFrame frame = new JFrame(windowName);
		imageView = new JLabel();
		final JScrollPane imageScrollPane = new JScrollPane(imageView);
		imageScrollPane.setPreferredSize(new Dimension(width, height));
		frame.add(imageScrollPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		return frame;
	}
 
}