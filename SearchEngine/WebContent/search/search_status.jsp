<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="engine.search.vo.SearchDataBean"%>
<%@ page import="common.MergeJson" %>
<%@ page import="com.google.gson.*" %>
<%@ page import="org.json.simple.JSONObject"%> 
<%@ page import="java.util.*"%>
<%
	String query = (String) request.getAttribute("QUERY");
	long searchTime = (Long) request.getAttribute("SEARCH_TIME");
	ArrayList<SearchDataBean> searchDataArray = (ArrayList<SearchDataBean>)request.getAttribute("SEARCH_RESULT");
	int totalHit = (Integer) request.getAttribute("SEARCH_RESULT_MAX");
	
	Gson gson = new Gson();
	JSONObject json = new JSONObject();
	json.put("QUERY", query);
	json.put("SEARCH_TIME", searchTime);
 	json.put("SEARCH_RESULT", gson.toJsonTree(searchDataArray));
	json.put("SEARCH_RESULT_MAX", totalHit);
	
	out.print(json.toJSONString());		// json으로 출력한다.
%>