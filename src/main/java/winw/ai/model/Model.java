package winw.ai.model;

/**
 * 模型，还带有大小多少，形状、外观，气味，等等属性。
 * 
 * <p>ONNX
 * @author winw
 *
 */
public class Model implements Comparable<Model> {

	private String name;

	public Model(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Model o) {
		return this.name.compareTo(o.name);
	}

}
