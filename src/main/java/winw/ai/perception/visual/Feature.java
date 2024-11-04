package winw.ai.perception.visual;

/**
 * <p>
 * 借助模型在大量自然图片中搜索皮层各处的偏好图片，作者绘制出了V4脑区的偏好地图。基于偏好地图的聚类分析将V4划分成了多个偏好不同类型图片的功能区。这些功能区有的偏好特定颜色或纹理的表面、有的偏好特定朝向的边界、还有的偏好特定类型的物体。
 * <p>
 * 那些偏好特定类型物体的V4功能区，实际表征的是物体的局部元件，如面孔中的鼻子嘴巴、圆形物体的圆边。特征贡献分析还发现V4功能区的偏好特征在感受野内呈现出不同的分布模式：有些功能区偏好局域分布的形状相关特征，而另一些则偏好弥散分布的表面相关特征。
 * <p>
 * 这种以特征分散程度为标志的分区结构或许反映了某种神经计算的组织原则。
 * 
 * 参考：https://mgv.pku.edu.cn/kxyj/kxjz/382532.htm
 */
public class Feature {

	private int orientationPreference;// 偏好特定朝向的边界

	private int colorPreference;// 偏好特定颜色

	private int colorreference;// 偏好特定纹理的表面

	private int frequencySelectivity;// 空间频率选择性

	private int heatmap;// 热力图，特征

	private int localizedFeature;// 偏好局域分布的形状相关特征，例如鼻子、嘴巴等；

	private int distributedFeature;// 偏好弥散分布的表面相关特征，例如鞋底、沙滩；

	private int object;// 偏好特定类型的物体
	
	/**
	 * 主要分类：亮点、
	 * 
	 * 
	 * 颜色块---> 形状
	 * 
	 * 纹理图---> 形状
	 * 
	 * 条纹图---> 
	 * 
	 * 
	 * 
	 */
	public void objectClassification() {
		
	}
}
