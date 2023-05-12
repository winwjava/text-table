package winw.ai.perception.visual;

public class VisualBlobColor {// 根据左边的点和上边的点合并
	private int x0;
	private int y0;

	private int xOffset;
	private int yOffset;
	private int[][] blob;// 中心点是（x0,y0），相对位置在（radius，radius）

	private int color;

	// 相邻的 Blob

	public VisualBlobColor() {
	}

	public VisualBlobColor(int x0, int y0, int[][] blob, int color) {
		super();
		this.x0 = x0;
		this.y0 = y0;
		this.blob = blob;
		this.color = color;
	}

	public VisualBlobColor(int x0, int y0, int xOffset, int yOffset, int color) {
		super();
		this.x0 = x0;
		this.y0 = y0;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.color = color;
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

}
