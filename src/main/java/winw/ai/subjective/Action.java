package winw.ai.subjective;

/**
 * 
 * @author winw
 *
 */
public class Action {

	private String name;

	/**
	 * 执行Action
	 */
	public void action() {
		System.out.println("执行 Action：" + name);

		// Action的内容应该是可以训练的。
		// 使用脚本语言动态编写。
	}

	public Action(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
