package engine.index.action;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import common.PropertiesContainer;

import engine.index.IndexEngine;
import engine.index.vo.IndexResultDataBean;

public class IndexAction implements Action {
	@Override
	public ActionForward execute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ActionForward forward = new ActionForward();
		IndexResultDataBean indexResultDataBean = null;
		IndexEngine indexEngine = null;
		String propertiesFilePath = "E:/data/config.properties";
		
		
		// load properties file
		PropertiesContainer properties = new PropertiesContainer(propertiesFilePath);
		properties.loadFile();
		//properties.loadClasspathFile();
		
		String realBookmarkFolder = null;
		String saveBookmarkFolder = properties.prop.getProperty("saveBookmarkFolder");
		// "/data/bookmark/chrome" - 북마크를 저장할 경로
		
		int fileSize = 5 * 1024 * 1024;
		
		//realBookmarkFolder = request.getSession().getServletContext().getRealPath(saveBookmarkFolder);
		realBookmarkFolder = saveBookmarkFolder;
		try {
			MultipartRequest multi = null;
			
			multi = new MultipartRequest(
					request, realBookmarkFolder, fileSize, "utf-8", new DefaultFileRenamePolicy());
			// 북마크 저장
			Enumeration<?> files = multi.getFileNames();
			
			String file = (String) files.nextElement();
			String filename = multi.getFilesystemName(file);
			String donator = multi.getParameter("CONTRIBUTOR");
			
			System.out.println("filename " + filename);
			System.out.println("donator " + donator);
			
			String fileRealPath = realBookmarkFolder + "\\" + filename;
			System.out.println("fileRealPath - " + fileRealPath);
			
			indexEngine = new IndexEngine();
			indexResultDataBean = indexEngine.start(fileRealPath, donator);
			
			request.setAttribute("INDEX_RESULT", indexResultDataBean);
			
			System.out.println("IndexAction - Success");
			forward.setPath("/indexStatus.if");
			
			return forward;
			
		} catch (Exception ex) {
			System.err.println("IndexAction - err");
			ex.printStackTrace();
		}
		
		return null;
		
	}
}
