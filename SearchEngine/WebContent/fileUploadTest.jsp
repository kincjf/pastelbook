<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>파일 전송 테스트 - Bookmark Index 테스트</title>
</head>
<body>
	<h1>Bookmark Indexing Test</h1>
	<h1>Chrome Bookmark와 이름(id)를 적어주세요</h1>
	<form action="http://127.0.0.1:8080/SearchEngine/indexAction.if"
		enctype="multipart/form-data" method="post">
		북마크 : <input type="file" name="TEMPLATE_FILE">
		이름 : <input type="text" name="CONTRIBUTOR">  
		<input type="submit" value="전송">
	</form>
</body>
</html>