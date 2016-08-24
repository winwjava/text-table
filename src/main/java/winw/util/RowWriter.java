package winw.util;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.List;

/**
 * Batch rows is written to the file, each row to write a single line of.
 * 
 * @see java.io.Writer
 * 
 * @author sjyao
 *
 */
public interface RowWriter extends Closeable, Flushable {
	/**
	 * Appends the rows to the file.
	 * 
	 * @param rows
	 *            rows list, Each string array is a row.
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	void append(List<String[]> rows) throws IOException;

}
