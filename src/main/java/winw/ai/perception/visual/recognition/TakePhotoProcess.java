package winw.ai.perception.visual.recognition;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
 
public class TakePhotoProcess extends Thread {
 
	private String imgPath;
	private Mat faceMat;
	private final static Scalar color = new Scalar(0, 0, 255);
 
	public TakePhotoProcess(String imgPath, Mat faceMat) {
		this.imgPath = imgPath;
		this.faceMat = faceMat;
	}
 
	public void run() {
		try {
			long currentTime = System.currentTimeMillis();
			StringBuffer samplePath = new StringBuffer();
			samplePath.append(imgPath).append(currentTime).append(".jpg");
			Imgcodecs.imwrite(samplePath.toString(), faceMat);
//			logger.info(">>>>>>write image into->" + samplePath.toString());
 
		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
		}
	}
 
}