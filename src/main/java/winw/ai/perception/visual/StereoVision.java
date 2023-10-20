package winw.ai.perception.visual;

/**
 * 立体视觉，是视觉认知中识别物体的必要功能，高度真实的映射了真实世界。
 * 
 * <Point>
 * 三维立体视觉感知，比二维平面多出的一维是深度（depth），即感知到的距离远近。
 * <Point>
 * 单目线索，可以通过单目视野物体的关联信息形成深度感知。
 * <li>透视 Perspective：线性透视（Linear perspective）、曲线透视（Curvilinear
 * perspective，又称为鱼眼透视）、空气透视（Aerial perspective）
 * <li>相对大小 Relative size
 * <li>相对高度 Relative height
 * <li>遮挡 Occlusion
 * <li>景深 Depth of field：前景清晰、背景较模糊的景深效果能很好地传达距离信息。
 * <li>光照和投影 Light &
 * shading：高光和阴影（shading）是感知物体的形状和位置的重要线索，离光源最近的物体表面往往最亮，远离光源或在暗面的物体亮度降低。
 * <li>纹理渐变 Texture
 * gradient：许多物体表面有一些细微结构，给人以不同材质和触感，我们称之为纹理。纹理随着距离远近产生密度变化的视觉效果：附近物体的精细细节清晰可见，而远处物体的纹理元素变小直到看不清。
 * <li>运动视差 Motion
 * parallax：当头部运动时感知到的差异，不同距离的物体以不同的速度移动，较近的物体似乎比远处的物体移动得更快。运动视差属于单眼线索。在坐火车时这种感觉非常明显：眼前的物体瞬间飞逝，而远处的景物看起来似乎静止不动。
 * <li>调节
 * Accommodation：为了使感知的图像清晰地出现在视网膜上，眼睛的睫状肌根据物体的距离变化而随时调整晶状体的形状（屈光率），从而改变焦距以保持清晰的焦点，这个过程被称为调节。
 * <Point>
 * 双目线索，通过双目近距离的视野成像差异计算深度信息。
 * <li>会聚
 * Convergence：会聚是人在观察物体时两眼之间的夹角，常常又称为辐辏。当视线聚焦在同一个物体上，两个眼球需要相对向内旋转一定角度，以便双目视线相交。
 * <li>视差 Disparity：当双眼注视同一个物体时，左右眼因位置不同会产生视角的差异，左右视网膜成像也会略有不同。
 * 
 * <Point>
 * 
 * 深度线索汇总表 (Doerner 等, 2022)​
 * <table>
 * <tr>
 * <td>深度线索</td>
 * <td>英文术语</td>
 * <td>类型</td>
 * <td>主要影响距离</td>
 * <td>是否反映绝对距离</td>
 * </tr>
 * <tr>
 * <td>线性透视</td>
 * <td>Linear perspective</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>空气透视</td>
 * <td>Aerial perspective</td>
 * <td>单目</td>
 * <td>>30</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>相对大小</td>
 * <td>Relative size</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>相对高度</td>
 * <td>Relative height</td>
 * <td>单目</td>
 * <td>>30m</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>遮挡</td>
 * <td>Occlusion</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>景深</td>
 * <td>Depth of field</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>光照和投影</td>
 * <td>Light & shading</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>纹理渐变</td>
 * <td>Texture gradient</td>
 * <td>单目</td>
 * <td>全部可视范围</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>运动视差</td>
 * <td>Motion parallax</td>
 * <td>单目</td>
 * <td>>20m</td>
 * <td>相对</td>
 * </tr>
 * <tr>
 * <td>调节</td>
 * <td>Accommodation</td>
 * <td>单目</td>
 * <td><2m</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>会聚</td>
 * <td>Convergence</td>
 * <td>双目</td>
 * <td><2m</td>
 * <td>绝对</td>
 * </tr>
 * <tr>
 * <td>视差</td>
 * <td>Disparity</td>
 * <td>双目</td>
 * <td><10m</td>
 * <td>相对</td>
 * </table>
 * 
 * <Point>
 * 参考：
 * <li>Stefano. Stereo Vision:Algorithms and Applications
 * <li>https://cloud.tencent.com/developer/article/1966973
 * <li>https://zhuanlan.zhihu.com/p/593177344
 * <li>Goldstein, E. B., & Cacciamani, L. (2022). Sensation and perception (11th
 * edition). Cengage.
 * <li>Doerner, R., & Steinicke, F. (2022). Perceptual Aspects of VR. In R.
 * Doerner, W. Broll, P. Grimm, & B. Jung (Eds.), Virtual and Augmented Reality
 * (VR/AR): Foundations and Methods of Extended Realities (XR) (pp. 39–70).
 * Springer International Publishing.
 * https://doi.org/10.1007/978-3-030-79062-2_2
 * <li>Doerner, R., Geiger, C., Oppermann, L., Paelke, V., & Beckhaus, S.
 * (2022). Interaction in Virtual Worlds. In R. Doerner, W. Broll, P. Grimm, &
 * B. Jung (Eds.), Virtual and Augmented Reality (VR/AR): Foundations and
 * Methods of Extended Realities (XR) (pp. 201–244). Springer International
 * Publishing. https://doi.org/10.1007/978-3-0
 * 
 * @author winw
 *
 */
public class StereoVision {
	// 人眼是通过眼部肌肉调焦，计算深度信息？

	// 相机标定：获得相机的焦距、光心坐标、镜头的畸变系数、内参数矩阵、外参数矩阵
	// 用OpenCV来进行相机标定或Matlab中的标定工具箱进行标定

	// TODO 双眼视差，比较方便在10米内视野景物的三维重建。通过常见物体的模型训练学习。

	// 左右两幅图像上的匹配点Y值一定相等，X之间的差别就是视差D

	// 用方形或圆形视野，并根据边缘的分割块，去找到匹配点？

	// 在指定像素坐标周围可以找到和该像素同样视差的像素

	// Disparity-Defined Edges

	// 在V4脑区，用视差边界作为视觉刺激得到的朝向功能图和明暗边界的朝向功能图一致，提示V4对不同来源的边界信息进行了整合。
	// 与此对应的是，在较低级的脑区V1和V2却没有发现这种视差边界的朝向功能图。这表明V4在从视差信息到立体形状信息的
	// 转换过程中发挥了重要的作用。这是首次在灵长类视觉通路中发现基于视差形状信息（shape-from-disparity）的功能结构，
	// 为进一步研究立体形状感知奠定了基础。

	// 我们发现猕猴视觉皮层的第四区（V4）能检测到这种纯粹由视差信息构成的立体边界，而且检测同样边界朝向的神经元聚集在一起，
	// 形成一个朝向功能图。这个视差边界的朝向功能图和明暗边界的朝向功能图一致，提示V4对不同来源的边界信息进行了整合。
	// 与此对应的是，在较低级的脑区V2和V1却没有发现这种立体边界的朝向功能图。

	// 由明暗边界 和 视差信息 在V4脑区整合形成立体形状信息。

	// TODO 多元线性拟合；推测出三维平面或曲面；
}
