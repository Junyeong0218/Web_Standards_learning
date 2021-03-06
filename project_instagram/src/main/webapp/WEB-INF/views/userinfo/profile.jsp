<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
    <link rel="stylesheet" href="/static/css/short_footer.css">
    <link rel="stylesheet" href="/static/css/popup.css">
    <link rel="stylesheet" href="/static/css/main/article_detail.css">
    <link rel="stylesheet" href="/static/css/main/main.css">
    <link rel="stylesheet" href="/static/css/userinfo/profile.css">
    <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script type="text/javascript" src="/static/js/principal.js"></script>
</head>

<body>

    <div class="container">

        <jsp:include page="/WEB-INF/views/templates/header.jsp" />

        <main>
            <div class="main-wrapper">
                <div class="user-summary">
                    <img src="/static/images/profile_background.png" class="profile-background" alt="">
                    	<img src="/static/images/basic_profile_image.jpg" class="profile-image" alt="유저 프로필 이미지">
                    <div class="userinfo">
                        <div class="menu-buttons">
                            
                            
                            
                            
                        </div>
                        <div class="follow-info">
                            <span>게시물 <span class="article-quantity"></span></span>
                            <button type="button">팔로워 <span class="follower"></span></button>
                            <button type="button">팔로우 <span class="following"></span></button>
                        </div>
                        <span class="user-name"></span>
                    </div>
                </div>
                <div class="tab-buttons">
                    <button type="button" class="article-tab active">
                        <span class="button-image"></span>
                        <span class="tab-button-text">게시물</span>
                    </button>
                    
                    <button type="button" class="collected-tab">
                        <span class="button-image"></span>
                        <span class="tab-button-text">저장됨</span>
                    </button>
                    
                    <button type="button" class="tagged-tab">
                        <span class="button-image"></span>
                        <span class="tab-button-text">태그됨</span>
                    </button>
                </div>
                <div class="contents-wrapper">
                    <div class="articles active">
                    	
                    </div>
                    <div class="collected">
                        <div class="no-contents">
                            <img src="/static/images/profile_no_contents_background.jpg" alt="">
                            <div class="recommend-to-add">
                                <span class="bold-text">소중한 순간을 포착하여 공유해보세요.</span>
                                <span class="common-text">앱을 다운로드하고 첫 사진이나 동영상을 공유해보세요.</span>
                                <div class="download-app-buttons">
                                    <a href="#" class="appstore-download"><img
                                            src="/static/images/download_applestore.png" alt=""></a>
                                    <a href="#" class="googleplay-download"><img
                                            src="/static/images/download_playstore.png" alt=""></a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="tagged">
                        <div class="no-contents">
                            <img src="/static/images/profile_no_contents_background.jpg" alt="">
                            <div class="recommend-to-add">
                                <span class="bold-text">소중한 순간을 포착하여 공유해보세요.</span>
                                <span class="common-text">앱을 다운로드하고 첫 사진이나 동영상을 공유해보세요.</span>
                                <div class="download-app-buttons">
                                    <a href="#" class="appstore-download"><img
                                            src="/static/images/download_applestore.png" alt=""></a>
                                    <a href="#" class="googleplay-download"><img
                                            src="/static/images/download_playstore.png" alt=""></a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        
        <jsp:include page="/WEB-INF/views/templates/new_file_picker.jsp"></jsp:include>

		<jsp:include page="/WEB-INF/views/templates/update_article_form.jsp"></jsp:include>

        <jsp:include page="/WEB-INF/views/templates/short_footer.jsp" />

		<div id=article-menu-popup class="pop-up">
			<div class="account-control">
				<button id="change-article-content" class="article-control-button">
                    <span>게시글 수정</span>
                </button>
                <button id="delete-article" class="article-control-button red">
                    <span>게시글 삭제</span>
                </button>
                <button class="article-control-button red">
                    <span>취소</span>
                </button>
			</div>
		</div>
        <div id="account-menus" class="pop-up">
            <div class="account-control">
                <button id="change-password" class="account-control-button">
                    <span>비밀번호 변경</span>
                </button>
                <button id="qr-code" class="account-control-button">
                    <span>QR 코드</span>
                </button>
                <button id="website" class="account-control-button">
                    <span>앱 및 웹사이트</span>
                </button>
                <button id="alert" class="account-control-button">
                    <span>알림</span>
                </button>
                <button id="privacy" class="account-control-button">
                    <span>개인정보 및 보안</span>
                </button>
                <button id="activity" class="account-control-button">
                    <span>로그인 활동</span>
                </button>
                <button id="email-from-instagram" class="account-control-button">
                    <span>Instagram에서 보낸 이메일</span>
                </button>
                <button id="report-problem" class="account-control-button">
                    <span>문제 신고</span>
                </button>
                <button id="logout" class="account-control-button">
                    <span>로그아웃</span>
                </button>
                <button id="pop-up-closer" class="account-control-button">
                    <span>취소</span>
                </button>
            </div>
        </div>
        <div id="follow-info" class="pop-up">
            <div class="follow-description">
				
            </div>
        </div>
    </div>
   	<script src="/static/js/main/searcher_control.js"></script>
	<script src="/static/js/main/profile_menu_control.js"></script>
    <script src="/static/js/main/activity_menu_control.js"></script>
    <script src="/static/js/main/new_file_picker.js"></script>
    <script src="/static/js/main/load_article_list.js"></script>
	<script src="/static/js/userinfo/profile/profile.js"></script>
    <script src="/static/js/userinfo/profile/tab_control.js"></script>
    <script src="/static/js/userinfo/profile/pop_up_control.js"></script>
</body>

</html>