package winw.ai.model;

import java.util.List;

/**
 * 图上的边，可以是曲边、圆形、或任意形状。可以是有向或无向，宽度、厚度等。
 * 
 * <p>
 * 在三维坐标轴上，用X、Y、Z 的函数表示。
 * 
 * <p>
 * 一个边的起点和终点相同，则这个边围起来的是一个区域。例如：椭圆。
 * <p>
 * 多个边连起来的起点和终点相同，则这个边围起来的是一个区域。例如：矩形。
 * 
 * @author winw
 *
 */
public class GraphEdge {


	protected List<GraphNode> nodelist;// 大脑中细胞一般只能表示点，边是若干的点组成的。
	
	protected double a;// X 的系数
	protected double b;// Y 的系数
	protected double c;// Z 的系数

//	protected double width;// 边的宽度，例如字体的大小，是否应该表述为边加宽度，还是应该表述为一个一个矩形？

	protected GraphNode startingNode;// 边的起点。
	protected GraphNode terminalNode;// 边的终点。

}
