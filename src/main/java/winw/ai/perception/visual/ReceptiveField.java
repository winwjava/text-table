package winw.ai.perception.visual;

import java.util.SortedMap;

/**
 * 感受野。
 */
public class ReceptiveField {

	private int x;
	private int y;
	
	/**
	 * 最小亮度
	 */
	private int minBrightness;

	/**
	 * 最大亮度
	 */
	private int maxBrightness;

	/**
	 * 对比度是描述图像中明暗区域差异的参数，高对比度意味着图像中亮的部分更亮，暗的部分更暗，细节更加清晰。
	 * 
	 * 对比度 = (L_max - L_min) / (L_max + L_min)，其中L_max是图像中最亮的亮度值，L_min是最暗的亮度值。
	 */
	private double contrast;

	/**
	 * 空间频率。亮暗作正弦调制的栅条周数，单位是周/度。空间频率的物理内涵是单位长度所含的波数，也可以认为是单位视角内明暗条纹重复出现的周期数。
	 */
	private double frequency;

	// 直方图、热力图

	/**
	 * 朝向
	 */
	private int orientation;

	/**
	 * 颜色
	 */
	private int color;

	/**
	 * 像素数
	 */
	private int pixel;
	
	/**
	 * 颜色的统计比例（灰度直方图）。高空间频率中，感受野中各个颜色占的比例。例如树木、草丛、建筑物表面、地面。
	 */
	private SortedMap<Integer, Integer> colorStats;

	/**
	 * 运动方向（与颜色、朝向不同，在视觉通路中走背侧通路，最终汇集到MT区：运动、导航，空间知觉和运动知觉）
	 */
	private int direction;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getContrast() {
		return contrast;
	}

	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	public int getMinBrightness() {
		return minBrightness;
	}

	public void setMinBrightness(int minBrightness) {
		this.minBrightness = minBrightness;
	}

	public int getMaxBrightness() {
		return maxBrightness;
	}

	public void setMaxBrightness(int maxBrightness) {
		this.maxBrightness = maxBrightness;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setContrast(double contrast) {
		this.contrast = contrast;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getPixel() {
		return pixel;
	}

	public void setPixel(int pixel) {
		this.pixel = pixel;
	}

	public SortedMap<Integer, Integer> getColorStats() {
		return colorStats;
	}

	public void setColorStats(SortedMap<Integer, Integer> colorStats) {
		this.colorStats = colorStats;
	}

}
