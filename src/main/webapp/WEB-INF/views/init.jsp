<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<html>
<head>
    <title>微信扫一扫</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/resources/js/jquery-3.2.1.js"></script>
</head>
<body>
    <h1>打开微信扫一扫</h1>
    <img src="${qrCode}">
</body>

<script type="text/javascript">
    $(function(){
       $.ajax({
          type : "GET",
           url : "/wxLogin",
           data : {}
       });
    });
</script>
</html>
