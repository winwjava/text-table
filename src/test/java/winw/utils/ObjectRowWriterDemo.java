package winw.utils;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ObjectRowWriterDemo {

	public static void main(String[] args) throws IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchFieldException, SecurityException, IntrospectionException {

		RowWriter fileRowWriter = FileRowAccessUtils.newRowWriter("D:/Test/temp.txt");
		List<Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>();
		list.add(new AbstractMap.SimpleEntry<String, Object>("name", null));
		list.add(new AbstractMap.SimpleEntry<String, Object>("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
		// list.add(new AbstractMap.SimpleEntry<String, Object>("state",
		// "allSmsMtState"));
		ObjectRowWriter<WriteObject> objectRowWriter = new ObjectRowWriter<WriteObject>(fileRowWriter, list, null);

		List<String[]> heads = new ArrayList<String[]>();
		heads.add(new String[] { "名称", "日期" });
		objectRowWriter.append(heads);

		List<WriteObject> objects = new ArrayList<WriteObject>();

		objects.add(new WriteObject("水调歌头", new Date(), 3));
		objects.add(new WriteObject("水十多个头", new Date(), 4));

		objectRowWriter.appendObjects(objects);

		objectRowWriter.close();
	}

	static class WriteObject {
		private String name;

		private Date date;

		private int state;

		public WriteObject(String name, Date date, int state) {
			super();
			this.name = name;
			this.date = date;
			this.state = state;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

	}

}
