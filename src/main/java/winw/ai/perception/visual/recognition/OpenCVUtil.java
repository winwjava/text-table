package winw.ai.perception.visual.recognition;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
 
 
public class OpenCVUtil {
 
	public static Image matToImage(Mat matrix) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (matrix.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
		byte[] buffer = new byte[bufferSize];
		matrix.get(0, 0, buffer); // 获取所有的像素点
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
		return image;
	}
 
	public static List<String> getFilesFromFolder(String folderPath) {
		List<String> fileList = new ArrayList<String>();
		File f = new File(folderPath);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File singleFile : files) {
				fileList.add(singleFile.getPath());
			}
		}
		return fileList;
	}
 
	public static String randomFileName() {
		StringBuffer fn = new StringBuffer();
		fn.append(System.currentTimeMillis()).append((int) (System.currentTimeMillis() % (10000 - 1) + 1))
				.append(".jpg");
		return fn.toString();
	}
 
	public static List<FileBean> getPicFromFolder(String rootPath) {
		List<FileBean> fList = new ArrayList<FileBean>();
		int fileNum = 0, folderNum = 0;
		File file = new File(rootPath);
		if (file.exists()) {
			LinkedList<File> list = new LinkedList<File>();
			File[] files = file.listFiles();
			for (File file2 : files) {
				if (file2.isDirectory()) {
					// logger.info(">>>>>>文件夹:" + file2.getAbsolutePath());
					list.add(file2);
					folderNum++;
				} else {
					// logger.info(">>>>>>文件:" + file2.getAbsolutePath());
					FileBean f = new FileBean();
					String fileName = file2.getName();
					String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
					File fParent = new File(file2.getParent());
					String parentFolderName = fParent.getName();
					f.setFileFullPath(file2.getAbsolutePath());
					f.setFileType(suffix);
					f.setFolderName(parentFolderName);
					fList.add(f);
					fileNum++;
				}
			}
			File temp_file;
			while (!list.isEmpty()) {
				temp_file = list.removeFirst();
				files = temp_file.listFiles();
				for (File file2 : files) {
					if (file2.isDirectory()) {
						// System.out.println("文件夹:" + file2.getAbsolutePath());
						list.add(file2);
						folderNum++;
					} else {
						// logger.info(">>>>>>文件:" + file2.getAbsolutePath());
						FileBean f = new FileBean();
						String fileName = file2.getName();
						String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
						File fParent = new File(file2.getParent());
						String parentFolderName = fParent.getName();
						f.setFileFullPath(file2.getAbsolutePath());
						f.setFileType(suffix);
						f.setFolderName(parentFolderName);
						fList.add(f);
						fileNum++;
					}
				}
			}
		}
		// logger.info(">>>>>>文件夹共有:" + folderNum + ",文件共有:" + fileNum);
		return fList;
	}
 
	public static BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			// bgr to rgb
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}
 
	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}
}