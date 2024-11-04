package winw.ai.perception.visual;

/**
 * TODO 带阴影或光线不均衡，或者亮度差异，直方图均衡化处理
 * 
 * @author winw
 *
 */
public class Blob {// 根据左边的点和上边的点合并
	private int x0;
	private int y0;

	private int xOffset;
	private int yOffset;
	private int[][] blob;// 中心点是（x0,y0），相对位置在（radius，radius）

	private int minX = Integer.MAX_VALUE;
	private int maxX = 0;
	private int minY = Integer.MAX_VALUE;
	private int maxY = 0;

	private int color;

	// 相邻的 Blob
//	private int minBrightness;
//	private int maxBrightness;

	private int colorRange;

//	public Blob() {
//	}

//	public Blob(int x0, int y0, int[][] blob, int color) {
//		super();
//		this.x0 = x0;
//		this.y0 = y0;
//		this.blob = blob;
//		this.color = color;
//	}

	public Blob(int x0, int y0, int xOffset, int yOffset, int color, int[][] blob) {
		super();
		this.x0 = x0;
		this.y0 = y0;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.color = color;
		this.blob = blob;
//		int minX = Integer.MAX_VALUE;
//		int maxX = 0;
//		int minY = Integer.MAX_VALUE;
//		int maxY = 0;
//		System.out.println(x0 + "," + y0 + "," + xOffset + "," + yOffset);
		for (int x = 0; x < blob.length; x++) {
			for (int y = 0; y < blob[0].length; y++) {
//				System.out.println(blob[x][y]);
				if (blob[x][y] == 1) {
					minX = Math.min(minX, x + x0 - xOffset);
					maxX = Math.max(maxX, x + x0 - xOffset);
					minY = Math.min(minY, y + y0 - yOffset);
					maxY = Math.max(maxY, y + y0 - yOffset);
				}
			}
		}
//		System.out.println(minX + "," + maxX + "," + minY + "," + maxY);
	}

	public int getX0() {
		return x0;
	}

	public void setX0(int x0) {
		this.x0 = x0;
	}

	public int getY0() {
		return y0;
	}

	public void setY0(int y0) {
		this.y0 = y0;
	}

	public int getxOffset() {
		return xOffset;
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public void setyOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	public int[][] getBlob() {
		return blob;
	}

	public void setBlob(int[][] blob) {
		this.blob = blob;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

//	public int getMinBrightness() {
//		return minBrightness;
//	}
//
//	public void setMinBrightness(int minBrightness) {
//		this.minBrightness = minBrightness;
//	}
//
//	public int getMaxBrightness() {
//		return maxBrightness;
//	}
//
//	public void setMaxBrightness(int maxBrightness) {
//		this.maxBrightness = maxBrightness;
//	}

	public int getColorRange() {
		return colorRange;
	}

	public void setColorRange(int colorRange) {
		this.colorRange = colorRange;
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
