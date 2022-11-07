package winw.ai.perception.visual.recognition;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import org.opencv.core.Mat;
 
public class VideoPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Image image;
 
	public void setImageWithMat(Mat mat) {
		image = OpenCVUtil.matToBufferedImage(mat);
		this.repaint();
	}
 
	public void SetImageWithImg(Image img) {
		image = img;
	}
 
	public Mat getMatFromImage() {
		Mat faceMat = new Mat();
		BufferedImage bi = ImageUtils.toBufferedImage(image);
		faceMat = OpenCVUtil.bufferedImageToMat(bi);
		return faceMat;
	}
 
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (image != null)
			g.drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), this);
	}
 
	public static VideoPanel show(String title, int width, int height, int open) {
		JFrame frame = new JFrame(title);
		if (open == 0) {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
 
		frame.setSize(width, height);
		frame.setBounds(0, 0, width, height);
		VideoPanel videoPanel = new VideoPanel();
		videoPanel.setSize(width, height);
		frame.setContentPane(videoPanel);
		frame.setVisible(true);
		return videoPanel;
	}
}