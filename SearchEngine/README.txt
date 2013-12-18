폴더구조
/data - 검색엔진 구동에 필요한 파일 저장
--------------------------------
/data/bookmark		// 브라우저별 bookmark 저장
/data/bookmark/chrome		// 사용자의 chrome bookmark 저장
--------------------------------
/data/docs		// 수집한 text들을 저장
/data/docs/content		// crawling 한 text(HTML Tag 제거) 저장
/data/docs/html		// crawling 한 HTML문서 저장
--------------------------------
/data/indexs		// index된 정보 저장
/data/root		// crawl한 정보 저장 : crawler4j

config.properties - 폴더경로관련 정보 저장

DB DDL 관련 sql만 추가할 것
DML sql을 추가하면 DB와 연동이 되지 않는다
(DB와 검색엔진이 따로 놀 수 있음)