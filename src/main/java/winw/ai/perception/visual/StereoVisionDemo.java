package winw.ai.perception.visual;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

import winw.ai.perception.visual.opencv.CannyTest;

public class StereoVisionDemo implements ActionListener {

	static Webcam webcam;

	@Override
	public void actionPerformed(ActionEvent event) {
		webcam.open();
		BufferedImage image = webcam.getImage();
		
		try {
			stereoVision(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage stereoVision(BufferedImage image) throws IOException {
		FileOutputStream output = new FileOutputStream(new File("D:/file/05-StereoVision.jpg"));
		ImageIO.write(image, "JPEG", output);
		output.close();
		// 使用 Canny 边缘分割
		BufferedImage bufImage = ImageIO.read(new File("D:/file/05-StereoVision.jpg"));
		BufferedImage cannyImg = CannyTest.getCannyImg(bufImage, 0.08, 0.4, 2);
//		output = new FileOutputStream(new File("D:/file/05-StereoVision.jpg"));
//		ImageIO.write(cannyImg, "JPEG", output);
//		output.close();
		// 使用threshold 阈值分割
		return cannyImg;
	}

	public static void main1(String[] args) {
		for (Webcam cam : Webcam.getWebcams()) {
			System.out.println(
					cam.getName() + " : " + cam.getViewSize().getHeight() + "," + cam.getViewSize().getWidth());
		}
		webcam = Webcam.getWebcams().get(0);
		webcam.setCustomViewSizes(new Dimension(2560, 720), new Dimension(1280, 480));
		webcam.setViewSize(new Dimension(1280, 480));

		JButton b = new JButton(new String("photograph".getBytes(), Charset.defaultCharset()));
		b.setPreferredSize(new Dimension(100, 30));
		b.addActionListener(new StereoVisionDemo());
		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setFPSDisplayed(true);
		panel.setDisplayDebugInfo(true);
		panel.setImageSizeDisplayed(true);
		panel.setMirrored(true);
		panel.add(b);
		JFrame window = new JFrame("Stereo Vision");
		window.pack();
		window.add(panel);
		window.setVisible(true);
		window.setResizable(true);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	

	public static void main(String[] args) throws IOException {
		BufferedImage image = ImageIO.read(new File("D:/file/05-StereoVision.jpg"));
		BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		VisualBlob.colorReceptiveField(resultImage, image);
		FileOutputStream output = new FileOutputStream(new File("D:/file/0511-StereoVision-blob.jpg"));
		ImageIO.write(resultImage, "jpg", output);
		output.flush();
		output.close();

		// TODO 找到平行等长的线段。
		// TODO 如果两个blob之间差异不大，但存在边缘，则强化这个边缘两侧blob的对比度。
	}
}
