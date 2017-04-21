<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Import Model</title>
</head>
<body>
<form action="/processEditor/import/submit" enctype="multipart/form-data" method="POST">
	<div align="center" class="STYLE21">
		流程名称：<input type="text" name="filename" cssStyle="width: 200px;" /><br />
		流程文件:<input type="file" name="file" cssStyle="width: 200px;" /><br />
		<input type="submit" value="上传流程" class="button_ok" />
	</div>
</form>
</body>
</html>