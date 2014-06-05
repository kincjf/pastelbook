package engine.search;

/*
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import engine.search.vo.SearchDataBean;

public class SearchEngine {
	
	/**
	 * @param args
	 */
	int totalHits = 0;;
	int hitsPerPage = 10;		// 기본 10, 최대 50
	/** Simple command-line based search demo. */
	long searchTime = 0;
	int multiply = 5;
	
	public SearchEngine(int hitPerPage, int multiply) {
		this.hitsPerPage = hitPerPage;
		this.multiply = multiply;
		// 페이지 뷰 갯수 지정, 단위 검색 갯수 지정 ( 설정량의 [multiply]배)
	}
	
	public SearchEngine() {
	}

	/**
	 * Simple command-line based search demo.
	 * 
	 * @return
	 */
	public ArrayList<SearchDataBean> search(String inputQuery, int page) throws Exception {		// hitPerPage(단위) 만큼 잘라서 출력한다.
		// String usage = "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
		/*
		 * if (args.length > 0 && ("-h".equals(args[0]) ||
		 * "-help".equals(args[0]))) { System.out.println(usage);
		 * System.exit(0); }
		 */

		String index = "/data/indexs"; // index dir
		String field = "contents"; // 검색할 필드 지정 (url, title, contents....)
		// the "title" arg specifies the default fieuxld to use
		// when no field is explicitly specified in the query.
		String queries = null; // query using file
		int repeat = 0;
		boolean raw = false;
		String queryString = inputQuery; // queries using string

		Date searchStartTime = new Date();		// 시간 측정을 위해서 시작 시간 측정
	
		ArrayList<SearchDataBean> searchDataArray = new ArrayList<SearchDataBean>();
		/*
		 * for (int i = 0; i < args.length; i++) { if ("-index".equals(args[i]))
		 * { index = args[i + 1]; // index 폴더 지정 i++; } else if
		 * ("-field".equals(args[i])) { field = args[i + 1]; // 상세 필드 지정 i++; }
		 * else if ("-queries".equals(args[i])) { queries = args[i + 1]; //
		 * query를 파일로 읽는다 i++; } else if ("-query".equals(args[i])) {
		 * queryString = args[i + 1]; // query를 String으로 읽는다 i++; } else if
		 * ("-repeat".equals(args[i])) { repeat = Integer.parseInt(args[i + 1]);
		 * // 벤치마크를 위한 repeat 지정 i++; } else if ("-raw".equals(args[i])) { raw =
		 * true; // raw data 검색 가능 } else if ("-paging".equals(args[i])) {
		 * hitsPerPage = Integer.parseInt(args[i + 1]); // 페이지 뷰의 갯수 if
		 * (hitsPerPage <= 0) { System.err
		 * .println("There must be at least 1 hit per page."); System.exit(1); }
		 * i++; } }
		 */// 개발자 지정, 사용자가 수정할 수 없게 할 예정임

		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);

		BufferedReader in = null;
		if (queries != null) {		// file을 이용한 query
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					queries), "UTF-8"));
		} else {
			in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		}
		
		QueryParser parser = new QueryParser(Version.LUCENE_40, field, analyzer);
		while (true) {

			String line = queryString != null ? queryString : "hit-it";
			// query가 안들어오면 수행하지 않음 - keyword hitit 검색
			if (line == null || line.length() == -1) {
				break;
			}

			line = line.trim(); // 양쪽 공백 제거 -> 빈 문자열 제거( 클라이언트 상에서도 가능)
			if (line.length() == 0) {
				break;
			}

			Query query = parser.parse(line);
			System.out.println("Searching for: " + query.toString(field));

			if (repeat > 0) { // repeat & time as benchmark
				Date start = new Date();
				for (int i = 0; i < repeat; i++) {
					searcher.search(query, null, 100);
				}
				Date end = new Date();
				System.out.println("Time: " + (end.getTime() - start.getTime())
						+ "ms");
			}

			totalHits = doPagingSearch(in, searcher, query, hitsPerPage, raw,
					queries == null && queryString == null, searchDataArray, page);
			// interactive - 상호작용?? => query를 수정하면서 질의검색이 가능한가?
			// 일단은 불가한걸로 지정
			if (queryString != null) {
				break;
			}
		}
		reader.close();

		Date searchEndTime = new Date();
		
		System.out.println("Query : " + queryString);
		
		searchTime = (searchEndTime.getTime() - searchStartTime.getTime());
		System.out.println("SearchEngine - Time : " + searchTime + "ms");
		
		return searchDataArray;
	}

	/**
	 * This demonstrates a typical paging search scenario, where the search
	 * engine presents pages of size n to the user. The user can then go to the
	 * next page if interested in the next hits.
	 * 
	 * When the query is executed for the first time, then only enough results
	 * are collected to fill 5 result pages. If the user wants to page beyond
	 * this limit, then the query is executed another time and all hits are
	 * collected.
	 * 
	 */
	public int doPagingSearch(BufferedReader in, IndexSearcher searcher,
			Query query, int hitsPerPage, boolean raw, boolean interactive,
			ArrayList<SearchDataBean> searchDataArray, int page) throws IOException {
		// 제한 페이지 - [multiply]까지
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, multiply * hitsPerPage); 
		// [multiply] * 페이지수만큼 검색한다.
		ScoreDoc[] hits = results.scoreDocs;
		// 점수순으로 내림차순 정렬해서 보여준다.

		int numTotalHits = results.totalHits;
		System.out.println(numTotalHits + " total matching documents");

		//int page = 1;		// 기본 1, page를 이용해서 페이지뷰를 조절한다.
		
		int start = Math.max(0, (page - 1) * hitsPerPage);
		int end = Math.min(numTotalHits, start + hitsPerPage);
		
		SearchDataBean searchDataBean = null;

		while (true) {
			/*
			 * if (end > hits.length) { System.out // 하나만 검색되었을 경우
			 * .println("Only results 1 - " + hits.length + " of " +
			 * numTotalHits + " total matching documents collected.");
			 * System.out.println("Collect more (y/n) ?"); String line =
			 * in.readLine(); if (line.length() == 0 || line.charAt(0) == 'n') {
			 * break; }
			 * 
			 * hits = searcher.search(query, numTotalHits).scoreDocs; }
			 */// 하나든 여러개든 한번 검색하고 연결 종료

			for (int i = start; i < end; i++) { // start, end를 조정해서 페이지뷰 갯수를 조정할
												// 수 있다.
				if (raw) { // output raw format
					System.out.println("doc=" + hits[i].doc + " score="
							+ hits[i].score);
					continue;
				}

				Document doc = searcher.doc(hits[i].doc);		// 순위가 매겨진 hit(i)의 문서
				// String path = doc.get("path");

				String title = doc.get("title");
				if (title != null) {
					String url = doc.get("url");
					if (url != null) {
						System.out.println("   Title: " + title);
						System.out.println("   URL: " + url);
						searchDataBean = new SearchDataBean();
						
						searchDataBean.setTitle(title);
						searchDataBean.setUrl(url);

						searchDataArray.add(searchDataBean);
					}
				} else {
					System.out.println((i + 1) + ". "
							+ "No title for this document");
				}

			}

			if (!interactive || end == 0) {
				break;
			}

		}
		return numTotalHits; // 총 hit 수를 반환한다.
	}

	/**
	 * @return the totalHits
	 */
	public final int getTotalHits() {
		return totalHits;
	}

	/**
	 * @return the hitsPerPage
	 */
	public final int getHitsPerPage() {
		return hitsPerPage;
	}

	public long getSearchTime() {
		// TODO Auto-generated method stub
		return searchTime;
	}
}
