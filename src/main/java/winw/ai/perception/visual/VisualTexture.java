package winw.ai.perception.visual;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * 
 * 
 * 研究发现，颜色和视差信息在V2区的表征呈条纹状分布，而自然纹理表征在V2区未形成可识别的功能柱。皮层亚层级结果显示，相比于颜色和视差，自然纹理在V2区更依赖反馈机制。信息连接性分析进一步提示，V2区在自然纹理加工中主要接收下游V4区的反馈信号。
 * 
 * <p>
 * 针对周边视觉的拥挤效应和涉及纹理的统计信息加工，研究发现学习可改善周边知觉瓶颈，并可增强对自然纹理中统计信息的敏感性。
 * 
 * <p>
 * 自然纹理蕴含的高阶统计信息涵盖了不同朝向、空间尺度和局部位置的相关性。
 * 
 * <h2>纹理特征</h2>
 * 
 * <p>
 * 纹理像形状一样，不同的纹理是有不同的特征的。比如不同的树，不同的房子，不同的大理石。
 * 
 * 
 * <p>
 * 弥散分布（布）/ 局域分布（鼻子）
 * <p>
 * 灰度直方图是对图像上单个像素具有某个灰度进行统计的结果，而灰度共生矩阵是对图像上保持某距离的两像素分别具有某灰度的状况进行统计得到的。
 * 
 * 
 * 4．
 * 寿天德等人还发现，经典感受野中心以外直径9度范围内的所谓大外周抑制区也具有独立的对光栅刺激的方位选择性反应。这一方位选择性的平均强度与经典感受野中心相近。大外周与中心的方位选择性可能不同，对整个大范围的复合感受野的方位选择性贡献也不同。在复合感受野方位选择性的形成中具有中心决定、大外周决定和中心与大外周共同决定等模式。并且大外周刺激的存在将调制中心的方位选择性，反之亦然。这使得神经节细胞可能完成更复杂的感知功能，如纹理质地的感知等。
 * 
 * 
 * 
 * <p>
 * 参考：https://mcgovern.life.tsinghua.edu.cn/ch/infoshow-2716.html
 */
public class VisualTexture {// 纹理包括：有序纹理和无序纹理，有序的纹理：相间条纹（正玄光栅）、颜色变化连续、，无序纹理：成块状分布、颜色不连续。

	// 总共640 × 480 像素，72度，每度大约6.6-8.8个像素，半径是3-5个像素。但每度像素太少也计算不出结果。
	public static int radius = 5;// 感受野半径，空间频率(感受野大小)，总的视野分成若干度，每一度的大小。

	// 猜想：需要统计信息，在低空间频率（模糊图像）中，几乎识别不出轮廓。但是在高空间频率（清晰图像）中又有各种特征？

