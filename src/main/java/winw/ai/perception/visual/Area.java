package winw.ai.perception.visual;

import java.util.Set;

/**
 * 有多个Blob组成的区域。
 * 
 * <h2>Area 性质</h2>
 * <p>
 * 纵横比：宽度与高度的比例。在双目视觉中同一个物体纵横比相差不大。
 * <p>
 * 中心位置：在双目摄像头中，同一个物体的Y轴中心位置相同，X轴会有一定范围的视差。
 */
public class Area {

	private Set<Blob> blobSet;

	/**
	 * 位置，位置坐标
	 */
	private int centerX;
	private int centerY;

	private int width;
	private int height;

	// minX 和 minY 相当于X0 和Y0
	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxX = 0;
	private int maxY = 0;

	/**
	 * 纵横比，双目成像中物体的纵横比（形状）或像素数变化不大，纵轴和横轴会按比例缩小或扩大。
	 */
	private double aspectRatio;

	// 左上，右上、右下、左下

	// 应该用线条

	private int[][] blob;// 重新定义一个二值图，把每个小的Blob合并起来，用于跟另外一个摄像头的二值图比较。

	public Area(Set<Blob> blobSet) {
		super();
		this.blobSet = blobSet;

		for (Blob blob : blobSet) {
			minX = Math.min(minX, blob.getMinX());
			maxX = Math.max(maxX, blob.getMaxX());
			minY = Math.min(minY, blob.getMinY());
			maxY = Math.max(maxY, blob.getMaxY());
		}

		this.centerX = (int) Math.floor((minX + maxX) / 2F);
		this.centerY = (int) Math.floor((minY + maxY) / 2F);

		this.width = maxX - minX + 1;
		this.height = maxY - minY + 1;

		// Blob 像素相差不大，纵横比相差不大；
		this.aspectRatio = Double.valueOf(maxY - minY) / (maxX - minX);

		// 合并blobSet
		this.blob = new int[width][height];

		// 求每个点与中心点的偏移量。
		for (Blob blob : blobSet) {
			for (int x = 0; x < blob.getBlob().length; x++) {
				for (int y = 0; y < blob.getBlob()[0].length; y++) {
					if (blob.getBlob()[x][y] == 1) {
						// minX 和 minY 当作偏移量
						this.blob[blob.getX0() + x - blob.getxOffset() - minX][blob.getY0() + y - blob.getyOffset()
								- minY] = blob.getBlob()[x][y];
					}
				}
			}

		}

	}

	public Set<Blob> getBlobSet() {
		return blobSet;
	}

	public void setBlobSet(Set<Blob> blobSet) {
		this.blobSet = blobSet;
	}

	public double getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int[][] getBlob() {
		return blob;
	}

	public void setBlob(int[][] blob) {
		this.blob = blob;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMinX() {
		return minX;
	}

	public void setMinX(int minX) {
		this.minX = minX;
	}

	public int getMaxX() {
		return maxX;
	}

	public void setMaxX(int maxX) {
		this.maxX = maxX;
	}

	public int getMinY() {
		return minY;
	}

	public void setMinY(int minY) {
		this.minY = minY;
	}

	public int getMaxY() {
		return maxY;
	}

	public void setMaxY(int maxY) {
		this.maxY = maxY;
	}

}
