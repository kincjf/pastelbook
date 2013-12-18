<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="engine.index.vo.IndexDataBean"%>
<%@ page import="com.google.gson.*" %>
<%
	Object idb = request.getAttribute("INDEX_RESULT");
	
	Gson gson = new Gson();
	
	out.print(gson.toJson(idb));		// json으로 출력한다.
%>