	/**
	 * 空间频率（空间细节变化密度）
	 * 
	 * <p>
	 * 空间频率是反映图像中明暗变化快慢的指标，对正弦光栅反应最佳
	 * 
	 * <p>
	 * 高频空间频率：每一度（单眼156度）空间，频率达到
	 * 
	 * <p>
	 * 视觉场景中物体的疏密以空间频率（spatial
	 * frequency）来表示，即每度视场中明暗交替光栅的周期数。空间频率是一个重要的视觉参数，关乎形状、纹理和运动等视觉认知。然而，关于灵长类动物纹外皮层如何编码空间频率，仍有许多问题没有解决。以前的研究表明，视觉皮层内的空间频率偏好总体呈现由中心视野向外周视野逐渐降低的趋势。在功能柱尺度上，V1的低空间频率功能柱与颜色功能柱倾向占据相同的皮层空间，高空间频率功能柱与方位功能柱的变化梯度呈正交分布。但是在纹外皮层（如V2和V4区），尚无证据表明空间频率偏好与其他视觉信息选择性之间存在特定的联系。
	 * 
	 * <p>
	 * LGN（外侧膝状体）是位于丘脑中的一个核团，它是视觉信息从眼睛到大脑皮层的中间站。LGN有六层，每层都接收来自一只眼睛的信号，并按照不同的特征进行分类。其中第1-2层为大细胞层（M层），主要接收来自视杆细胞的信号，负责运动、深度和低空间频率（即粗略图像）信息的处理；第3-6层为小细胞层（P层），主要接收来自视锥细胞的信号，负责形状、颜色和高空间频率（即清晰图像）信息的处理；此外还有一些位于各层之间的粒状细胞层（K层），主要接收来自非M非P型神经节细胞（即双层树突神经节细胞）的信号，负责红绿对立和蓝黄对立等颜色信息的处理。
	 * <p>
	 * 空间频率：空间频率是描述图像空间变化的频率，它通常用来表征图像中细节的密集程度。空间频率是视觉中的一个重要属性，分为高空间频率区和低空间频率区。空间频率：对正弦光栅反应最佳
	 * 在图像处理中，空间频率指的是图像灰度变化的频率，通常用周期性变化的亮暗条纹的数量来表示。空间频率的单位是每度视角（cycles per
	 * degree）或者每毫米（cycles per millimeter）等。
	 * SPF表示细胞偏好的空间频率。有颜色的点表示细胞，颜色对应了方向选择指数（DSI，Direction Selective
	 * Index），DSI越高表示细胞的方向选择性越强。可以看出，方向选择性强的细胞大多分布在低空间频率区。
	 * 
	 * <p>
	 * 大部分V1区偏好高空间频率，而大部分V4区倾向于偏好低空间频率。从V1到V2，再到V4，皮层偏好的空间频率逐渐降低，与之前文献中的发现一致。
	 * 
	 * <p>
	 * (1)与V1类似，V2和V4区对空间频率的编码随离心率减少，逐渐由低空间频率向高空间频率转变。但V4中心视野对应区域具有广泛的空间频率响应，具有处理复杂多变图像信息的能力。
	 * <p>
	 * (2)空间频率与方位信息梯度正交。与已知的V1结果类似，在V2和V4中，方位和空间频率信息梯度倾向于相互正交，使得在有限的皮层空间内，能够表征较完整的信息组合。
	 * <p>
	 * (3)与V1类似，在V4和V2中，颜色功能柱与低空间频率功能柱联系紧密。因此，自V1到V4，视皮层采用了较为保守的结构形式，来表征空间频率、形状（朝向）、颜色等信息。所得结果，进一步推进了我们对于灵长类动物视皮层结构和功能的认识。
	 * 
	 * <p>
	 * 在用空间频率描述视觉系统的特性时，栅条空间频率的大小和栅条本身的对比度都是重要的因素。栅条图形的对比度是（最高亮度－最低亮度）/（最高亮度+最低亮度）。调整某一空间频率栅条的对比度，当观察者能有50%的正确分辨率时，这个对比度就是该空间频率的对比阈限。对比阈限值的倒数即观察者对这个空间频率的对比感受性。实验测定，人眼对比阈限是随空间频率的改变而改变的，即是空间频率的函数，称之为对比感受性函数（简称CSF）因它类似于光学系统的调制传递函数（简称MTF），故也称之为MTF。一般视力正常的观察者对每度视角3周或4周的栅条最敏感，高于或低于这个频率时感受性都降低。如果空间频率超过每度视角60周时,不论对比度怎样加大,都不能看清栅条。在不能看清栅条时的频率称为截止频率，它可作为视觉锐度的指标。
	 * 
	 * <p>
	 * 参考： http://www.ziint.zju.edu.cn/index.php/event/cdetails.html?tid=514
	 * https://www.kepuchina.cn/article/articleinfo?business_type=100&classify=0&ar_id=335047
	 */
	public static void spatialFrequencyAnalysis(BufferedImage resultImage, BufferedImage image) {// 空间频率分析
		// 大部分V1区偏好高空间频率，而大部分V4区倾向于偏好低空间频率。从V1到V2，再到V4，皮层偏好的空间频率逐渐降低，与之前文献中的发现一致。

		// LGN 将 需要注意：低空间频率（即粗略图像）、高空间频率（即清晰图像）

		// TODO 空间频率指的是图像灰度变化的频率，通常用周期性变化的亮暗条纹的数量来表示。空间频率的单位是每度视角（cycles per
		// degree）或者每毫米（cycles per millimeter）等。

		// 先灰度处理
		int[][] grayImage = VisualRetina.brightnessReceptiveField(image, radius);// 灰度处理，边缘增强，返回二值化二维数组，存储亮度0~255

		// 对比度是指图像中最亮和最暗像素之间的差异程度。换句话说，它反映了图像中灰度级的变化范围。高对比度意味着图像中明暗区域的差异很大，而低对比度意味着图像中的灰度级变化较为平缓。对比度可以通过以下公式来计算：

		// 空间频率是180度方向的，像方位一样。频率从高到低。

		List<Integer> list = new ArrayList<Integer>();
		for (int j = 0 + radius + 1; j < grayImage.length - radius - 2; j += radius * 2) {// 每个感受野，只需要计算一次即可。
			for (int k = 0 + radius + 1; k < grayImage[0].length - radius - 2; k += radius * 2) {// 每个感受野，只需要计算一次即可。

				// 计算当前感受野的空间频率。
				double sf = 0;
				int x0 = j, y0 = k;
				int x1, y1, x2, y2;
				// 方位和空间频率属性的梯度线夹角以大角度（60°-90°）为主，根据方位图检测？而在非方位选择性区域内，则不存在这一现象
				for (int i = 0; i < 180; i += 5) {// 在180度范围内搜索，在8个方向搜索即可
					// 外环两个点
					x1 = (int) Math.round(x0 - radius * Math.sin(Math.PI * (i - 90) / 180));
					y1 = (int) Math.round(y0 + radius * Math.cos(Math.PI * (i - 90) / 180));// - radius

					x2 = (x1 > x0) ? x0 - (x1 - x0) : x0 + (x0 - x1);
					y2 = (y1 > y0) ? y0 - (y1 - y0) : y0 + (y0 - y1);
					list.clear();
					if (i > 45 && i < 135) {// 将Y轴与X轴调换，为了获得更多的像素点
						double slope = ((double) (x2 - x1)) / ((double) (y2 - y1));
						int from = Math.min(y1, y2);
						int to = Math.max(y1, y2);

						for (int y = from; y <= to; y++) {
							// 当斜率等于0，平行与X轴
							int x = (int) Math.round((y - y1) * slope + x1);
							// 求这条线上交替光栅的周期数。
							list.add(grayImage[x][y]);
							// TODO 必须先做一下过滤，把空间频率为0的区域过滤掉。只计算剩余部分。
							// FIXME 怎么把空间频率记录在二维数组中？像颜色块？像二维梯度那样。
							// FIXME 像梯度那样，例如当前视野的FS为5，则记录中心像素FS为5，旁边为3，在边缘上肯定跟小。

							// FIXME 估计是没有什么好方法，必须是一个一个数的。
							// 要找到波峰和波谷
						}

						sf = Math.max(sf, spatialFrequency(list, 8));
					} else {
						double slope = ((double) (y2 - y1)) / ((double) (x2 - x1));
						int from = Math.min(x1, x2);
						int to = Math.max(x1, x2);
						for (int x = from; x <= to; x++) {
							// 当斜率等于0，平行与X轴
							int y = (int) Math.round((x - x1) * slope + y1);
							list.add(grayImage[x][y]);
						}
						sf = Math.max(sf, spatialFrequency(list, 8));
					}
//					System.out.println(j + "," + k + ", SF: " + sf);
//					if (sf >= 3F) {// V1偏好1.5到4.25左右，特别是3附近
					if (sf >= 1F) {// V2偏好0.5到3.5，特别是2附近
//					if (sf >= 0.25F && sf <= 0.5F) {// V4偏好0到2.5，特别是0.25附近
						for (int m = j - radius; m < j + radius; m++) {
							for (int n = k - radius; n < k + radius; n++) {
								resultImage.setRGB(m, n, 0xFFFFFF);
							}
						}
					}
				}
				// 求一个最大值即可，还是要求各个方向？

				// 怎么计算当前视野的空间频率。

				// FIXME 在同一个视野里，有一次灰度变换，则认为是空间频率为1，有两次变换则认为是2？

				// 梯度值表征为第x行第y列的某像素和他的右邻或下邻像素的差值，梯度越大说明像素值差异越大，画面内容突变越明显

				// 傅里叶变换
				// 亮暗作正弦调制的栅条周数，单位是周/度。空间频率的物理内涵是单位长度所含的波数，也可以认为是单位视角内明暗条纹重复出现的周期数。
				// 怎么计算？

				// 高空间频率功能柱与方位功能柱的变化梯度呈正交分布。
			}
		}
	}

