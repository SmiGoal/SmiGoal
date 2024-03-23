package smigoal.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class URLCheckService {

    public List<String> getWebpageURL(String[] urls){
        List<String> webpages = new ArrayList<>();

        for (int i=0;i<urls.length;i++){
            if (checkURL(urls[i])){
                webpages.add(urls[i]);
                log.info("urlcheck : {}", urls[i]);
            }
        }

        return webpages;
    }

    private boolean checkURL(String checkurl){
        // 웹페이지 url이면 true, 나머지는 false 반환

        try {
            URL url = new URL(checkurl);

            if (!"http".equals(url.getProtocol()) && !"https".equals(url.getProtocol())){
                return false;
            }

            // HttpURLConnection 인스턴스 생성
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // HEAD 요청 방식 설정
            connection.setRequestMethod("HEAD");
            connection.connect();

            // 응답 코드 확인 (404 등의 에러 처리)
            int responseCode = connection.getResponseCode();
            if (responseCode >= 400) {
                // HEAD 요청 실패 시 GET 요청으로 대체
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                responseCode = connection.getResponseCode();
            }

            if (responseCode >= 200 && responseCode < 300) {
                // Content-Type 헤더 확인
                String contentType = connection.getContentType();

                // Content-Disposition 헤더 확인
                String contentDisposition = connection.getHeaderField("Content-Disposition");

                if (contentType != null && contentType.contains("text/html")) {

                    if (contentDisposition == null) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }catch (Exception e){
            log.info("error in checkURL() : {}", e);
            return false;
        }
    }
}
