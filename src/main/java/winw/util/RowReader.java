package winw.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * Read the file, volume readout, said that each row cell array.
 * 
 * @see java.io.Reader
 * 
 * @author sjyao
 *
 */
public interface RowReader extends Closeable {

	/**
	 * Returns <tt>true</tt> if the has more rows.
	 *
	 * @return <tt>true</tt> if the file has more rows.
	 */
	boolean hasNext();

	/**
	 * Reads string array into an list, and not more than specified read size.
	 * 
	 * @param size
	 *            read size
	 * @return row list, each string array is a row.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	List<String[]> read(int size) throws IOException;

	/**
	 * Tells whether the reading is finished.
	 * 
	 * @return true has reached the end of, or there is next row.
	 */
	// boolean ended();
	/**
	 * Skips rows.
	 * 
	 * @param size
	 *            the number of row to be skipped.
	 * @return the actual number of row skipped.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	// long skip(long size) throws IOException;

}