	public static double spatialFrequency(List<Integer> data, float threshold) {
		double frequency = 0;
		int sin = 0;// 如果是负数，则表示在波谷，如果是正数，则是在波峰，0 表示在中间。
		int min = data.get(0);// 区间极小值
		int max = data.get(0);// 区间极大值
//		System.out.println(data.get(0) + ", f: " + frequency + ", sin: " + sin);
		for (int i = 1; i < data.size(); i++) {
			max = Math.max(data.get(i), max);
			min = Math.min(data.get(i), min);
			if (sin <= 0 && data.get(i) - min >= threshold) {// 出现波峰
				frequency++;
				sin = 1;
				max = data.get(i);// 极大值，重新定义
			} else if (sin >= 0 && max - data.get(i) >= threshold) {// 出现波谷
				frequency++;
				sin = -1;
				min = data.get(i);// 极小值，重新定义
			}
//			System.out.println(data.get(i) + ", f: " + frequency + ", sin: " + sin);
		}

		return frequency / 4f;// 两个波峰和两个波谷，算一个正玄频率
	}

	public static void main1(String[] args) {
		int threshold = 10;
		double sf = spatialFrequency(
				Arrays.asList(30, 10, 30, 30, 20, 0, 40, 10, 10, 20, 30, 10, 30, 30, 20, 0, 40, 10), threshold);
		System.out.println("sf: " + sf);
	}

