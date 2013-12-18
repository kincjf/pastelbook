package common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileContainer {

	public static void SaveFile(String html, String content, int idxNum) { // html, content 파일 저장
		// String filename = GetSTRFilter(title);
		System.out.println("filename : " + idxNum);
		
		String htmlFileFolder = PropertiesContainer.prop.getProperty("saveHtmlFile");
		String contentFileFolder = PropertiesContainer.prop.getProperty("saveContentFile");
		
		File htmlFile = new File(htmlFileFolder + idxNum + ".html");
		File contentFile = new File(contentFileFolder + idxNum + ".txt");

		try {
			if (htmlFile.createNewFile() == true) { // HTML파일 저장
				BufferedWriter output = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								htmlFile.getPath()), "UTF8"));

				output.write(html);
				output.close();
			}

			if (contentFile.createNewFile() == true) { // CONTENT파일 저장
				BufferedWriter output = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								contentFile.getPath()), "UTF8"));

				output.write(content);
				output.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("BasicCrawler - SaveFile : " + idxNum);
			e.printStackTrace();
		}

	}
	
	public static String GetSTRFilter(String str) {
		/*String[] filter_word = { " ", "\\.", "\\?", "\\/", "\\>", "\\!", "\\@",
				"\\#", "\\$", "\\%", "\\^", "\\&", "\\*", "\\(", "\\)", "\\_",
				"\\+", "\\=", "\\|", "\\\\", "\\}", "\\]", "\\{", "\\[",
				"\\\"", "\\'", "\\:", "\\;", "\\<", "\\,", "\\>", "\\.", "\\?",
				"\\/" };*/
		String[] filter_word = { " ", "\\?", "\\/", "\\>", "\\&", "\\*", "\\(", "\\)", "\\_",
				"\\|", "\\\\", "\\\"", "\\'", "\\:", "\\<", "\\,", "\\>", "\\?", "\\/" };
		
		for (int i = 0; i < filter_word.length && str.length() > 0; i++) {
			str = str.replaceAll(filter_word[i], " ");
		}

		return str;

	}
	
	public static String deleteExtension(String str) {
		int fileIdx = str.lastIndexOf(".");
		return str.substring(0, fileIdx);
	}
	
	public static String deleteAnchor(String str) {
		int fileIdx = str.lastIndexOf("#");
		String strTmp = str;
		
		if(fileIdx == -1) {
			return strTmp;
		} else {
			return str.substring(0, fileIdx);
		}
	}
}
