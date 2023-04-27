package winw.ai.perception.visual;

public class VisualBlobColor {// 根据左边的点和上边的点合并
	private int x0;
	private int y0;

	private int[][] blob;// 所有点都是相对x0 和y0，从（0,0）开始

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
