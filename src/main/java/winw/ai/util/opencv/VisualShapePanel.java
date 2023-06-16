package winw.ai.util.opencv;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.opencv.core.Core;
import org.opencv.core.Point;

import winw.ai.perception.visual.VisualFeature;

public class VisualShapePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	protected VisualFeature shape;

	public VisualFeature getShape() {
		return shape;
	}

	public void setShape(VisualFeature shape) {
		this.shape = shape;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		this.paint((Graphics2D) g);
	}

	public void paint(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.setStroke(new BasicStroke(2));

		List<List<Point>> edgeList = shape.getEdgeList();

		for (List<Point> edge : edgeList) {
//			g.drawPolygon(xPoints, yPoints, nPoints);// 多边形
//			g.drawPolyline(xPoints, yPoints, nPoints);// 多个线段连接起来
			g.drawPolygon(VisualFeature.getXPoints(edge), VisualFeature.getYPoints(edge), edge.size());
		}

	}

	public static void show(String title, VisualFeature shape) {
		JFrame frame = new JFrame(title);
		frame.setVisible(true);
		JPanel container = new JPanel();
//		container.setLayout(new GridLayout(2, 2));
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
//		for (QuotePanel quotePanel : views) {

		VisualShapePanel visualShapePanel = new VisualShapePanel();
		visualShapePanel.setShape(shape);
		visualShapePanel.setPreferredSize(new Dimension(500, 500));
		container.add(visualShapePanel);
//		}
		frame.add(new JScrollPane(container));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
 
	public static void main(String[] args) throws Exception {

		List<Point> pointList = new ArrayList<Point>();
		pointList.add(new Point(100.0, 100.0));
		pointList.add(new Point(200.0, 100.0));
		pointList.add(new Point(200.0, 200.0));
		pointList.add(new Point(100.0, 200.0));
		pointList.add(new Point(100.0, 100.0));
//		VisualShape visualShape = new VisualShape(pointList);
//		visualShape.affine();
		VisualShapePanel.show("ShapePanel", new VisualFeature(pointList));
		
	}

}
