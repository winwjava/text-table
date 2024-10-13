package winw.ai.perception.visual;

/**
 * IT皮层的物体空间组织
 * 
 * 首选轴（preferred axes），单个下颞叶细胞将传入对象投影到该空间的特定轴上。
 * 
 * <p>
 * 记录NML网络的NML2和NML3区块在纵横比、曲率和方向上变化的线段的细胞响应。纵横比的响应方差占平均跨细胞的22.8%，曲率占方差的5.6%，方向占方差的3.5%。结果表明：NML网络对高纵横比的偏好响应最强
 * 
 * <p>
 * PC1一定程度上刻画了spiky/smooth（尖锐/光滑）的特征，而PC2一定程度上刻画了animate（curve，有生命的，曲线）/inanimate（square，无生命的，正方形）的特征。
 * PC1和PC2 （两个维度，）两个维度构成一个特征平面，那么如下图所示，我们可以认为第一象限对应Network
 * X脑区，第二象限对应（已知的）Body脑区，第三象限对应（已知的）Face脑区。
 * 
 * <h3>形状比较的关键指标</h3>
 * <p>
 * 纵横比（长宽比） Aspect ratio
 * <p>
 * 曲线：曲线的曲率
 * 
 */
public class VisualObject {

}
