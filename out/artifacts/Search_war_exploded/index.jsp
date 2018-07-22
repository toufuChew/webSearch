<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <link rel="stylesheet" type="text/css" href="css/semantic.min.css">
  <script type="text/javascript" src="js/jquery-3.2.1.js"></script>
  <title>傅神一下</title>
</head>
<div style="text-align: center; margin-top: 150px;">
  <div>
    <img src="img/logo.png">
  </div>
  <form class="ui input" action="search?p=1&&has=T" method="GET">
    <input type="text" placeholder="使用傅神进行搜索" size="75" name="key">
    <input name="p" value="1" hidden>
    <input name="has" value="T" hidden>
    <button class="ui button">球哥一下</button>
  </form>
</div>
<script>
    $(function() {
        $("input").focus(function() {
            $(this).attr("placeholder", "");
        })
        $("input").blur(function() {
            if($(this).val() == "") {
                $(this).attr("placeholder", "使用傅神进行搜索");
            }
        });
        $("button").click(function() {
            if($("input").val() == "") {
                return false;
            }
            return true;
        });
    });

</script>
<body>
</body>
</html>
