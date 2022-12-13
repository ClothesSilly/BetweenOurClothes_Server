# BetweenOurClothes_Server
너와 내 옷 사이 api 서버입니다.


### version
- Java: 1.8.0_301  
- Spring boot: 2.1.7.RELEASE   
- QueryDSL: '1.0.10'  
- swagger: 2.9.2



### 기능
- **auth-api-controller: 회원가입 및 로그인**
  - 회원가입
  - 이메일 전송/인증
  - 로그인
  - 토큰 재발급
- **closets-api-controller: 내 옷장 관리**
  - 내 옷 등록/삭제/수정/가져오기
  - 게시글 미리보기 가져오기
  - 추천 가져오기
- **stores-api-controller: 중고거래 플랫폼**
  - 게시글 등록/삭제/수정/가져오기
  - 게시글 미리보기 가져오기
  - 찜 가져오기
  - 게시글 키워드 검색
  - 판매상태 업데이트
  - 찜 등록/삭제
  - 댓글 등록/삭제/가져오기
- **main-api-controller: 메인 화면 (추천)**
  - 사용자 맞춤 추천 (top 10)
  - 찜 많은 게시글 가져오기 (top 10)
  - 최신 게시글 가져오기 (top 10)


