<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Query Search Test</title>
</head>
<body>
	<h1>Query Search Test - 등록한 북마크를 검색할 수 있습니다.</h1>
	<form action="http://127.0.0.1:8080/SearchEngine/searchAction.sf" method="get">
		Query : <input type="text" name="QUERY">
		Page : <input type="text" name="PAGE">
		<input type="submit" value="전송">
	</form>
</body>
</html>