package winw.ai.perception.visual;

/**
 * 视觉线条。
 * 
 * @author winw
 *
 */
public class Line {

	int orientation;// 方向
	double slope;

	int x1;
	int y1;
	int x2;
	int y2;

	public Line(int orientation, int x1, int y1, int x2, int y2) {
		super();
		this.orientation = orientation;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
        this.slope = Math.tan(Math.toRadians(orientation));

//        System.out.println("tan("+orientation+") slope: " + this.slope);
	}

	public int getOrientation() {
		return orientation;
	}

	public double getSlope() {
		return slope;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

}
