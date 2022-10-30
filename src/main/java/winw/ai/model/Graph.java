package winw.ai.model;

import java.util.HashSet;
import java.util.List;

/**
 * 图模型，可用于逻辑表达，逻辑推演。
 * 
 * <p>
 * 图论中，图（Graph）的符号往往用G表示，图被定义为一个多元组，核心元素为顶点（vertex）集V以及边（edge）集E，即G=（V，E）。
 * 从数据的角度，顶点可以理解为针对实体、对象的建模，边则是用于描述两个顶点间的关联或交互。给定两个顶点u,v,
 * 用（u,v）表示两点间的边。此外，图的多元组中往往还有标签函数L（指向点边的标签）、属性函数P（指向点边的属性）以及点边类型函数T等等。
 * 
 * <p>
 * 人们每天获取的信息，不限任何主题或领域，大都可以一条或多条陈述句来表达。例如“拜登当选了美国总统”，“Twitter封杀了特朗普”。陈述句的主体大都可以表示为主谓宾，如三元组:<主语：拜登，
 * 谓语：当选，宾语：美国总统>以及<主语：Twitter，谓语：封杀，宾语：特朗普>。而图模型可以很好地建模主谓宾，即以主、宾为两个对象（顶点），以谓语表示对象间的关联或交互（边）。因此图模型能很好地建模主谓宾，进而建模陈述句、信息。而且，这里不对信息做任何主题或者领域的限制，因此图模型能以简洁的形式对复杂知识信息进行良好的建模和融合。
 * 
 * <p>
 * 例如，一个茶杯，形状由边构成，颜色是属性。类似形状的茶杯按形状可以归为一类。
 * 
 * <p>
 * 贝叶斯网络是有向图模型
 * <p>
 * 马尔可夫网络是无向图模型
 * 
 * <p>
 * 构建模型、使用模型是大脑思考的基础。
 * 
 * <p>
 * 大脑中的图模型，包括空间，全部是由点的三维矩阵组成的。例如：坎德尔的大脑导航地图。大脑中有点的细胞、边的细胞等。
 * 
 * @see https://blog.csdn.net/weixin_48167662/article/details/118993379
 * @see org.deeplearning4j.models.sequencevectors.graph.primitives.Graph<V, E>
 * @author winw
 *
 */
public class Graph {// 文字，包括象形文字是图的最广泛的应用方式。有向图

	protected HashSet<GraphNode> nodelist = new HashSet<GraphNode>();// 点，相对位置。

	public HashSet<GraphNode> getNodes() {
		return nodelist;
	}
	
	// protected List<GraphEdge> edgelist;// 边，可以是曲边，可以是向量，有向，边与边可以围成一个面。

	public void addNode(int x, int y) {
		nodelist.add(GraphNode.of(x, y));
	}

	public void addNode(GraphNode node) {
		nodelist.add(node);
	}

	// TODO 划分为子图
	public List<Graph> split() {
		return null;
	}

	public void draw() {// 绘制出来，以指定方向作为正面。

	}

	public void compare() {// 与另外一个图比较，是否相同。
		// 用卷积计算。或其他方式。

	}
}
