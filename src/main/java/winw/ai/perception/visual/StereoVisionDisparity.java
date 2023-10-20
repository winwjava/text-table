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

/**
 * 视觉双目视差。
 * 
 * <Point>
 * 在V4脑区，用视差边界作为视觉刺激得到的朝向功能图和明暗边界的朝向功能图一致，提示V4对不同来源的边界信息进行了整合。
 * 与此对应的是，在较低级的脑区V1和V2却没有发现这种视差边界的朝向功能图。这表明V4在从视差信息到立体形状信息的
 * 转换过程中发挥了重要的作用。这是首次在灵长类视觉通路中发现基于视差形状信息（shape-from-disparity）的功能结构，
 * 为进一步研究立体形状感知奠定了基础。
 * 
 * <Point>
 * 由明暗边界 和 视差信息 在V4脑区整合形成立体形状信息。
 * 
 * @author winw
 *
 */
public class StereoVisionDisparity implements ActionListener {

	// TODO 根据双目摄像头的两个照片的EDGE，找到视差，并合成立体形状。

	// 需要先将明暗边界合并为更大的边界（V1 到 V2）

	public static void main(String[] args) throws IOException {
		BufferedImage result = VisualBlob.colorReceptiveField(ImageIO.read(new File("E:/IMG/0612-StereoVision.jpg")));
		FileOutputStream output = new FileOutputStream(new File("E:/IMG/0612-StereoVision-blob.jpg"));
		ImageIO.write(result, "jpg", output);
		output.flush();
		output.close();

		// TODO 找到平行等长的线段。
		// TODO 如果两个blob之间差异不大，但存在边缘，则强化这个边缘两侧blob的对比度。
	}

	public static void main3(String[] args) throws IOException {
		BufferedImage stereo = ImageIO.read(new File("E:/IMG/0612-StereoVision.jpg"));// 1280, 480
		BufferedImage leftImage = stereo.getSubimage(0, 0, 640, 480);
		BufferedImage rightImage = stereo.getSubimage(640, 0, 640, 480);
		BufferedImage result = VisualEdge.show(rightImage);
		FileOutputStream output = new FileOutputStream(new File("E:/IMG/0612-StereoVision-edge-r.jpg"));
		ImageIO.write(result, "jpg", output);
		output.close();

		// TODO 两个区域的亮度有较小差异，需要将边界处对比度增强，马赫带。
		// TODO 将亮暗边界的视野扩大，扩大到整个视野，将小视野的明暗边界连接为更大视野的明暗边界。
		
	}

	static Webcam webcam;

	@Override
	public void actionPerformed(ActionEvent event) {
		webcam.open();
		BufferedImage image = webcam.getImage();
		try {
			FileOutputStream output = new FileOutputStream(new File("E:/IMG/0612-StereoVision.jpg"));
			ImageIO.write(image, "jpg", output);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main2(String[] args) {
		for (Webcam cam : Webcam.getWebcams()) {
			System.out.println(
					cam.getName() + " : " + cam.getViewSize().getHeight() + "," + cam.getViewSize().getWidth());
		}
		webcam = Webcam.getWebcams().get(1);
		webcam.setCustomViewSizes(new Dimension(2560, 720), new Dimension(1280, 480));
		webcam.setViewSize(new Dimension(1280, 480));

		JButton b = new JButton(new String("拍照".getBytes(), Charset.defaultCharset()));
		b.setPreferredSize(new Dimension(50, 30));
		b.addActionListener(new StereoVisionDisparity());
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
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// TODO 多元线性拟合；推测出三维平面或曲面；
}
