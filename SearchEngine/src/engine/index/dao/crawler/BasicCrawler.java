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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.Header;

import common.FileContainer;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import engine.index.vo.IndexDataBean;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class BasicCrawler extends WebCrawler {
	static int idxNum = BasicCrawlController.totalURLListSize;
	// db table 크기 + 1
	private final static Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g"
					+ "|png|tiff?|mid|mp2|mp3|mp4"
					+ "|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& href.startsWith("http://www.ics.uci.edu/");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		String domain = page.getWebURL().getDomain();
		String path = page.getWebURL().getPath();
		String subDomain = page.getWebURL().getSubDomain();
		String anchor = page.getWebURL().getAnchor();
		
		IndexDataBean indexDataBean = null;

		//System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		System.out.println("Domain: '" + domain + "'");
		System.out.println("Sub-domain: '" + subDomain + "'");
		System.out.println("Path: '" + path + "'");
		//System.out.println("Parent page: " + parentUrl);
		System.out.println("Anchor text: " + anchor);

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText(); // HTML을 제거한 text 형태
			String html = htmlParseData.getHtml(); // HTML 그대로
			String title = htmlParseData.getTitle(); // <title>에 있는 내용 (제목)
			List<WebURL> links = htmlParseData.getOutgoingUrls(); // 밖으로 나가는 링크
																	// 수

			System.out.println("Text length: " + text.length());
			System.out.println("Html length: " + html.length());
			System.out.println("Number of outgoing links: " + links.size());

			addIdxNum(); // ++연산에 대한 동기화를 시켜서 idxNum을 할당할 수 있도록 한다.

			indexDataBean = new IndexDataBean();

			Date dt = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String nowDate = sdf.format(dt).toString();

			indexDataBean.setHit(1);
			indexDataBean.setIdxNum(idxNum);
			indexDataBean.setTitle(title);
			indexDataBean.setUrl(url);
			indexDataBean.setModifiedDate(nowDate);
			indexDataBean.setRegisteredDate(nowDate);

			FileContainer.SaveFile(html, text, idxNum);

			BasicCrawlController.indexDAO.InsertIndexData(indexDataBean);
			BasicCrawlController.indexDAO.InsertRegisterMember(url,
					BasicCrawlController.contributor); // 북마크를 제공한 사람에 대해서 DB에 추가함
			
			BasicCrawlController.getNewIdxList().add(idxNum);
			BasicCrawlController.getNewIdxMap().put(idxNum, indexDataBean);
			// thread-safe 때문에 Synchronized, concurrent Container 이용

		}

		Header[] responseHeaders = page.getFetchResponseHeaders();
		if (responseHeaders != null) {
			System.out.println("=============");
			System.out.println("Response headers:");
			for (Header header : responseHeaders) {
				System.out.println("\t" + header.getName() + ": "
						+ header.getValue());
			}
		}

		System.out.println("=============");
	}

	public static synchronized void addIdxNum() {
		++idxNum;
	}
}