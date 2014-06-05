package engine.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.PropertiesContainer;

import net.sf.json.JSONArray;
import engine.index.dao.Indexer.BasicIndexer;
import engine.index.dao.bookmarkParser.BookmarkParser;
import engine.index.dao.crawler.BasicCrawlController;
import engine.index.vo.IndexDataBean;
import engine.index.vo.IndexResultDataBean;

public class IndexEngine {

	/**
	 * @param args
	 */
	public IndexResultDataBean start(String filename, String donator) {
		// TODO Auto-generated method stub
		//String filename = "/data/bookmark/chrome/Bookmarks_13.12.04";		// 북마크 저장 경로
		int numberOfCrawlers = 
				Integer.parseInt(PropertiesContainer.prop.getProperty("numberOfCrawler"));
		// crawler thread 수 - 5개(기본)
		String crawlStorageFolder = PropertiesContainer.prop.getProperty("crawlStorageFolder");
		// "/data/root";		// crawl한 문서 저장 경로
		String indexPath = PropertiesContainer.prop.getProperty("indexPath");
		// "/data/indexs";		// index할 문서 저장 경로
		String docsPath = PropertiesContainer.prop.getProperty("docsPath");
		// "/data/docs/content";		// index된 문서의 저장 경로
		
		HashMap<Integer, IndexDataBean> newIdxBeanMap;
		HashMap<Integer, Integer> updateIdxNHit;
		List<Integer> newIdxList;
		ArrayList<Integer> updateIdxList;
		
		String contributor = null;
		
		int totalBookmarkSize = 0;
		int totalURLListSize = 0;
		int assignedIdxNum = 0;
		
		IndexResultDataBean indexResultDataBean = new IndexResultDataBean();
		//////////////////////////////////////////////////////////////////////////
		BookmarkParser bookmarkParser = new BookmarkParser();		//bookmark Parser
		//////////////////////////////////////////////////////////////////////////
		BasicCrawlController basicCrawlController = new BasicCrawlController();
				
		JSONArray outArray = bookmarkParser.Parser(filename);
		basicCrawlController.start(outArray, donator, crawlStorageFolder, numberOfCrawlers);
		// crawler
		newIdxBeanMap = new HashMap<Integer, IndexDataBean>(BasicCrawlController.getNewIdxMap());
		updateIdxNHit = new HashMap<Integer, Integer>(BasicCrawlController.getUpdateIdxNHit());
		newIdxList = new ArrayList<Integer>(BasicCrawlController.getNewIdxList());
		updateIdxList = new ArrayList<Integer>(BasicCrawlController.getUpdateIdxList());
		// 값들을 index에 반영하기 위해서 옮김
		totalBookmarkSize = BasicCrawlController.getTotalBookmarkSize();		// 등록한 북마크의 url 갯수
		totalURLListSize = BasicCrawlController.getTotalURLListSize();		// 현재 서버에 등록된 url 갯수
		assignedIdxNum = BasicCrawlController.getNewIdxList().size();		// 실제 인덱스에 반영될 문서의 갯수
		contributor = donator;
		///////////////////////////////////////////////////////////////////////////////////
		BasicIndexer basicIndexer = new BasicIndexer(indexPath, docsPath, contributor, newIdxBeanMap, updateIdxNHit, newIdxList, updateIdxList);	
		int indexCount = basicIndexer.index();		// indexer
		//////////////////////////////////////////////////////////////////////////////////
		System.out.println("number of Indexing doc : " + indexCount);
		
		indexResultDataBean.setTotalBookmarkSize(totalBookmarkSize);
		indexResultDataBean.setTotalURLListSize(totalURLListSize);
		indexResultDataBean.setAssignedIdxNum(assignedIdxNum);
		
		return indexResultDataBean;
	}

}
