package smigoal.server.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import smigoal.server.service.crawling.WebDriverUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlingService {

    private WebDriver webDriver;

    private List<WebElement> findElementSafely(WebDriver driver, By by){
        for (int attempts = 0; attempts < 3; attempts++) {  // 3번의 기회
            try {
                return driver.findElements(by);
            } catch (StaleElementReferenceException e) {    // 해당 웹이 동적인 경우 찾는 중간에 요소가 바뀌면 exception 발생
                if (attempts == 2) { // 마지막 시도에서도 실패한 경우
                    break;
                }
            }
        }
        return null;    // null : 요소 찾기 실패
    }

    public String getURLContent(String url) throws InterruptedException {
        webDriver = WebDriverUtil.getChromeDriver();
        WebElement depth1Div=null;
        WebElement depth2Div=null;

        if (!ObjectUtils.isEmpty(webDriver)){
            webDriver.get(url);
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            List<WebElement> divs = findElementSafely(webDriver, By.cssSelector("body > div"));
            // div가 가장 범용적인것 같지만 div를 사용하지 않는 웹사이트의 경우 문제가 생길 수 있음

            if (divs.isEmpty()) {    // 최상위에 div가 없을 때
                divs = findElementSafely(webDriver, By.tagName("div"));
            }

            if (divs.isEmpty()){    // div 태그를 찾지 못함
                if (webDriver != null) {
                    webDriver.quit();
                }
                return null;
            }

            int maxLength1 = 0;

            for (WebElement div : divs) {
                int length = div.getText().length();
                if (length > maxLength1) {
                    depth1Div = div;
                    maxLength1 = length;
                }
            }

//             무한 스크롤 - depth1Div에 대해 스크롤 끝까지 내리기
            int SCROLL_PAUSE_TIME = 1500;
            System.out.println("!!!");
            var stTime = new Date().getTime(); //현재시간
            while (new Date().getTime() < stTime + 10000) { // 10초 동안 무한스크롤 지속
                Thread.sleep(SCROLL_PAUSE_TIME); //리소스 초과 방지
                //executeScript: 해당 페이지에 JavaScript 명령을 보내는 거
                ((JavascriptExecutor)webDriver).executeScript("window.scrollTo(0, document.body.scrollHeight)", depth1Div);
            }

            List<WebElement> divs2; // 주요 내용을 찾기 위해 깊은 탐색

            System.out.println("----------------------------------div2 탐색");
            divs2 = depth1Div.findElements(By.xpath("./div"));

            System.out.println(divs2);
            if (!divs2.isEmpty()){
                int maxLength2 = 0;

                for (WebElement div : divs2) {
                    System.out.println(div);
                    int length=0;
                    length = div.getText().length();
                    if (length > maxLength2) {
                        depth2Div = div;
                        maxLength2 = length;
                    }
                }

                int depth = 5;  // 너무 깊이 탐색하면 동적인 웹사이트의 요소가 변해버려서 오류 발생할 수 있음

                for (int i=1;i<depth;i++){
                    System.out.println("!!!@@#@#@!@#!@");
                    System.out.println(maxLength2);
                    maxLength1=maxLength2;
                    maxLength2=0;
                    divs2 = depth2Div.findElements(By.xpath("./div"));
                    if (divs2.isEmpty())
                        break;

                    for (WebElement div : divs2) {
                        int length = div.getText().length();
                        if (length > maxLength2) {
                            depth2Div = div;
                            maxLength2 = length;
                        }
                    }

                    if (maxLength2<=maxLength1*0.95 || maxLength2 < 1000)
                        break;
                }
            }

        }

        String result;
        if (depth2Div != null) {
            result = depth2Div.getText();
            if (webDriver != null) {
                webDriver.quit();
            }
        } else if (depth1Div != null){
            result = depth1Div.getText();
            if (webDriver != null) {
                webDriver.quit();
            }
        }else{
            if (webDriver != null) {
                webDriver.quit();
            }
            return null;
        }

        // 앞부분 자르기
        if(result.length() >= 600){
            result = result.substring(100);
        }
        return result;
    }
}
