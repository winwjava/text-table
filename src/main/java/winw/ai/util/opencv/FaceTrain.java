package winw.ai.util.opencv;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceTrain {

	public static void main(String[] args) throws IOException {
		train("F:/face/imagedb", "F:/face/model");
	}

	/**
	 * 训练模型的方法，传入人脸图片所在的文件夹路径，和模型输出的路径 训练结束后模型文件会在模型输出路径里边
	 **/
	public static void train(String imageFolder, String saveFolder) throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
		CascadeClassifier faceCascade = new CascadeClassifier();
// opencv的模型
		faceCascade.load("F:/face/install/etc/haarcascades/haarcascade_frontalface_alt.xml");
// 读取文件于数组中
		File[] files = new File(imageFolder).listFiles();
		Map<String, Integer> nameMapId = new HashMap<String, Integer>(10);
// 图片集合
		List<Mat> images = new ArrayList<Mat>(files.length);
// 名称集合
		List<String> names = new ArrayList<String>(files.length);
		List<Integer> ids = new ArrayList<Integer>(files.length);
		for (int index = 0; index < files.length; index++) {
// 解析文件名 获取名称
			File file = files[index];
			String name = file.getName().split("\\.")[1];
			Integer id = nameMapId.get(name);
			if (id == null) {
				id = names.size();
				names.add(name);
				nameMapId.put(name, id);
				faceRecognizer.setLabelInfo(id, name);
			}

			Mat mat = Imgcodecs.imread(file.getCanonicalPath());
			Mat gray = new Mat();
// 图片预处理
			Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
			images.add(gray);
			System.out.println("add total " + images.size());
			ids.add(id);
		}
		int[] idsInt = new int[ids.size()];
		for (int i = 0; i < idsInt.length; i++) {
			idsInt[i] = ids.get(i).intValue();
		}
// 显示标签
		MatOfInt labels = new MatOfInt(idsInt);
// 调用训练方法
		faceRecognizer.train(images, labels);
// 输出持久化模型文件 训练一次后就可以一直调用
		faceRecognizer.save(saveFolder + "/face_model.yml");
	}

}
