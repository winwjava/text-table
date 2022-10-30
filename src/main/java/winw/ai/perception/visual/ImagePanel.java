package winw.ai.perception.visual;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private BufferedImage bufferedImage;

	public ImagePanel(BufferedImage bufferedImage) {
		super();
		this.bufferedImage = bufferedImage;
	}



	@Override
	public void paint(Graphics g) {
		g.drawImage(bufferedImage, 0, 0, null);
	}

}
