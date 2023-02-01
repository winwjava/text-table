package winw.ai.perception.visual;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Fionán
 */
public class EdgeDection {

	static enum DIRECTION {
		RIGHT, DOWN, LEFT, UP, NOMOVE
	}

	BufferedImage bi;
	int borderColor = Color.black.getRGB();
	DIRECTION facing;
	Point p = new Point();
	ArrayList<Point> borders;
	boolean upFlag = false;
	int x = p.x;
	int y = p.y;

	public static void main(String[] args) throws IOException {
//		BufferedImage testImage = null;
//		testImage = ImageIO.read(EdgeDection.getClass().getResourceAsStream("testImage2.png"));

		File file = new File("E:\\2016.jpg");
		BufferedImage testImage = ImageIO.read(file);
		int x = 150;
		int y = 60;
		// forcing instance for loading Images only.
		EdgeDection test = new EdgeDection();
		JFrame show = new JFrame();
		show.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel picLabel = new JLabel(new ImageIcon(testImage));
		show.add(picLabel);
		show.pack();
		show.setVisible(true);
		EdgeDection dector = new EdgeDection(testImage, new Point(x, y));
		dector.start();
		dector.highLightEdge();
		show.repaint();
	}

	boolean canMove(DIRECTION d, Point p) {
		switch (d) {
		case RIGHT:
			return bi.getRGB(p.x + 1, p.y) != borderColor;
		case DOWN:
			return bi.getRGB(p.x, p.y + 1) != borderColor;
		case LEFT:
			return bi.getRGB(p.x - 1, p.y) != borderColor;
		// Deafult is up
		case UP:
			return bi.getRGB(p.x, p.y - 1) != borderColor;
		default:
			return false;
		}
	}

	public EdgeDection(BufferedImage bi, Point p) {
		this.facing = DIRECTION.RIGHT;
		this.bi = bi;
		this.p = p;
		this.borders = new ArrayList<>();
	}

	public EdgeDection() {
	}

	DIRECTION getDirection() {
		return null;
	}

	void addBorder(Point p) {
		if (borders.isEmpty()) {
			x = p.x;
			y = p.y;
		}
		borders.add(p);
	}

	void start() {
		do {
			System.out.println("Checking " + p.x + " " + p.y + facing);
			if (canMove(facing, p)) {
				if (upFlag) {
					facing = DIRECTION.UP;
					// p =new Point(p.x+1,p.y);
				}
				p = NextPointByDirection();
				if (!upFlag)
					stepBackDirection();
				if (upFlag)
					upFlag = false;
			} else {
				addBorder(p);
				setNextDirection();
				System.out.println("going " + facing + " border array size = " + borders.size());
				System.out.println("Up Flag status " + upFlag);
			}
		} while (facing != DIRECTION.NOMOVE && (p.x != x || p.y != y));
	}

	private void stepBackDirection() {
		switch (facing) {
		case RIGHT:
			if (upFlag) {
				facing = DIRECTION.UP;
			} else {
				facing = DIRECTION.RIGHT;
			}
			break;
		case DOWN:
			facing = DIRECTION.RIGHT;
			break;
		case LEFT:
			facing = DIRECTION.DOWN;
			break;
		case UP:
			facing = DIRECTION.LEFT;
		}
	}

	private void setNextDirection() {
		switch (facing) {
		case RIGHT:
			facing = DIRECTION.DOWN;
			if (upFlag) {
				facing = DIRECTION.UP;
				upFlag = false;
			}
			return;
		case DOWN:
			facing = DIRECTION.LEFT;
			return;
		case LEFT:
			facing = DIRECTION.UP;
			return;
		case UP:
			upFlag = true;
			facing = DIRECTION.RIGHT;
//                upFlag = true;
			// if (canMove(facing, new Point(p.x + 1, p.y - 1))){
			// p = new Point(p.x + 1, p.y - 1);
			//
			// } ;
			//
			// if (upFlag) {
			// facing = DIRECTION.RIGHT;
			// }
		}
	}

	private Point NextPointByDirection() {
		// if (upFlag) {
		// facing = DIRECTION.UP;
		// upFlag = !upFlag;
		// }
		switch (facing) {
		case RIGHT:
			return new Point(p.x + 1, p.y);
		case DOWN:
			return new Point(p.x, p.y + 1);
		case LEFT:
			return new Point(p.x - 1, p.y);
		default:
			return new Point(p.x, p.y - 1);
		}
	}

	private void print() {
		for (Point p : borders) {
			System.out.print(p.x + " " + p.y + " ");
		}
	}

	void highLightEdge() {
		for (Point p : borders) {
			bi.setRGB(p.x, p.y, Color.RED.getRGB());
		}
	}
}