	/**
	 * 而自然纹理表征在V2区未形成可识别的功能柱。皮层亚层级结果显示，相比于颜色和视差，自然纹理在V2区更依赖反馈机制。信息连接性分析进一步提示，V2区在自然纹理加工中主要接收下游V4区的反馈信号。
	 * 
	 * 参考： https://mcgovern.life.tsinghua.edu.cn/ch/infoshow-2716.html
	 */
	public void texture() {
		// 颜色块、纹理块，都属于空间频率可以通过辨别。
		// TODO 通过低空间频率，划分的区域。然后排除颜色块。
	}

	public static void main(String[] args) throws IOException {// 空间密度，与空间频率
		long t0 = System.currentTimeMillis();
		BufferedImage image = ImageIO.read(new File("D:/file/data/leuvenB.jpg"));
		BufferedImage resultImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

		VisualTexture.spatialFrequencyAnalysis(resultImage, image);
		ImageIO.write(resultImage, "jpg", new File("D:/file/temp/leuvenB-sf.jpg"));

		long t1 = System.currentTimeMillis();
		System.out.println("Visual Blob, cost: " + (t1 - t0) + "ms.");

		// TODO 从表面亮度分块（经过马赫带亮度处理），然后标记颜色。
		// TODO 如果两个blob之间差异不大，但存在边缘，则强化这个边缘两侧blob的对比度。
	}

}
