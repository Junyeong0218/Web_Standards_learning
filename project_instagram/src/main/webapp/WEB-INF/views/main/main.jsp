<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Instagram</title>
    <link rel="shortcut icon" href="/static/images/shortcut.ico" type="image/x-icon">
    <link rel="stylesheet" href="/static/css/container.css">
    <link rel="stylesheet" href="/static/css/main/header.css">
    <link rel="stylesheet" href="/static/css/main/main.css">
    <link rel="stylesheet" href="/static/css/popup.css">
    <link rel="stylesheet" href="/static/css/main/article_detail.css">
    <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script type="text/javascript" src="/static/js/principal.js"></script>
</head>

<body>
    <div class="container">

		<jsp:include page="/WEB-INF/views/templates/new_file_picker.jsp" />

        <jsp:include page="/WEB-INF/views/templates/header.jsp" />

        <main>
            <div class="main-wrapper">
                <div class="article-area">
                    <ul class="story-area">

                    </ul>
                    <div class="articles">

                    </div>
                </div>

                <jsp:include page="/WEB-INF/views/templates/main_aside.jsp" />

            </div>
        </main>
    </div>
    <jsp:include page="/WEB-INF/views/templates/article_menu_popup.jsp"></jsp:include>

    <jsp:include page="/WEB-INF/views/templates/comment_menu_popup.jsp"></jsp:include>
    
    <script src="/static/js/main/searcher_control.js"></script>
    <script src="/static/js/main/profile_menu_control.js"></script>
    <script src="/static/js/main/activity_menu_control.js"></script>
    <script src="/static/js/main/new_file_picker.js"></script>
    <script src="/static/js/main/pop_up_and_window.js"></script>
    <script src="/static/js/main/load_aside_recommend.js"></script>
    <script src="/static/js/main/load_recent_stories.js"></script>
    <script src="/static/js/main/load_article_list.js"></script>
    <script src="/static/js/main/main.js"></script>
</body>

</html>