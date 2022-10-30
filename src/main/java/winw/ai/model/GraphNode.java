package winw.ai.model;

/**
 * 图上的点，一般是顶点，两条边相交的终点。
 * 
 * <p>
 * 在三维坐标轴上，用X、Y、Z表示。
 * 
 * @author winw
 *
 */

public class GraphNode implements Comparable<GraphNode> {// 或者是 Vertex

	protected int x;
	protected int y;
	protected int z = 0;

	protected int size = 1;// 点的大小。或者宽度

	public GraphNode() {
		super();
	}

	public GraphNode(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public GraphNode(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static GraphNode of(int x, int y) {
		return new GraphNode(x, y);
	}

	
	
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

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int compareTo(GraphNode o) {
		int xCompare = Integer.valueOf(this.x).compareTo(o.x);
		int yCompare = Integer.valueOf(this.y).compareTo(o.y);
		int zCompare = Integer.valueOf(this.z).compareTo(o.z);
		return xCompare == 0 ? (yCompare == 0 ? zCompare : yCompare) : xCompare;
	}

}
