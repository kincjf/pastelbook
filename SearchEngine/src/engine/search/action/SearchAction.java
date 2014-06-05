package engine.search.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import engine.search.SearchEngine;
import engine.search.vo.SearchDataBean;

public class SearchAction implements Action {
	@Override
	public ActionForward execute(HttpServletRequest request,
			HttpServletResponse response) {

		ActionForward forward = new ActionForward();

		ArrayList<SearchDataBean> searchDataArray = null;
		SearchEngine searchEngine = null;

		boolean success = true;
		int totalHit = 0;
		String query = request.getParameter("QUERY");
		int page = Integer.parseInt(request.getParameter("PAGE"));
		long searchTime = 0;
		
		int hitPerPage = 10;
		int multiply = 5;		// 10개 단위로 최대 50개까지 검색할 수 있음
		searchEngine = new SearchEngine(hitPerPage, multiply);

		try {
			searchDataArray = searchEngine.search(query, page);		// 검색엔진
			totalHit = searchEngine.getTotalHits();
			searchTime = searchEngine.getSearchTime();		// 관련 정보 수집
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("SearchAction - SearchEngine.search err");
			e.printStackTrace();
			success = false;
		} 
		
		request.setAttribute("QUERY", query);
		request.setAttribute("SEARCH_TIME", searchTime);
		request.setAttribute("SEARCH_RESULT", searchDataArray);
		request.setAttribute("SEARCH_RESULT_MAX", totalHit);
		
		if(success) {
			System.out.println("SearchAction - Success");
		}
		
		forward.setPath("/searchStatus.sf");

		return forward;
	}
}
