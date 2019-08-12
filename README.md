# Municipality_Support

지자체 지원정보 데이터를 관리하는 애플리케이션

## Api 기능

###SupportInfo
- 데이터 파일전송에서 데이터베이스에 저장 (있는 데이터는 업데이트)

    [POST] /api/supportInfo/files

- 지자체 지원정보 목록 검색

    [GET] /api/supportInfo/lists

-  지자체명을 입력 받아 해당 지차체의 지원정보 검색
 
    [GET] /api/supportInfo/infos
  
 - 지자체의 지원정보 수정
    
    [PUT] /api/supportInfo/modified
  
 - 지원한도 컬럼에서 지원금액으로 내림차순, 이차보전 평균비율 오름차순하여 특정 개수만 지자체명 검색
  
    [GET] /api/supportInfo/limitDesc
  
 - 이차보전 컬럼에서 보전 비율이 가장 작은 추천 기관명 검색

    [GET] /api/supportInfo/leastRate
  
 -  특정 기사를 분석하여 지자체정보 추천    
  
    [POST] /api/supportInfo/recommends
 
 ###User
- singup 계정생성
  [POST] /api/user/signup
  
 - signin 로그인
  [POST] /api/user/signin
  
 - refresh 토큰 재발급
  [POST] /api/user/refresh

## 개발환경
- Spring boot
- Java8
- JPA(Hibernate)
- H2 Database (In-Memory)
- Gradle
- JUnit

## 프로젝트 빌드, 실행 방법

Springboot 환경에서 동작하는 프로젝트이며, Application.java에서 Application을 실행하면 동작한다.

- DB Admin Page
http://localhost:8080/console

## 사용 Flow

## 동작방식

## 고려사항
