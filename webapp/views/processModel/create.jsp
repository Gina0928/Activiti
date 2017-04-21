<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/js/commons.jsp" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>创建模板</title>
</head>
<body>
 	<form action="/processEditor/submit" method="POST">
 		<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
		  <tr>
		    <td height="30"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		      <tr>
		        <td height="24" bgcolor="#353c44"><table width="100%" border="0" cellspacing="0" cellpadding="0">
		          <tr>
		            <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
		              <tr>
		                <td width="6%" height="19" valign="bottom"><div align="center"><img src="/images/tb.gif" width="14" height="14" /></div></td>
		                <td width="94%" valign="bottom">
		                	<span class="STYLE1">
		                			新增/修改模板
		                	</span>
		                </td>
		              </tr>
		            </table></td>
		            <td><div align="right"><span class="STYLE1">
		              </span></div></td>
		          </tr>
		        </table></td>
		      </tr>
		    </table></td>
		  </tr>
		  <tr>
		    <td>
		    	<div align="center" class="STYLE21">
		    		<input type="hidden" name="id" id="modelId"/>
		    		<table>
		    		<tr>
		    		<td>模板名称:</td>
		    		<td><input type="text" name="name" style="width: 200px;"/></td>
		    		</tr>
		    		<tr>
		    		<td>模板KEY:</td>
		    		<td><input type="text" name="key" style="width: 200px;"/></td>
		    		</tr>
		    		<tr>
		    		<td>分类:</td>
		    		<td><textarea name="category" cols="50" rows="5"></textarea></td>
		    		</tr>
		    		<tr>
		    		<td><input type="submit" value="提交" class="button_ok"/></td>
		    		</tr>
			 		</table>
				</div>
		    </td>
		  </tr>
	</table>
	 	
	</form>
</body>
<script>
$(function(){
	if('${bill.id}'){
		$('#modelId').val('${bill.id}');
	}else{
		$('#modelId').val(0);
	}
})
</script>
</html>