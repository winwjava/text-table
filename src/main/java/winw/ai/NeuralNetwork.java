package winw.ai;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * 存储感知记忆、认知记忆、运动记忆等等。
 * 
 * @author winw
 *
 */
public class NeuralNetwork {

	public static void main(String[] args) {
		INDArray tens = Nd4j.zeros(3, 5).addi(10);
	}

}
