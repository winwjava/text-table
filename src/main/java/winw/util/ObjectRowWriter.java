package winw.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

/**
 * 将 Object 以行为单位写入输出流。
 * <p>
 * 支持国际化、格式化。
 * 
 * @author sjyao
 *
 * @param <T>
 *            Object Type
 */
public class ObjectRowWriter<T> implements RowWriter {

	private RowWriter writer;

	/**
	 * 字段和字段的转义方式组成的集合，转义方式支持国际化中 ResourceBundle 的键（用String
	 * 表示）和格式化（继承于Format），转义方式方式为空表示不做转义。
	 */
	private List<Map.Entry<String, Object>> writeFields;

	/**
	 * 用于国际化。
	 */
	private ResourceBundle resourceBundle;

	/**
	 * 用一个RowWriter 和 writeFields 构建一个ObjectRowWriter。
	 * 
	 * <p>
	 * writeFields 是字段和字段的转义方式组成的集合，转义方式支持国际化中 ResourceBundle 的键（用String
	 * 表示）和格式化（继承于Format），转义方式方式为空表示不做转义。
	 * 
	 * @param writer
	 *            RowWriter 实现。
	 * @param writeFields
	 *            字段和字段的转义方式组成的集合。
	 */
	public ObjectRowWriter(RowWriter writer, List<Map.Entry<String, Object>> writeFields,
			ResourceBundle resourceBundle) {
		if (writer == null || writeFields == null || writeFields.size() == 0) {
			throw new IllegalArgumentException();
		}
		this.writer = writer;
		this.writeFields = writeFields;
		this.resourceBundle = resourceBundle;
	}

	/**
	 * 返回字段和字段的转义方式组成的集合，转义方式支持国际化中 ResourceBundle 的键（用String
	 * 表示）和格式化（继承于Format），转义方式方式为空表示不做转义。
	 * 
	 * @return 字段和字段的转义方式组成的集合。
	 */
	public List<Map.Entry<String, Object>> getWriteFields() {
		return writeFields;
	}

	/**
	 * 
	 * 
	 * 重新设置字段和字段的转义方式组成的集合，转义方式支持国际化中 ResourceBundle 的键（用String
	 * 表示）和格式化（继承于Format），转义方式方式为空表示不做转义。
	 * 
	 * @param writeFields
	 *            字段和字段的转义方式组成的集合。
	 */
	public void setWriteFields(List<Map.Entry<String, Object>> writeFields) {
		this.writeFields = writeFields;
	}

	private List<String> tempRow = new ArrayList<String>();
	private List<String[]> tempRows = new ArrayList<String[]>();

	/**
	 * 将 Object 集合附加到输出流。
	 * 
	 * @param objects
	 *            Object 集合。
	 */
	public void appendObjects(List<T> objects) throws IntrospectionException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, IOException {
		tempRows.clear();
		for (T obj : objects) {
			tempRow.clear();
			for (Entry<String, Object> fieldEntry : writeFields) {
				if (obj == null) {
					tempRow.add(null);
					continue;
				}
				// 取出属性的值
				Object value = new PropertyDescriptor(fieldEntry.getKey(), obj.getClass()).getReadMethod().invoke(obj);

				tempRow.add(toString(fieldEntry.getKey(), value, fieldEntry.getValue()));
			}
			tempRows.add(tempRow.toArray(new String[tempRow.size()]));
		}
		append(tempRows);
	}

	/**
	 * 将对象值转换为 String。
	 * 
	 * @param name
	 *            Object 属性的名称。
	 * @param value
	 *            Object 属性的值。
	 * @param escapeStyle
	 *            转义方式，转义方式支持国际化中 ResourceBundle 的键（用String
	 *            表示）和格式化（继承于Format），转义方式方式为空表示不做转义。
	 * @return 对象的String值。
	 */
	protected String toString(String name, Object value, Object escapeStyle) {
		if (value == null) {
			return null;// 值为空
		} else if (escapeStyle == null) {
			return value.toString();// 不用转义
		} else if (escapeStyle instanceof Format) {
			return ((Format) escapeStyle).format(value);// 用格式化转义
		} else if (escapeStyle instanceof String) {
			return getString(escapeStyle.toString() + "." + value.toString());// 用国际化转义
		} else {
			throw new IllegalArgumentException(escapeStyle.getClass().getName());// 不支持
		}
	}

	public String getString(String key) {
		return resourceBundle.getString(key);
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void append(List<String[]> rows) throws IOException {
		writer.append(rows);
	}

}
