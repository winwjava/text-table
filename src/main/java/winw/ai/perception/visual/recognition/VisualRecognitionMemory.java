package winw.ai.perception.visual.recognition;

import java.util.HashMap;
import java.util.Map;

import winw.ai.Agent;
import winw.ai.memory.Memory;
import winw.ai.model.Model;

/**
 * 视觉认知记忆。长时程增强。
 * 
 * <Point>
 * 见过一个物体后，根据颜色形状大小等信息模型，增强记忆。多次看到，会增强记忆。
 * 
 * <Point>
 * 物体识别可以用几种方式：边缘形状、颜色区域分割、视频背景消除。
 * <Point>
 * 物体认知，还需要强化学习参与。
 * 
 * @author winw
 *
 */
public class VisualRecognitionMemory extends Agent {

	// 将视觉认知的模型记录下来，下一次认知。

	// 自动将看到的每一个物体建立模型，每一个物体都需要用“杏仁核”建立起情绪反射，和主观价值。

	// 比如社会性动物的面部表情，是一套复杂的表情，可以高效传递信息；

	/**
	 * 概念记忆，一个模型对应一个概念记忆。经典条件反射。
	 */
	protected Map<Model, Memory> modelMapping = new HashMap<Model, Memory>();

	// 第一次见的物体。建立模型。

	/**
	 * @see FindContours
	 * @see FindContoursDemo
	 */
	public void model() {// 建立模型
		// TODO 找到所有最大闭环图。

		// FindContours 中可以把所有点连起来的图。

		// 颜色分割，可以作为一个物体整体。
		// 轮廓分割，作为辅助

		// 所有轮廓中的有环图

		// 图内部的纹理、图中子图。

		// TODO 遮挡怎么处理？根据模型搜索
		// 怎么将物体从背景中搜索出来：模型匹配、聚焦处理

	}
}
