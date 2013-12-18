<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>파일 전송 테스트 - 파일 전송 테스트</title>
</head>
<body>
	<h1>입력값을 적어주세요</h1>
	<form action="/indexAction.if"
		enctype="multipart/form-data" method="post">
		<input type="file" name="TEMPLATE_FILE"> 
		<input type="submit" value="전송">
	</form>
</body>
</html>