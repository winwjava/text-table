package winw.ai.perception.visual.opencv;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FFTExample {
	public static void main(String[] args) {
		// 初始化一个样本序列
		double[] sampleData = { 5, 5, 3, 4, 5, 5, 7, 5 };

		// 执行FFT
		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] fft = transformer.transform(sampleData, TransformType.FORWARD);

		// 输出FFT结果
		for (Complex c : fft) {
			System.out.println(c.toString());
		}
	}
}