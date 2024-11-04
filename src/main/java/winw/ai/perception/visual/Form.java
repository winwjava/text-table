package winw.ai.perception.visual;

import java.util.Set;

/**
 * 形状。
 */
public class Form {

	private Set<ReceptiveField> fieldSet;

	public Form(Set<ReceptiveField> fieldSet) {
		super();
		this.fieldSet = fieldSet;
	}

	/**
	 * 纵横比，双目成像中物体的纵横比（形状）或像素数变化不大，纵轴和横轴会按比例缩小或扩大。
	 */
	private double aspectRatio;

	public Set<ReceptiveField> getFieldSet() {
		return fieldSet;
	}

	public void setFieldSet(Set<ReceptiveField> fieldSet) {
		this.fieldSet = fieldSet;
	}

	public double getAspectRatio() {
		return aspectRatio;
	}

	public void setAspectRatio(double aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

}
