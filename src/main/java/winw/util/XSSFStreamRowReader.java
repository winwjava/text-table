package winw.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XSSF and SAX (Event API)
 * 
 * If memory footprint is an issue, then for XSSF, you can get at the underlying XML data, 
 * and process it yourself. This is intended for intermediate developers who are willing to 
 * learn a little bit of low level structure of .xlsx files, and who are happy processing XML 
 * in java. Its relatively simple to use, but requires a basic understanding of the file structure. 
 * The advantage provided is that you can read a XLSX file with a relatively small memory footprint.
 * 
 * <h4>Note: Excel, the time is formatted as "yyyy-MM-dd hh:mm:ss".</h4>
 * 
 * Guide: http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api
 * @author sjyao
 *
 */
public class XSSFStreamRowReader implements RowReader {

    enum xssfDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
    }

    private int minColumns;
    private OPCPackage xlsxPackage;

    private BlockingQueue<String[]> bufferRows = new LinkedBlockingQueue<String[]>(100);

    private Thread readThread;

    private volatile Exception readException;
    
    public XSSFStreamRowReader(String filename) throws IOException {
        this.minColumns = -1;
        try {
            this.xlsxPackage = OPCPackage.open(filename, PackageAccess.READ);
        } catch (InvalidFormatException e) {
            throw new IOException(e);
        }
        
        readThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    process();
                    hasNext.set(false);
                } catch (Exception e) {
                	readException = e;
                }
            }
        });
        readThread.start();
    }

    private AtomicBoolean hasNext = new AtomicBoolean(true);

    @Override
    public boolean hasNext() {
        if (hasNext.get()) {
            return true;
        }else{
            return !bufferRows.isEmpty();
        }
    }

    @Override
    public List<String[]> read(int size) throws IOException {
        if (size <= 0) {
            throw new IllegalArgumentException("Reads the size must be greater than 0: " + size);
        }
		if (readException != null) {
			throw new IOException(readException);
		}
        List<String[]> rows = new ArrayList<String[]>();
        if (!bufferRows.isEmpty()) {
            bufferRows.drainTo(rows, size);
        }

        while (hasNext.get() && size > rows.size()) {
            if (!bufferRows.isEmpty()) {
                bufferRows.drainTo(rows, size - rows.size());
            } else {
                try {
                    String[] tempRow = bufferRows.poll(10, TimeUnit.MILLISECONDS);
                    if (tempRow != null) {
                        rows.add(tempRow);
                    }
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
            }
        }
        return rows;
    }

    @Override
    public void close() throws IOException {
    	if(readThread != null && readThread.isAlive()){
        	readThread.interrupt();
    	}
    }

    public void process() throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {

        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
        XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
        StylesTable styles = xssfReader.getStylesTable();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        while (iter.hasNext()) {
            InputStream stream = iter.next();
            // String sheetName = iter.getSheetName();
            try {
				final SheetDataReader sheetDataReader = new SheetDataReader(styles, strings, minColumns);

				InputSource sheetSource = new InputSource(stream);
				SAXParserFactory saxFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = saxFactory.newSAXParser();
				XMLReader sheetParser = saxParser.getXMLReader();
				sheetParser.setContentHandler(sheetDataReader);
				sheetParser.parse(sheetSource);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
        }
    }

    public class SheetDataReader extends DefaultHandler {

        private int minColumns;

        private StylesTable stylesTable;

        private ReadOnlySharedStringsTable sharedStringsTable;

        private final int minColumnCount;

        private boolean vIsOpen;

        private xssfDataType nextDataType;

        private short formatIndex;
        private String formatString;
        private final DataFormatter formatter;

        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        private int thisColumn = -1;
        private int lastColumnNumber = -1;

        private StringBuffer value;

        private List<String> row = new ArrayList<String>();

        public SheetDataReader(StylesTable styles, ReadOnlySharedStringsTable strings, int cols) {
            this.stylesTable = styles;
            this.sharedStringsTable = strings;
            this.minColumnCount = cols;
            this.value = new StringBuffer();
            this.nextDataType = xssfDataType.NUMBER;
            this.formatter = new DataFormatter();
        }

        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("inlineStr".equals(name) || "v".equals(name) || "t".equals(name)) {
                vIsOpen = true;
                // Clear contents cache
                value.setLength(0);
            }
            // c => cell
            else if ("c".equals(name)) {
                // Get the cell reference
                String r = attributes.getValue("r");
                int firstDigit = -1;
                for (int c = 0; c < r.length(); ++c) {
                    if (Character.isDigit(r.charAt(c))) {
                        firstDigit = c;
                        break;
                    }
                }
                thisColumn = nameToColumn(r.substring(0, firstDigit));

                // Set up defaults.
                this.nextDataType = xssfDataType.NUMBER;
                this.formatIndex = -1;
                this.formatString = null;
                String cellType = attributes.getValue("t");
                String cellStyleStr = attributes.getValue("s");
                if ("b".equals(cellType))
                    nextDataType = xssfDataType.BOOL;
                else if ("e".equals(cellType))
                    nextDataType = xssfDataType.ERROR;
                else if ("inlineStr".equals(cellType))
                    nextDataType = xssfDataType.INLINESTR;
                else if ("s".equals(cellType))
                    nextDataType = xssfDataType.SSTINDEX;
                else if ("str".equals(cellType))
                    nextDataType = xssfDataType.FORMULA;
                else if (cellStyleStr != null) {
                    // It's a number, but almost certainly one
                    // with a special style or format
                    int styleIndex = Integer.parseInt(cellStyleStr);
                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                    this.formatIndex = style.getDataFormat();
                    this.formatString = style.getDataFormatString();
                    if (this.formatString == null)
                        this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
                }
            }

        }

        public void endElement(String uri, String localName, String name) throws SAXException {
            String thisStr = null;

            // v => contents of a cell
            if ("v".equals(name) || "t".equals(name)) {
                // Process the value contents as required.
                // Do now, as characters() may be called more than once
                switch (nextDataType) {

                case BOOL:
                    char first = value.charAt(0);
                    thisStr = first == '0' ? "FALSE" : "TRUE";
                    break;

                case ERROR:
                    thisStr = "\"ERROR:" + value.toString() + '"';
                    break;

                case FORMULA:
                    // A formula could result in a string value,
                    // so always add double-quote characters.
                    thisStr = value.toString();
                    break;

                case INLINESTR:
                    // TODO: have seen an example of this, so it's untested.
                    XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                    thisStr = rtsi.toString();
                    break;

                case SSTINDEX:
                    String sstIndex = value.toString();
                    try {
                        int idx = Integer.parseInt(sstIndex);
                        XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
                        thisStr = rtss.toString();
                    } catch (NumberFormatException ex) {
                        // TODO
                        // output.println("Failed to parse SST index '" +
                        // sstIndex +
                        // "': " + ex.toString());
                    }
                    break;

                case NUMBER:
                    String n = value.toString();
					if (this.formatString != null) {
						double doubleV = Double.parseDouble(n);
						// Is it a date?
						if (DateUtil.isADateFormat(this.formatIndex, this.formatString)) {
							thisStr = dateFormat.format(DateUtil.getJavaDate(doubleV, false));
						} else {// else Number
							thisStr = formatter.formatRawCellContents(doubleV, this.formatIndex, this.formatString);
						}
					} else {
						thisStr = n;
					}
                    break;

                default:
                    thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                    break;
                }

                // Output after we've seen the string contents
                // Emit commas for any fields that were missing on this row
                if (lastColumnNumber == -1) {
                    lastColumnNumber = 0;
                }
                for (int i = lastColumnNumber + 1; i < thisColumn; ++i) {
                    row.add(null);
                }

                // Might be the empty string.
                row.add(thisStr);
                // TODO output.print(thisStr);

                // Update column
                if (thisColumn > -1)
                    lastColumnNumber = thisColumn;

            } else if ("row".equals(name)) {

                // Print out any missing commas if needed
                if (minColumns > 0) {
                    // Columns are 0 based
                    if (lastColumnNumber == -1) {
                        lastColumnNumber = 0;
                    }
                    for (int i = lastColumnNumber; i < (this.minColumnCount); i++) {
                        // row.add(null);
                        // TODO output.print(',');
                    }
                }

                String[] temp = new String[row.size()];
                for (int i = 0; i < row.size(); i++) {
                    temp[i] = row.get(i);
                    // System.out.print(row.get(i) + "\t");
                }
                // System.out.println();

                try {
                    bufferRows.put(temp);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                row.clear();
                lastColumnNumber = -1;
            }

        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            if (vIsOpen)
                value.append(ch, start, length);
        }

        private int nameToColumn(String name) {
            int column = -1;
            for (int i = 0; i < name.length(); ++i) {
                int c = name.charAt(i);
                column = (column + 1) * 26 + c - 'A';
            }
            return column;
        }
    }
}
