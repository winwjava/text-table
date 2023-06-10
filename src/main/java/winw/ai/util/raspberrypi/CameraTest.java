package winw.ai.util.raspberrypi;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;

public class CameraTest {

    public static void main(String[] args) {
//        Webcam webcam = Webcam.getDefault();
        Webcam webcam = Webcam.getWebcams().get(1);
//        Lenovo EasyCamera 0 : 0.0
//        USB2.0 PC CAMERA 1 : 0.0
        for(Webcam cam :Webcam.getWebcams()) {
        	System.out.println(cam.getName()+" : "+cam.getViewSize().getHeight() + ","+cam.getViewSize().getWidth());
        }
        webcam.setCustomViewSizes(new Dimension(2560,720),new Dimension(1280,480));
        
//        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.setViewSize(new Dimension(1280,480));
        

        WebcamPanel panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(true);
        panel.setDisplayDebugInfo(true);
        panel.setImageSizeDisplayed(true);
        panel.setMirrored(true);
        JFrame window = new JFrame("Test webcam panel");
        JButton b = new JButton(new String("拍照".getBytes(), Charset.defaultCharset()));
        b.setPreferredSize(new Dimension(50,30));
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Webcam webcamc = Webcam.getDefault();
                webcamc.open();
                BufferedImage image = webcamc.getImage();
                // save image to PNG file
                try {
                    ImageIO.write(image, "PNG", new File("e:/img/testbut.png"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(b);
        window.add(panel);
        window.setResizable(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        //webcam.open();
        window.setVisible(true);
//        Thread t = new Thread(() -> {
//            Webcam webcamc = Webcam.getDefault();
//            webcamc.open();
//            try {
//                // get image //保存图片
//                for(int i=0;i<600;i++) {
//                    BufferedImage image = webcamc.getImage();
//                    // save image to PNG file
//                    ImageIO.write(image, "PNG", new File("e:/cap/test"+i+".png"));
//                    Thread.sleep(1000);//1s
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        });
//        t.start();

    }
}
