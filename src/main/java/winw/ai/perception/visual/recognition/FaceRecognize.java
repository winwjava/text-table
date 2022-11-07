package winw.ai.perception.visual.recognition;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
 
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
 
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
import javax.swing.JButton;
 
public class FaceRecognize extends JFrame {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
//	private static Logger logger = Logger.getLogger(FaceRecognize.class);
	private static final String cascadeFileFullPath = "D:\\opencvinstall\\build\\install\\etc\\lbpcascades\\lbpcascade_frontalface.xml";
	private static final String photoPath = "D:\\opencv-demo\\face\\mk\\";
	private static final String modelFolder = "D:\\opencv-demo\\model";
	private static final String modelPath = "D:\\opencv-demo\\model\\face_model.yml";
	private JPanel contentPane;
	protected static VideoPanel videoCamera = new VideoPanel();
	private static final Size faceSize = new Size(165, 200);
	private static VideoCapture capture = new VideoCapture();
	private static boolean trainSwitch = false;
	private static boolean identifySwitch = false;
 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		FaceRecognize frame = new FaceRecognize();
		frame.setVisible(true);
		frame.invokeCamera(frame, videoCamera);
	}
 
	public void invokeCamera(JFrame frame, VideoPanel videoPanel) {
		new Thread() {
			public void run() {
				CascadeClassifier faceCascade = new CascadeClassifier();
				faceCascade.load(cascadeFileFullPath);
				try {
					capture.open(0);
					Scalar color = new Scalar(0, 255, 0);
					MatOfRect faces = new MatOfRect();
					// Mat faceFrames = new Mat();
					if (capture.isOpened()) {
//						logger.info(">>>>>>video camera in working");
						Mat faceMat = new Mat();
						while (true) {
							capture.read(faceMat);
							if (!faceMat.empty()) {
								faceCascade.detectMultiScale(faceMat, faces);
								Rect[] facesArray = faces.toArray();
								if (facesArray.length >= 1) {
									for (int i = 0; i < facesArray.length; i++) {
										Imgproc.rectangle(faceMat, facesArray[i].tl(), facesArray[i].br(), color, 2);
										videoPanel.setImageWithMat(faceMat);
										frame.repaint();
										// videoPanel.repaint();
									}
								}
							} else {
//								logger.info(">>>>>>not found anyinput");
								break;
							}
							Thread.sleep(200);
						}
					}
				} catch (Exception e) {
//					logger.error("invoke camera error: " + e.getMessage(), e);
				}
			}
		}.start();
	}
 
	/**
	 * Create the frame.
	 */
	private static void setLabel(Mat im, String label, Point or, Scalar color) {
		int fontface = Core.NORM_L1;
		double scale = 0.8;
		int thickness = 2;
		int[] baseline = new int[1];
 
		Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
		Imgproc.rectangle(im, new Point(or.x, or.y),
				new Point(or.x + text.width, or.y - text.height - baseline[0] - baseline[0]), color, Core.FILLED);
//	    System.out.println("识别信息-------------->"+label);
		Imgproc.putText(im, label, new Point(or.x, or.y - baseline[0]), fontface, scale, new Scalar(255, 255, 255),
				thickness);
 
	}
 
	public FaceRecognize() {
 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 768);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
 
		JPanel cameraGroup = new JPanel();
		cameraGroup.setBounds(10, 10, 988, 580);
		contentPane.add(cameraGroup);
		cameraGroup.setLayout(null);
 
		JLabel videoDescriptionLabel = new JLabel("Video");
		videoDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		videoDescriptionLabel.setBounds(0, 10, 804, 23);
		cameraGroup.add(videoDescriptionLabel);
 
		videoCamera.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		videoCamera.setBounds(10, 43, 794, 527);
		cameraGroup.add(videoCamera);
 
		// JPanel videoPreview = new JPanel();
		VideoPanel videoPreview = new VideoPanel();
		videoPreview.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		videoPreview.setBounds(807, 359, 171, 211);
		cameraGroup.add(videoPreview);
 
		JLabel lblNewLabel = new JLabel("Preview");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(807, 307, 171, 42);
		cameraGroup.add(lblNewLabel);
 
		JPanel buttonGroup = new JPanel();
		buttonGroup.setBounds(65, 610, 710, 35);
		contentPane.add(buttonGroup);
		buttonGroup.setLayout(new GridLayout(1, 0, 0, 0));
 
		JButton photoButton = new JButton("Take Photo");
		photoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				logger.info(">>>>>>take photo performed");
				StringBuffer photoPathStr = new StringBuffer();
				photoPathStr.append(photoPath);
				try {
					if (capture.isOpened()) {
						Mat myFace = new Mat();
						while (true) {
							capture.read(myFace);
							if (!myFace.empty()) {
								Image previewImg = ImageUtils.scale2(myFace, 165, 200, true);// 等比例缩放
								TakePhotoProcess takePhoto = new TakePhotoProcess(photoPath.toString(), myFace);
								takePhoto.start();// 照片写盘
								videoPreview.SetImageWithImg(previewImg);// 在预览界面里显示等比例缩放的照片
								videoPreview.repaint();// 让预览界面重新渲染
								break;
							}
						}
					}
				} catch (Exception ex) {
//					logger.error(">>>>>>take photo error: " + ex.getMessage(), ex);
				}
			}
		});
		buttonGroup.add(photoButton);
 
		JButton trainButton = new JButton("Train");
		trainButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				logger.info(">>>>>>train button performed");
				List<Mat> images = new ArrayList<Mat>();
				CascadeClassifier faceCascade = new CascadeClassifier();
				faceCascade.load(cascadeFileFullPath);
				FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
				// String trainSamplesFolder = "D:\\opencv-demo\\face";
				try {
					if (trainSwitch) {
						trainSwitch = false;
						List<FileBean> trainSamples = OpenCVUtil.getPicFromFolder(photoPath);
						Map<String, List<Integer>> labelOfPerson = new HashMap<String, List<Integer>>();
//						logger.info(">>>>>>trainSamples has [" + trainSamples.size() + "] files");
						if (trainSamples != null && trainSamples.size() > 0) {
							new Thread() {
								private int index = 0;
 
								public void run() {
									for (FileBean sample : trainSamples) {
										if (sample.getFileType().equalsIgnoreCase("jpg")
												|| sample.getFileType().equalsIgnoreCase("pgm")) {
											// logger.info(">>>>>>train file->" + sample);
											MatOfRect faces = new MatOfRect();
											Mat grayFrame = new Mat();
											Mat src = Imgcodecs.imread(sample.getFileFullPath());
											Imgproc.cvtColor(src, grayFrame, Imgproc.COLOR_BGR2GRAY);
											// Imgproc.equalizeHist(grayFrame, grayFrame);
											// 采集人脸
											faceCascade.detectMultiScale(grayFrame, faces);
											Rect[] facesArray = faces.toArray();
											// logger.info(">>>>>>facesArray.length->" + facesArray.length);
											if (facesArray.length >= 1) {
												for (int i = 0; i < facesArray.length; i++) {
													// labelId = i;
													String labelInfo = sample.getFolderName();
													// faceRecognizer.setLabelInfo(labelId, labelInfo);
													if (labelOfPerson.get(labelInfo) == null) {
														index++;
														List<Integer> ids = new ArrayList<Integer>();
														ids.add(index);
														labelOfPerson.put(labelInfo, ids);
														// ids.add(index);
													} else {
														labelOfPerson.get(labelInfo).add(index);
													}
													// logger.info(">>>>>>label-> " + index + " : " + labelInfo);
													faceRecognizer.setLabelInfo(index, labelInfo);
													Mat faceROI = new Mat(grayFrame, facesArray[i]);
													Mat trainFace = new Mat();
 
													Imgproc.resize(faceROI, trainFace, faceSize);
													// images.add(faceROI);
													images.add(trainFace);
													try {
														Thread.sleep(50);
													} catch (Exception e) {
													}
													// images.add(grayFrame.submat(facesArray[i]));
												}
											}
										}
									}
									int[] labelsOfInt = new int[images.size()];
									int i = 0;
									for (String key : labelOfPerson.keySet()) {
										List<Integer> labelIdList = labelOfPerson.get(key);
										for (Integer labelId : labelIdList) {
//											logger.info(">>>>>>i: " + i + " labelId: " + labelId);
											labelsOfInt[i] = labelId;
											i++;
										}
 
									}
									MatOfInt labels = new MatOfInt(labelsOfInt);
									// 调用训练方法
									faceRecognizer.train(images, labels);
									// 输出持久化模型文件 训练一次后就可以一直调用
									faceRecognizer.save(modelFolder + "\\face_model.yml");
								}
							}.start();
 
						}
					} else {
						trainSwitch = true;
					}
					new Thread() {
						public void run() {
							try {
								if (capture.isOpened()) {
									Mat myFace = new Mat();
									while (trainSwitch) {
										capture.read(myFace);
										if (!myFace.empty()) {
											Image previewImg = ImageUtils.scale2(myFace, 165, 200, true);// 等比例缩放
											TakePhotoProcess takePhoto = new TakePhotoProcess(photoPath.toString(),
													myFace);
											takePhoto.start();// 照片写盘
											Thread.sleep(100);
										}
									}
								}
							} catch (Exception e) {
//								logger.error(">>>>>>train error: " + e.getMessage(), e);
							}
						}
					}.start();
				} catch (Exception ex) {
//					logger.error(">>>>>>take photo error: " + ex.getMessage(), ex);
				}
 
			}
		});
		buttonGroup.add(trainButton);
 
		JButton identifyButton = new JButton("Identify");
		identifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (identifySwitch) {
					identifySwitch = false;
				} else {
					identifySwitch = true;
				}
				CascadeClassifier faceCascade = new CascadeClassifier();
				faceCascade.load(cascadeFileFullPath);
				FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
				faceRecognizer.read(modelPath);
				new Thread() {
					public void run() {
						String personName = "unknown";
						try {
							if (capture.isOpened()) {
								Mat faceMat = new Mat();
								while (identifySwitch) {
									capture.read(faceMat);
									if (!faceMat.empty()) {
 
										MatOfRect faces = new MatOfRect();
										Mat grayFrame = new Mat();
										// 读入要比对的脸
										// Mat faceMat = Imgcodecs.imread(humanFacePath);
										// 灰度处理
										Imgproc.cvtColor(faceMat, grayFrame, Imgproc.COLOR_BGR2GRAY);
										// Imgproc.equalizeHist(grayFrame, grayFrame);
 
										faceCascade.detectMultiScale(grayFrame, faces);
										Rect[] facesArray = faces.toArray();
										Scalar color = new Scalar(0, 0, 255);
//										logger.info(">>>>>>facesArray size->" + facesArray.length);
										for (int i = 0; i < facesArray.length; i++) {
											int[] predictedLabel = new int[1];
											double[] confidence = new double[1];
											// faceRecognizer.predict(grayFrame.submat(facesArray[i]), predictedLabel,
											// confidence);
											Mat faceROI = new Mat(grayFrame, facesArray[i]);
											Mat trainFace = new Mat();
											Imgproc.resize(faceROI, trainFace, faceSize);
											// faceRecognizer.predict(faceROI, predictedLabel, confidence);
											faceRecognizer.predict(trainFace, predictedLabel, confidence);
//											logger.info(">>>>>>personName-" + personName + " : " + confidence[0]);
 
											if (confidence[0] < 50) {
												personName = faceRecognizer.getLabelInfo(predictedLabel[0]);
											} else {
												personName = "unknown";
											}
											setLabel(faceMat, personName, facesArray[i].tl(), color);
											videoCamera.setImageWithMat(faceMat);
											// frame.repaint();
											// Thread.sleep(50);
 
										}
									} else {
										break;
									}
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}.start();
			}
		});
		buttonGroup.add(identifyButton);
	}
}