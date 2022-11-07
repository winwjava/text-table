package winw.ai.perception.visual;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import winw.ai.model.Graph;
import winw.ai.model.GraphNode;

/**
 * 视觉感知。
 * 
 * <p>
 * 模拟视网膜到V1，再经过V2、V3、V4
 * 
 * @author winw
 *
 */
public class VisualPerception {

	// TODO 需要建立一套复杂结构的三维网络，可以匹配视觉中的一切模型。

	/**
	 * 视网膜，实际的功能会只局限于简单的像素采集，经过了光电转换，将光信号转换成电信号。
	 * 
	 * <p>
	 * 光感受器将信息传递给十多个亚型的双极细胞，可以理解为十多条并行的通路，这十多条通路在对视觉信息进行平行传递时由水平细胞和无长突细胞进行加工处理，最终将视觉信息的不同要素（比如明暗变化，颜色，运动速度与方向等）抽提出来传递给不同亚型的神经节细胞。
	 * 
	 * <p>
	 * 通过视网膜的中心环绕感受野，视网膜可以转换视觉场景的信号有：暗（OFF）、亮（ON）、边缘（EDGE）、红、绿、蓝、左移运动和右移运动（Right
	 * motion）
	 * 
	 * <p>
	 * 人视网膜有视杆细胞约12000万个，对弱光刺激敏感；视锥细胞有650万～700万个，对强光和颜色敏感。二种细胞平行排列，视锥细胞主要集中在中央凹；视杆细胞由中央凹边缘向外周渐多。至锯齿缘附近，视细胞消失。
	 * 
	 * @see http://www.ion.ac.cn/kpwz/201907/t20190703_5332551.html
	 * @see https://github.com/pablomc88/Primate_Visual_System
	 */
	public void retina() {

		/*
		 * 'ex1_disk.py'：视网膜对闪光的反应，可以是圆盘形或环形
		 * 
		 * 'ex2_square.py'：视网膜对闪烁方块的反应
		 * 
		 * 'ex3_grating_spatial_freq.py'：视网膜对不同空间频率的正弦波光栅的响应
		 * 
		 * 'ex4_disk_area_response.py'：视网膜对不同直径闪光点的反应
		 * 
		 * 'ex5_receptive_field.py'：估计视网膜感受野
		 */

		// 视网膜对不同空间频率的正弦波光栅的响应

	}

	/**
	 * 视皮层呈现柱状分布：方向柱、方位柱、眼优势柱、空间频率柱以及颜色柱
	 * 
	 * <p>
	 * 颜色、方向、朝向、复杂形状、深度。
	 */
	public void v1() {

	}

	/**
	 * 根据边缘，得到形状。
	 * 
	 * <p>
	 * 使用聚类？
	 * 
	 * <p>
	 * 将形状分类/聚类。
	 * 
	 * <p>
	 * 梯度
	 */
	public void v1Shape() {

	}

	/**
	 * 
	 * 转移注意力，由大脑基底核或额叶控制。
	 * 
	 * <p>
	 * 周围可感知的信息很多，但大脑的计算资源是有限的，所以一般只关注当前的注意力聚焦的部分。
	 * 
	 */
	public void moveAttention() {// 聚焦到视野的指定位置，例如，正前方，偏右45度，高10度。
		// 可实现运动物体的跟踪。或调整姿态以便保持视图稳定。
	}

	public static void main(String[] args) throws IOException {

		// TODO 从相机中读取图片。

		File file = new File("E:\\2016.jpg");
		BufferedImage bufferedImage = ImageIO.read(file);
		Graph graph = RetinaEdge.generateGraph(bufferedImage);
		JFrame frame = new JFrame("winw-game");
		JPanel container = new JPanel();

		BufferedImage graphImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),BufferedImage.TYPE_INT_RGB);

		for(GraphNode node: graph.getNodes()) {
			graphImage.setRGB(node.getX(), node.getY(), 65281);
		}

		ImagePanel imagePanel = new ImagePanel(graphImage);
		
		imagePanel.setPreferredSize(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
		container.add(imagePanel);
		
		frame.setVisible(true);
		frame.add(new JScrollPane(container));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		FileOutputStream ops = new FileOutputStream(new File("E:\\2016_edge4_" + file.getName()));
//		ImageIO.write(bufferedImage, "jpg", ops);
//		ops.flush();
//		ops.close();
		
		// TODO 寻找所有的闭合区域。长时程增强，自动聚类。
		// TODO 物体分割提取，每个物体构成的边界会组成一个闭合区域。但有时候有遮挡或干扰，应该怎么处理。
		
	}
}
