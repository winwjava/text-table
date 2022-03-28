//package winw.util;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.Reader;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;
//import java.text.DateFormat;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.poifs.filesystem.POIFSFileSystem;
//import org.apache.poi.ss.formula.eval.ErrorEval;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.DataFormatter;
//import org.apache.poi.ss.usermodel.DateUtil;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
//
//import au.com.bytecode.opencsv.CSVReader;
//import au.com.bytecode.opencsv.CSVWriter;
//
///**
// * Factory and utility methods for {@link RowReader} and {@link RowWriter}
// * classes defined in this package. Different types of files using different
// * ways to read and write. This class supports the following kinds of files:
// * 
// * <table BORDER CELLPADDING=3 CELLSPACING=1>
// * <tr>
// * <td>FileType</td>
// * <td ALIGN=CENTER><em>Streaming</em></td>
// * <td ALIGN=CENTER><em>Buffered</em></td>
// * <td ALIGN=CENTER><em>Dependency</em></td>
// * <td ALIGN=CENTER><em>Reader</em></td>
// * <td ALIGN=CENTER><em>Writer</em></td>
// * <td ALIGN=CENTER><em>See</em></td>
// * </tr>
// * <tr>
// * <td><b>TXT</b></td>
// * <td>Yes</td>
// * <td>Yes</td>
// * <td>JDK</td>
// * <td>BufferedReader</td>
// * <td>BufferedWriter</td>
// * <td>Java SE doc</td>
// * </tr>
// * <tr>
// * <td><b>CSV</b></td>
// * <td>Yes</td>
// * <td>Yes</td>
// * <td>OpenCSV</td>
// * <td>CSVReader</td>
// * <td>CSVWriter</td>
// * <td>http://opencsv.sourceforge.net/</td>
// * </tr>
// * <tr>
// * <td><b>XLS</b></td>
// * <td>No</td>
// * <td>No</td>
// * <td>POI</td>
// * <td>HSSFWorkbook</td>
// * <td>HSSFWorkbook</td>
// * <td>http://poi.apache.org/</td>
// * </tr>
// * <tr>
// * <td><b>XLSX</b></td>
// * <td>Yes</td>
// * <td>Yes</td>
// * <td>POI</td>
// * <td>XSSFWorkbook</td>
// * <td>XSSFWorkbook</td>
// * <td>http://poi.apache.org/</td>
// * </tr>
// * </table>
// * 
// * <p>
// * Default, Auto-detector the character files's charset, including TXT and CSV.
// * Please see
// * <a href="http://code.google.com/p/juniversalchardet/"><i> juniversalchardet
// * </i>.
// * </p>
// * <h4>Note: Excel, the date or time is formatted as "yyyy-MM-dd HH:mm:ss", the
// * numeric is formatted as "#.##########".</h4>
// * 
// * @see RowReader
// * @see RowWriter
// * 
// * @author sjyao
// *
// */
//public class FileRowAccessUtils {
//	/**
//	 * File (extension name) type, is used to specify read and write files the
//	 * way.
//	 * 
//	 * @author sjyao
//	 */
//	public enum FileType {
//		TXT, CSV, XLS, XLSX
//	};
//
//	/** Default file encoding. */
//	public static final String DEFAULT_CHARSET_NAME = "GBK";
//
//	/** Default line terminator uses system property "line.separator". */
//	public static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
//
//	/** Unix/Linux line terminator character. */
//	public static final String LINUX_LINE_SEPARATOR = "\n";
//
//	/** Windows line terminator character. */
//	public static final String WINDOWS_LINE_SEPARATOR = "\r\n";
//
//	/**
//	 * Creates a RowReader by opening a connection to an actual file, the file
//	 * named by the path name name in the file system.
//	 * 
//	 * @param filename
//	 *            the system-dependent file name.
//	 * @return the newly created file row reader.
//	 * @throws IOException
//	 */
//	public static RowReader newRowReader(String filename) throws IOException {
//		return newRowReader(filename, getFileTypeByExtension(filename));
//	}
//
//	public static RowReader newRowReader(InputStream inputStream, String filename) throws IOException {
//		FileType fileType = getFileTypeByExtension(filename);
//
//		switch (fileType) {
//		case TXT:
//			return new TXTRowAccess(inputStream);
//		case CSV:
//			return new CSVRowAccess(inputStream);
//		case XLS:
//			return new XLSRowAccess(inputStream);
//		case XLSX: {
//			File f = File.createTempFile("batchlist_", ".tmp");
//			Files.copy(inputStream, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
//			return new XSSFStreamRowReader(f.getAbsolutePath());
//		}
//		default:
//			throw new IOException("Unsupported FileType:" + fileType);
//		}
//	}
//
//	/**
//	 * Creates a RowReader that uses the a fileType.
//	 * 
//	 * @param filename
//	 *            the system-dependent file name.
//	 * @param fileType
//	 *            The type of a supported fileType
//	 * @return the newly created file row reader.
//	 * @throws IOException
//	 */
//	public static RowReader newRowReader(String filename, FileType fileType) throws IOException {
//		switch (fileType) {
//		case TXT:
//			return new TXTRowAccess(new FileInputStream(filename), getFileDetectedCharset(filename));
//		case CSV:
//			return new CSVRowAccess(new FileInputStream(filename), getFileDetectedCharset(filename));
//		case XLS:
//			return new XLSRowAccess(new FileInputStream(filename));
//		case XLSX:
//			return new XSSFStreamRowReader(filename);
//		default:
//			throw new IOException("Unsupported FileType:" + fileType);
//		}
//	}
//
//	/**
//	 * Creates a RowReader that uses the named charset.
//	 * 
//	 * @param inputStream
//	 *            An InputStream.
//	 * @param charsetName
//	 *            The name of a supported charset.
//	 * @param fileType
//	 *            The type of a supported fileType.
//	 * @return the newly created file row reader.
//	 * @throws IOException
//	 */
//	public static RowReader newRowReader(InputStream inputStream, FileType fileType, String charsetName)
//			throws IOException {
//		switch (fileType) {
//		case TXT:
//			return new TXTRowAccess(inputStream, charsetName);
//		case CSV:
//			return new CSVRowAccess(inputStream, charsetName);
//		case XLS:
//			return new XLSRowAccess(inputStream);
//		case XLSX: {
//			File f = File.createTempFile("batchlist_", ".tmp");
//			Files.copy(inputStream, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
//			return new XSSFStreamRowReader(f.getAbsolutePath());
//		}
//		default:
//			throw new IOException("Unsupported FileType:" + fileType);
//		}
//	}
//
//	/**
//	 * Creates a RowWriter by opening a connection to an actual file, the file
//	 * named by the path name name in the file system.
//	 * 
//	 * @param filename
//	 *            the system-dependent file name.
//	 * @return the newly created file row writer.
//	 * @throws IOException
//	 */
//	public static RowWriter newRowWriter(String filename) throws IOException {
//		return newRowWriter(filename, getFileTypeByExtension(filename));
//	}
//
//	/**
//	 * Creates a RowWriter that uses the a fileType.
//	 * 
//	 * @param filename
//	 *            the system-dependent file name.
//	 * @param fileType
//	 *            The type of a supported fileType.
//	 * @return the newly created file row writer.
//	 * @throws IOException
//	 */
//	public static RowWriter newRowWriter(String filename, FileType fileType) throws IOException {
//		return newRowWriter(new FileOutputStream(filename), fileType);
//	}
//
//	/**
//	 * Creates a RowWriter that uses the a charsetName, and specify a newline.
//	 * 
//	 * @param filename
//	 * @param fileType
//	 *            The type of a supported fileType.
//	 * @param charsetName
//	 * @param lineSeparator
//	 * @return the newly created file row writer.
//	 * @throws IOException
//	 */
//	public static RowWriter newRowWriter(String filename, FileType fileType, String charsetName, String lineSeparator)
//			throws IOException {
//		return newRowWriter(new FileOutputStream(filename), fileType, charsetName, lineSeparator);
//	}
//
//	/**
//	 * Creates a RowWriter that uses the a fileType.
//	 * 
//	 * @param outputStream
//	 *            an OutputStream.
//	 * @param fileType
//	 *            The type of a supported fileType.
//	 * @return the newly created file row writer.
//	 * @throws IOException
//	 */
//	public static RowWriter newRowWriter(OutputStream outputStream, FileType fileType) throws IOException {
//		return newRowWriter(outputStream, fileType, DEFAULT_CHARSET_NAME, DEFAULT_LINE_SEPARATOR);
//	}
//
//	/**
//	 * Creates a RowWriter that uses the a charsetName, and specify a newline.
//	 * 
//	 * @param outputStream
//	 *            an OutputStream.
//	 * @param fileType
//	 *            The type of a supported fileType.
//	 * @param charsetName
//	 * @param lineSeparator
//	 * @return the newly created file row writer.
//	 * @throws IOException
//	 */
//	public static RowWriter newRowWriter(OutputStream outputStream, FileType fileType, String charsetName,
//			String lineSeparator) throws IOException {
//		switch (fileType) {
//		case TXT:
//			return new TXTRowAccess(outputStream, charsetName, lineSeparator);
//		case CSV:
//			return new CSVRowAccess(outputStream, charsetName, lineSeparator);
//		case XLS:
//			return new XLSRowAccess(outputStream);
//		case XLSX:
//			return new XLSXRowAccess(outputStream);
//		default:
//			throw new IOException("Unsupported FileType:" + fileType);
//		}
//	}
//
//	/**
//	 * File type by file extension.
//	 * 
//	 * @param filename
//	 *            A string of the file name.
//	 * @return The type of a supported fileType, or return null means can not be
//	 *         determined.
//	 */
//	public static FileType getFileTypeByExtension(String filename) {
//		if (filename == null || filename.length() == 0 || !filename.contains(".")) {
//			return null;
//		}
//		String extension = filename.substring(filename.lastIndexOf('.') + 1).toUpperCase();
//		if ("TXT".equals(extension)) {
//			return FileType.TXT;
//		} else if ("CSV".equals(extension)) {
//			return FileType.CSV;
//		} else if ("XLS".equals(extension)) {
//			return FileType.XLS;
//		} else if ("XLSX".equals(extension)) {
//			return FileType.XLSX;
//		} else {
//			return null;
//		}
//	}
//
//	/**
//	 * After reset(), you can reuse the detector to process another document.
//	 */
//	private static org.mozilla.universalchardet.UniversalDetector charsetDetector = new org.mozilla.universalchardet.UniversalDetector(
//			null);
//
//	private static Set<String> charsets = new HashSet<String>();
//
//	static {
//		// UNICODE(Universal Transformation Format, RFC 3629)
//		charsets.add("UTF-8".intern());
//		charsets.add("UTF-16BE".intern());
//		charsets.add("UTF-16LE".intern());
//		charsets.add("UTF-32BE".intern());
//		charsets.add("UTF-32LE".intern());
//
//		// MBCS(Multi-Byte Chactacter System)
//		charsets.add("BIG5".intern());
//		charsets.add("GB18030".intern());
//		// charsets.add("EUC-TW".intern());
//		// charsets.add("EUC-KR".intern());
//		// charsets.add("EUC-JP".intern());
//		// charsets.add("SHIFT_JIS".intern());
//
//		// SBCS(Single-Byte Chactacter System)
//		// charsets.add("WINDOWS-1251".intern());
//		// charsets.add("KOI8-R".intern());
//		// charsets.add("ISO-8859-5".intern());
//		// charsets.add("MACCYRILLIC".intern());
//		// charsets.add("IBM866".intern());
//		// charsets.add("IBM855".intern());
//		// charsets.add("ISO-8859-7".intern());
//		// charsets.add("WINDOWS-1253".intern());
//		// charsets.add("ISO-8859-5".intern());
//		// charsets.add("WINDOWS-1251".intern());
//		// charsets.add("WINDOWS-1255".intern());
//
//		// Latin-1
//		// charsets.add("WINDOWS-1252".intern());
//	}
//
//	/**
//	 * Detect charset from file header read 4096 bytes.
//	 * 
//	 * @param filename
//	 *            A string of the file name.
//	 * @return The detected encoding is returned, if the detector can determine
//	 *         and the {@link #charsets} contains, else return
//	 *         {@link #DEFAULT_CHARSET_NAME}
//	 * @throws IOException
//	 *             if an I/O error occurs.
//	 */
//	public static synchronized String getFileDetectedCharset(String filename) throws IOException {
//		int nread;
//		byte[] buf = new byte[4096];
//		FileInputStream fis = new FileInputStream(filename);
//		try {
//			charsetDetector.reset();
//			if ((nread = fis.read(buf)) > 0) {
//				charsetDetector.handleData(buf, 0, nread);
//			}
//			if (charsetDetector.isDone()) {
//				charsetDetector.dataEnd();
//				String encoding = charsetDetector.getDetectedCharset();
//
//				return (encoding != null) ? encoding : DEFAULT_CHARSET_NAME;
//			} else {
//				charsetDetector.dataEnd();
//				String encoding = charsetDetector.getDetectedCharset();
//
//				return (encoding != null && charsets.contains(encoding)) ? encoding : DEFAULT_CHARSET_NAME;
//			}
//		} finally {
//			fis.close();
//		}
//	}
//
//	/**
//	 * 方法不关闭inpustream，因为inpustream还有其他读取用处，调用请注意内存泄露问题
//	 * 
//	 * @param inputStream
//	 * @return
//	 * @throws IOException
//	 */
//	public static synchronized String getFileDetectedCharset(InputStream inputStream) throws IOException {
//		int nread;
//		byte[] buf = new byte[4096];
//		charsetDetector.reset();
//		if ((nread = inputStream.read(buf)) > 0) {
//			charsetDetector.handleData(buf, 0, nread);
//		}
//		if (charsetDetector.isDone()) {
//			charsetDetector.dataEnd();
//			String encoding = charsetDetector.getDetectedCharset();
//
//			return (encoding != null) ? encoding : DEFAULT_CHARSET_NAME;
//		} else {
//			charsetDetector.dataEnd();
//			String encoding = charsetDetector.getDetectedCharset();
//
//			return (encoding != null && charsets.contains(encoding)) ? encoding : DEFAULT_CHARSET_NAME;
//		}
//	}
//
//	/**
//	 * Read the first character, detecting and skipping the BOM character. if
//	 * the first character is not a BOM, then reset the <code>Reader</code>.
//	 * <p>
//	 * <a href="http://www.unicode.org/glossary/#byte_order_mark">Byte Order
//	 * Mark (BOM)</a>. The Unicode character U+FEFF when used to indicate the
//	 * byte order of a text.
//	 * </p>
//	 * <p>
//	 * A BOM can be used as a signature no matter how the Unicode text is
//	 * transformed: UTF-16, UTF-8, or UTF-32. The exact bytes comprising the BOM
//	 * will be whatever the Unicode character U+FEFF is converted into by that
//	 * transformation format.
//	 * <p>
//	 * 
//	 * @param reader
//	 *            An Reader
//	 * @throws IOException
//	 *             If an I/O error occurs
//	 * @see <a href="http://en.wikipedia.org/wiki/Byte_order_mark">Wikipedia -
//	 *      Byte Order Mark</a>
//	 */
//	private static void skipFirstBOM(Reader reader) throws IOException {
//		char[] firstChar = new char[1];
//		reader.mark(1);// marking the first character
//
//		reader.read(firstChar);
//		// if the first character is not a BOM, then invoke the reset()
//		if (firstChar[0] != '\uFEFF')
//			reader.reset();
//	}
//
//	// TXT
//	private static class TXTRowAccess implements RowReader, RowWriter {
//
//		private String txtSplitRegex = ",";
//		private String lineSeparator = "\n";
//		private BufferedReader txtReader = null;
//		private BufferedWriter txtWriter = null;
//
//		public TXTRowAccess(InputStream inputStream) throws IOException {
//			txtReader = new BufferedReader(new InputStreamReader(inputStream));
//		}
//
//		public TXTRowAccess(InputStream inputStream, String charsetName) throws IOException {
//			txtReader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
//			skipFirstBOM(txtReader);
//		}
//
//		public TXTRowAccess(OutputStream outputStream, String charsetName, String lineSeparator)
//				throws IOException {
//			this.lineSeparator = lineSeparator;
//			txtWriter = new BufferedWriter(new OutputStreamWriter(outputStream, charsetName));
//		}
//
//		private boolean hasNext = true;
//
//		@Override
//		public boolean hasNext() {
//			return hasNext;
//		}
//
//		@Override
//		public List<String[]> read(int size) throws IOException {
//			if (size <= 0) {
//				throw new IllegalArgumentException("Reads the size must be greater than 0: " + size);
//			}
//			List<String[]> rows = new ArrayList<String[]>();
//
//			String line = null;
//			for (int i = 0; i < size; i++) {
//				if ((line = txtReader.readLine()) != null) {
//					rows.add(line.split(txtSplitRegex, -1));
//				} else {
//					hasNext = false;
//					break;
//				}
//			}
//			return rows;
//		}
//
//		@Override
//		public void append(List<String[]> rows) throws IOException {
//			if (rows == null || rows.size() <= 0) {
//				return;
//			}
//			for (int i = 0; i < rows.size(); i++) {
//				txtWriter.write(join(rows.get(i), txtSplitRegex));// \t
//				txtWriter.write(lineSeparator);// txtWriter.newLine();
//			}
//		}
//
//		private static String join(String[] array, String separator) {
//			if (array == null) {
//				return "";
//			}
//			StringBuffer buf = new StringBuffer("");
//			for (int i = 0; i < array.length; i++) {
//				if (i > 0) {
//					buf.append(separator);
//				}
//				if (array[i] != null) {
//					buf.append(array[i]);
//				}
//			}
//			return buf.toString();
//		}
//
//		@Override
//		public void flush() throws IOException {
//			txtWriter.flush();
//		}
//
//		@Override
//		public void close() throws IOException {
//			if (txtReader != null) {
//				txtReader.close();
//				txtReader = null;
//			}
//			if (txtWriter != null) {
//				txtWriter.close();
//				txtWriter = null;
//			}
//		}
//
//	}
//
//	// CSV
//	private static class CSVRowAccess implements RowReader, RowWriter {
//		private CSVReader csvReader = null;
//		private CSVWriter csvWriter = null;
//
//		public CSVRowAccess(InputStream inputStream) throws IOException {
//			csvReader = new CSVReader(new BufferedReader(new InputStreamReader(inputStream)));
//		}
//
//		public CSVRowAccess(InputStream inputStream, String charsetName) throws IOException {
//			Reader reader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
//			skipFirstBOM(reader);
//			csvReader = new CSVReader(reader);
//		}
//
//		public CSVRowAccess(OutputStream outputStream, String charsetName, String lineSeparator)
//				throws IOException {
//			csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(outputStream, charsetName)),
//					CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER,
//					lineSeparator);
//		}
//
//		private boolean hasNext = true;
//
//		@Override
//		public boolean hasNext() {
//			return hasNext;
//		}
//
//		@Override
//		public List<String[]> read(int size) throws IOException {
//			if (size <= 0) {
//				throw new IllegalArgumentException("Reads the size must be greater than 0: " + size);
//			}
//			List<String[]> rows = new ArrayList<String[]>();
//
//			String[] row = null;
//			for (int j = 0; j < size; j++) {
//				if ((row = csvReader.readNext()) != null) {
//					rows.add(row);
//				} else {
//					hasNext = false;
//					break;
//				}
//			}
//			return rows;
//		}
//
//		@Override
//		public void append(List<String[]> rows) throws IOException {
//			if (rows == null || rows.size() <= 0) {
//				return;
//			}
//			for (int i = 0; i < rows.size(); i++) {
//				if (!csvWriter.checkError()) {
//					csvWriter.writeNext(rows.get(i));
//				} else {
//					throw new IOException();
//				}
//			}
//		}
//
//		@Override
//		public void flush() throws IOException {
//			csvWriter.flush();
//		}
//
//		@Override
//		public void close() throws IOException {
//			if (csvReader != null) {
//				csvReader.close();
//				csvReader = null;
//			}
//			if (csvWriter != null) {
//				csvWriter.close();
//				csvWriter = null;
//			}
//		}
//	}
//
//	// XLS
//	private static class XLSRowAccess implements RowReader, RowWriter {
//		protected int rownum = 0;
//
//		protected Sheet sheet;
//		protected Workbook workbook;
//		protected InputStream inputStream;
//		protected OutputStream outputStream;
//
//		public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		public NumberFormat numberFormat = new DecimalFormat("#.##########");
//		public DataFormatter dataFormatter = new DataFormatter();
//
//		private XLSRowAccess() {
//			super();
//		}
//
//		public XLSRowAccess(InputStream inputStream) throws IOException {
//			this.inputStream = inputStream;
//			this.workbook = new HSSFWorkbook(new POIFSFileSystem(inputStream));
//			this.sheet = nextSheet(workbook);
//		}
//
//		public XLSRowAccess(OutputStream outputStream) throws IOException {
//			this.workbook = new HSSFWorkbook();
//			this.sheet = workbook.createSheet();
//			this.outputStream = outputStream;
//		}
//
//		private int sheetIndex = -1;
//
//		protected Sheet nextSheet(Workbook workbook) {
//			sheetIndex += 1;
//			if (sheetIndex < workbook.getNumberOfSheets()) {
//				return workbook.getSheetAt(sheetIndex);
//			}
//			return null;
//		}
//
//		private boolean hasNext = true;
//
//		@Override
//		public boolean hasNext() {
//			return hasNext;
//		}
//
//		/*
//		 * @ReviewCode @author gqliu @Date 2013-07-30 16:35
//		 */
//		@Override
//		public List<String[]> read(int size) throws IOException {
//			if (size <= 0) {
//				throw new IllegalArgumentException("Reads the size must be greater than 0: " + size);
//			}
//			List<String[]> rows = new ArrayList<String[]>(size);
//			if (sheet == null && (sheet = nextSheet(workbook)) == null) {
//				hasNext = false;
//				return rows;
//			}
//			int maxnum = sheet.getLastRowNum();
//			for (; rownum <= maxnum && rows.size() < size; rownum++) {
//				String[] rowArray = readRow(sheet.getRow(rownum));
//				if (rowArray != null && rowArray.length > 0) {
//					rows.add(rowArray);
//				}
//			}
//
//			if (rows.size() < size) {
//				rownum = 0;
//				sheet = nextSheet(workbook);
//				rows.addAll(read(size - rows.size()));
//			}
//			return rows;
//		}
//
//		protected String[] readRow(Row row) throws IOException {
//			if (row == null || row.getPhysicalNumberOfCells() <= 0) {
//				return null;
//			}
//			int lastCell = row.getLastCellNum();
//			String[] rowArray = new String[lastCell];
//			for (int k = 0; k < lastCell; k++) {
//				Cell cell = row.getCell(k);
//				if (cell == null) {
//					continue;
//				}
//				rowArray[k] = readCell(cell);
//			}
//			return rowArray;
//		}
//
//		protected String readCell(Cell cell) {
//			String value = null;
//			switch (cell.getCellType()) {
//			case Cell.CELL_TYPE_STRING:
//				// rowArray[k] = cell.getStringCellValue();
//				value = cell.getRichStringCellValue().getString();
//				break;
//			case Cell.CELL_TYPE_NUMERIC:
//				try {
//					if (DateUtil.isCellDateFormatted(cell)) {
//						value = dateFormat.format(cell.getDateCellValue());
//					} else {
//						value = numberFormat.format(cell.getNumericCellValue());
//					}
//				} catch (Exception e) {
//				}
//				break;
//			case Cell.CELL_TYPE_BOOLEAN:
//				value = Boolean.toString(cell.getBooleanCellValue());
//				break;
//			case Cell.CELL_TYPE_ERROR:
//				value = ErrorEval.getText(cell.getErrorCellValue());
//				break;
//			case Cell.CELL_TYPE_FORMULA:
//				value = readFormulaCell(cell);
//				break;
//			case Cell.CELL_TYPE_BLANK:
//				value = null;
//				break;
//			default:
//				throw new RuntimeException("Unexpected cell type (" + cell.getCellType() + ")");
//			}
//			return value;
//		}
//
//		protected boolean shouldEvaluateFormulas = true;
//
//		protected String readFormulaCell(Cell cell) {
//			String value = null;
//			if (!shouldEvaluateFormulas) {
//				value = cell.getCellFormula();
//			} else {
//				switch (cell.getCachedFormulaResultType()) {
//				case Cell.CELL_TYPE_STRING:
//					value = cell.getRichStringCellValue().getString();
//					break;
//				case Cell.CELL_TYPE_NUMERIC:
//					CellStyle style = cell.getCellStyle();
//					if (style == null) {
//						try {
//							if (DateUtil.isCellDateFormatted(cell)) {
//								value = dateFormat.format(cell.getDateCellValue());
//							} else {
//								value = numberFormat.format(cell.getNumericCellValue());
//							}
//						} catch (Exception e) {
//						}
//					} else {
//						value = dataFormatter.formatRawCellContents(cell.getNumericCellValue(), style.getDataFormat(),
//								style.getDataFormatString());
//					}
//					break;
//				case Cell.CELL_TYPE_BOOLEAN:
//					value = Boolean.toString(cell.getBooleanCellValue());
//					break;
//				case Cell.CELL_TYPE_ERROR:
//					value = ErrorEval.getText(cell.getErrorCellValue());
//					break;
//				case Cell.CELL_TYPE_BLANK:
//					value = null;
//					break;
//				default:
//					throw new RuntimeException("Unexpected cell type (" + cell.getCellType() + ")");
//				}
//			}
//			return value;
//		}
//
//		@Override
//		public void append(List<String[]> rows) throws IOException {
//			for (int i = 0; i < rows.size(); i++) {
//				Row row = sheet.createRow(rownum++);
//				for (int j = 0; j < rows.get(i).length; j++) {
//					Cell cell = row.createCell(j);
//					if (rows.get(i)[j] != null) {
//						cell.setCellValue(rows.get(i)[j]);
//					}
//				}
//			}
//		}
//
//		@Override
//		public void flush() throws IOException {
//		}
//
//		private void flush1() throws IOException {
//			if (workbook != null && outputStream != null) {
//				workbook.write(outputStream);
//				outputStream.flush();
//			}
//		}
//
//		@Override
//		public void close() throws IOException {
//			if (inputStream != null) {
//				inputStream.close();
//				inputStream = null;
//			}
//			if (outputStream != null) {
//				flush1();
//				outputStream.close();
//				outputStream = null;
//			}
//		}
//	}
//
//	// XLSX
//	private static class XLSXRowAccess extends XLSRowAccess {
//
//		public XLSXRowAccess(OutputStream outputStream) throws IOException {
//			workbook = new SXSSFWorkbook(500);
//			sheet = workbook.createSheet();
//			this.outputStream = outputStream;
//		}
//	}
//
//}
