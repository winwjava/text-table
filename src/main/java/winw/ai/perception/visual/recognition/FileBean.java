package winw.ai.perception.visual.recognition;

import java.io.Serializable;
 
public class FileBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String fileFullPath;
	private String folderName;
	private String fileType;
 
	public String getFileType() {
		return fileType;
	}
 
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
 
	public String getFileFullPath() {
		return fileFullPath;
	}
 
	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}
 
	public String getFolderName() {
		return folderName;
	}
 
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
 
}