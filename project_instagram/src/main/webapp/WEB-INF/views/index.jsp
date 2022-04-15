<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">

<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Instagram</title>
	<link rel="stylesheet" href="/static/css/container.css">
	<link rel="stylesheet" href="/static/css/index.css">
</head>

<body>
	<div>
		<main class="container">
			<div class="main-image">
				<div class="img-positioner">
					<img src="/static/images/main1.png" class="inner-img" alt="기능 이미지"> <img
						src="/static/images/main2.png" class="inner-img" alt="기능 이미지"> <img
						src="/static/images/main3.png" class="inner-img" alt="기능 이미지"> <img
						src="/static/images/main4.png" class="inner-img" alt="기능 이미지">
				</div>
			</div>
			<div class="main-form">
				<div class="login">
					<h1 class="logo"></h1>
					<form action="/signin" method="post">
						<div>
							<span>전화번호, 사용자 이름 또는 이메일</span> <input type="text"
								name="username" required>
							<div class="input-indicator">
								<span></span>
							</div>
						</div>
						<div>
							<span>비밀번호</span> <input type="password" name="password" required>
							<div class="input-indicator">
								<span></span>
							</div>
						</div>
						<button type="submit" disabled>로그인</button>
					</form>
					<div class="separator">
						<span class="liner"></span> <span class="or">또는</span> <span
							class="liner"></span>
					</div>
					<button type="button" class="facebook-signin">
						<img src="/static/images/login_facebook.png" alt="페이스북 로그인 로고"> <span>Facebook으로
							로그인</span>
					</button>
					<div class="forget-password">
						<a href="#">비밀번호를 잊으셨나요?</a>
					</div>
				</div>
				<div class="to-signup">
					<span>계정이 없으신가요? <a href="/signup">가입하기</a></span>
				</div>
				<div class="download">
					<span>앱을 다운로드하세요.</span>
					<div class="download-buttons">
						<a href="#"> <img src="/static/images/download_applestore.png"
							alt="앱스토어 다운로드">
						</a> <a href="#"> <img src="/static/images/download_playstore.png"
							alt="플레이스토어 다운로드">
						</a>
					</div>
				</div>
			</div>
		</main>
	</div>
	
	<jsp:include page="/WEB-INF/views/templetes/index_footer.jsp" />
	
	<script src="/static/js/inputEvent.js"></script>
	<script src="/static/js/imageEvent.js"></script>
</body>

</html>