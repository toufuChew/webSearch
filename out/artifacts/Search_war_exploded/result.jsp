<%@ page import="java.io.*, java.util.*"%>
<%@ page import="SearchServlet.Page" %>
<%@ page import="Main.JSPMessage" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    Page pageC = (Page) request.getAttribute("page");
    ArrayList<JSPMessage> results = (ArrayList<JSPMessage>) request.getAttribute("messageList");
    String key = (String) request.getAttribute("key");
    double costTime = (double) request.getAttribute("time");
    int urlNum = pageC.getTotalMessageNum();
    int pageId = pageC.getPage();
    int leftRange = pageId - 4;
    if(leftRange < 1) leftRange = 1;
    int rightRange = leftRange + 9;
    int pageNum = pageC.getTotalPage();
    if(rightRange > pageNum) {
        rightRange = pageNum;
    }
    String correct = (String) request.getAttribute("correct");
//    System.out.println("key:" + key);
%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>搜索页面</title>
    <link rel="stylesheet" type="text/css" href="css/semantic.min.css">
    <script type="text/javascript" src="js/jquery-3.2.1.js"></script>
    <style>
        B {
            color: red;
        }
    </style>
</head>
<body>
<div style="display: flex;">
    <a href="index.jsp"><img src="img/logo.png" style="width: 150px;height: 75px;"></a>
    <form class="ui action input" style="height: 40px; margin-top: 20px; margin-left: 10px;" action="search?key=<%=key%>" method="GET" id="searchForm">
        <input type="text" placeholder="使用球哥进行搜索" style="width: 600px;" name="key" value="<%=key%>" id="keyInput">
        <input type="text" name="p" value="1" id="pageIdInput" hidden>
        <input type="text" name="has" value="T" id="hasInput" hidden>
        <button class="ui icon button" id="searchbutton">搜索</button>
    </form>
</div>
<div class="ui secondary pointing menu" style="background-color: #F0F0F0;">
    <a class="active item" style="margin-left: 165px;">网页</a>
    <a class="item">图片</a>
    <a class="item">视频</a>
    <a class="item">新闻</a>
    <a class="item">地图</a>
    <a class="item">更多</a>
</div>
<div>
    <p style="color: #C9C6C6;margin-left: 165px;margin-bottom: 25px;">球哥为您找到相关结果为<%=urlNum%>个,用时<%=costTime%>秒</p>
    <%
      if (correct != null && !correct.equals("")) {
    %>
        <h4 style="margin-left: 165px;margin-bottom: 25px;">您是否想查询的是<a id="correctHref"><%=correct%></a></h4>
    <%
      }
    %>
</div>

<div class="center" style="margin-left: 165px;">

    <%--<div style="margin-bottom: 25px;">--%>
    <%--<h3 class="ui header" style="color: #0000FF; margin-bottom: 0px;"><a href="http://www.szu.edu.cn">深圳大学</a></h3>--%>
    <%--<p style="color: green; margin-bottom: 0px;">www.szu.edu.cn</p>--%>
    <%--<div>--%>
    <%--<p style="color: grey; max-width: 600px;">深圳大学是深圳市的一所特色大学。。</p>--%>
    <%--</div>--%>
    <%--</div>--%>

    <div class="content">
        <%
            if(results != null)
            for(JSPMessage result : results) {
        %>
        <div style="margin-bottom: 25px;">
            <h3 class="ui header" style="color: #0000FF; margin-bottom: 0px;">
                <a href="<%=result.getUrl()%>"><%=result.getTitle()%></a>
            </h3>
            <div><p style="color: grey; max-width: 600px;"><%=result.getContent().length() > 200 ?
                    result.getContent().substring(0, 200) : result.getContent()%></p></div>
            <p style="color: green; margin-bottom: 0px"><a href="<%=result.getUrl()%>"><%=result.getUrl()%></a></p>
        </div>
        <%
            }
        %>
    </div>

    <div class="page">
        <%
            if(pageId > 1) {
        %>
        <button class="ui button" id="prevBtn">上一页</button>
        <%}
            for(int var = leftRange; var <= rightRange; var = var + 1) {
                if(var == pageId) {
        %>
        <button class="ui primary button pageBtn" id="<%=var%>"><%=var%></button>
        <%
        } else {
        %>
        <button class="ui button pageBtn" id="<%=var%>"><%=var%></button>
        <%
                }
            }
            if(pageId != pageNum && urlNum != 0) {
        %>
        <button class="ui button" id="nextBtn">下一页</button>
        <%}
        %>
    </div>
</div>

<div style="background-color: #F0F0F0; margin-top: 50px;">
    <div style="margin-left: 165px; color: #A0A0A0;">
        <p>by 球哥</p>
        <p>详情请咨询微信号:C_Qiu</p>
    </div>
</div>

<script>

    $(function() {
        var $clickObject = null;

        function searchEvent() {
            if($clickObject == "pageBtn") {
                $clickObject = null;
                return true;
            } else if($("#keyInput").val() == "") {
                $("#searchForm").attr("action", "index.jsp");
                return true;
            } else if($("#keyInput").val() != "<%=key%>") {
                $("#searchForm").attr("action", "search?key=<%=key%>");
                $("#pageIdInput").val("1");
                $("#hasInput").val("T");
                return true;
            } else if(<%=pageId != 1%>){
                $("#searchForm").attr("action", "search?key=<%=key%>");
                $("#pageIdInput").val("1");
                $("#hasInput").val("F");
                return true;
            } else {
                return false;
            }
        }

        $("#keyInput").focus(function() {
            $(this).attr("placeholder", "");
        })

        $("#keyInput").blur(function() {
            if($(this).val() == "") {
                $(this).attr("placeholder", "使用球哥进行搜索");
            }
        });

        $("#searchbutton").click(function() {
            return searchEvent();
        });

        $("#keyInput").bind("keypress", function(event) {
            if(event.keyCode == "13") {
                return searchEvent();
            }
        })

        $(".pageBtn").click(function() {
            $id = $(this).attr("id");
            if($id == "<%=pageId%>") {
                return false;
            }
            $("#searchForm").attr("action", "search?key=<%=key%>");
            $("#pageIdInput").val($id);
            $("#hasInput").val("F");
            $clickObject = "pageBtn";
            return $("#searchbutton").click();
        })

        $("#prevBtn").click(function() {
            $("#<%=pageId - 1%>").click();
        })

        $("#nextBtn").click(function() {
            $("#<%=pageId + 1%>").click();
        })

        $("#correctHref").click(function() {
//            alert("click");
            $("#keyInput").val("<%=correct%>");
            $("#searchForm").attr("action", "search?key=<%=correct%>");
            $("#pageIdInput").val("1");
            $("#hasInput").val("T");
            <%--alert("<%=correct%>");--%>
            $clickObject = "pageBtn";
            return $("#searchbutton").click();
        })

    });

</script>
</body>
</html>
