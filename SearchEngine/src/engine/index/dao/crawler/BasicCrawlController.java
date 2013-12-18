/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package engine.index.dao.crawler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import common.FileContainer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import engine.index.dao.db.IndexDAO;
import engine.index.vo.IndexDataBean;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawlController {
	private static List<Integer> newIdxList;	// 각각 할당될 idxNum (크롤러에 의해 +1씩 증가함)
	private static ArrayList<Integer> updateIdxList;
	
	private static ConcurrentHashMap<Integer, IndexDataBean> newIdxMap;
	private static HashMap<Integer, Integer> updateIdxNHit;
	static int totalBookmarkSize = 0;
	static int totalURLListSize = 0;
	
	static String contributor;
	
	static IndexDAO indexDAO = null;		// 너무 열고닫고를 많이해서 잠시 빌려쓰기 위해서..
	
	public void start(JSONArray list, String contributor, String crawlStorageFolder, int numberOfCrawlers) {
		/*
		 * crawlStorageFolder is a folder where intermediate crawl data is
		 * stored.
		 */
		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Be polite: Make sure that we don't send more than 1 request per
		 * second (1000 milliseconds between requests).
		 */
		config.setPolitenessDelay(1000);

		/*
		 * You can set the maximum crawl depth here. The default value is -1 for
		 * unlimited depth
		 */
		config.setMaxDepthOfCrawling(1);

		/*
		 * You can set the maximum number of pages to crawl. The default value
		 * is -1 for unlimited number of pages
		 */
		config.setMaxPagesToFetch(1000);

		/*
		 * Do you need to set a proxy? If so, you can use:
		 * config.setProxyHost("proxyserver.example.com");
		 * config.setProxyPort(8080);
		 * 
		 * If your proxy also needs authentication:
		 * config.setProxyUsername(username); config.getProxyPassword(password);
		 * Comment by
		 * 
		 * tharindu...@gmail.com, Sep 15, 2013: Isn't it
		 * proxy.setProxyPassword(password) ?
		 */

		/*
		 * This config parameter can be used to set your crawl to be resumable
		 * (meaning that you can resume the crawl from a previously
		 * interrupted/crashed crawl). Note: if you enable resuming feature and
		 * want to start a fresh crawl, you need to delete the contents of
		 * rootFolder manually.
		 */
		config.setResumableCrawling(false);

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig,
				pageFetcher);
		
		CrawlController controller = null;
		
		try {
			controller = new CrawlController(config, pageFetcher,
					robotstxtServer);
		

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		
		indexDAO = new IndexDAO();
		this.contributor = contributor;
		indexDAO.connection();		
		// db 접근이 많기 때문에 부하를 줄이기 위해서 connection과 close는 직접 한다.
		totalURLListSize = indexDAO.getURLListCount();
		// index 이름 정하는법 - db table(URL_DATA) size + 1 부터 시작해서 하나씩 증가한다. 
		
		ArrayList<String> urlList = indexDAO.getURLList();
		ArrayList<Integer> idxNumList = indexDAO.getIdxNumList();
		
		HashMap<String, Integer> urlNIdxNumMap = new HashMap<String, Integer>();
		
		Iterator<String> urlListIterator = urlList.iterator();
		Iterator<Integer> idxNumListIterator = idxNumList.iterator();
		
		String urlTmp = null;
		int idxNumTmp = 0;
		
		while(urlListIterator.hasNext() || idxNumListIterator.hasNext()) {
			urlTmp = urlListIterator.next();
			idxNumTmp = idxNumListIterator.next();
			
			urlNIdxNumMap.put(urlTmp, idxNumTmp);
		}		// url에 따른 idxNum(파일이름)을 매칭시키기 위해서
		
		// 일단 urlList를 가져온 후 비교한다.
		totalBookmarkSize = list.size();
		
		Iterator<JSONObject> iterator = list.iterator();
		
		newIdxList = Collections.synchronizedList(new ArrayList<Integer>());
		newIdxMap = new ConcurrentHashMap<Integer, IndexDataBean>();
		updateIdxList = new ArrayList<Integer>();
		updateIdxNHit = new HashMap<Integer, Integer>();
		
		String url = null;
		JSONObject obj = null;
		
		boolean isSuccess = true;
		int hit = 0;
		
		//boolean test = false;
		
		isSuccess = indexDAO.InsertContributor(contributor);		
		// 끝나고 하면 무결성에 어긋난다. 먼저 이름을 DB에 넣어주어야 크롤러가 추가할 수 있음
		
		while(iterator.hasNext()) {
			obj = iterator.next();
			
			url = obj.getString("url");		// BookMarkParser의 데이터
			url = FileContainer.deleteAnchor(url);		// anchor를 제거하고 집어넣는다
			//test = urlList.contains(url);
			if(urlList.contains(url)) {		// 이미 url이 등록이 되어있는 경우
				isSuccess = indexDAO.InsertRegisterMember(url, contributor);		// 등록이 되어 있는 경우 등록되지 않음
				
				if(isSuccess) {		// 등록이 되어있지 않은 경우 hit수 갱신
					hit = indexDAO.IndexHitModify(url);		// ((기존 hit 수) + 1) - 갱신
				}
				
				idxNumTmp = urlNIdxNumMap.get(url);
				updateIdxList.add(idxNumTmp);		// updateIdx로 등록
				updateIdxNHit.put(idxNumTmp, hit);		// 차후 Index의 hit를 수정하기 위해서 idxNum과 hit을 매칭함
			} else {		// url이 등록이 되어있지 않은 경우
				controller.addSeed(url);
				// seed를 등록한 후에 DB에 Index 데이터를 저장하고 Map을 추가
				// 이 부분이 오래걸린다..... 시간을 줄일 필요가 있을 것 같다
				// 추가가 되었지만(seed) but 크롤링을 못하게 막은 것도 있다...
			}
			
		}

		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		
		controller.start(BasicCrawler.class, numberOfCrawlers);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("BasicCrawlController - err");
			e.printStackTrace();
		} finally {
			indexDAO.close();		// 꼭 닫아주어야 함.....
		}
	}

	public static int getTotalBookmarkSize() {
		return totalBookmarkSize;
	}

	public static ConcurrentHashMap<Integer, IndexDataBean> getNewIdxMap() {
		return newIdxMap;
	}

	public static HashMap<Integer, Integer> getUpdateIdxNHit() {
		return updateIdxNHit;
	}

	public static List<Integer> getNewIdxList() {
		return newIdxList;
	}

	public static ArrayList<Integer> getUpdateIdxList() {
		return updateIdxList;
	}

	public static int getTotalURLListSize() {
		return totalURLListSize;
	}
}