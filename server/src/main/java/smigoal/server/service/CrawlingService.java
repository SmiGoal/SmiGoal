package smigoal.server.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import smigoal.server.service.crawling.WebDriverUtil;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CrawlingService {

    private WebDriver webDriver;

    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String screenShot(String url){
        try{
            webDriver = WebDriverUtil.getChromeDriver();

            if (!ObjectUtils.isEmpty(webDriver)) {
                log.info("screenshot url = {}", url);
                webDriver.manage().window().setSize(new Dimension(1920, 1080));
                webDriver.get(url);
                
                // 스크린샷 글씨 깨짐 방지
                Thread.sleep(3000);

                File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);

                String transUrl = url.replace("http://", "").replace("https://", "");
                String fileKey = "screenshot/"+transUrl+UUID.randomUUID().toString().substring(0, 8)+".png";

                amazonS3Client.putObject(bucket, fileKey, scrFile);

                GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileKey)
                        .withMethod(HttpMethod.GET);

                URL imageUrl = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

                return imageUrl.toString();
            }
            return null;
        }catch (Exception e){
            log.info("getURLContent() 에러 발생", e);
            return null;
        }finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }

    public String getURLContent(String url){
        String crawlingResult = crawling(url);

        if (crawlingResult == null){
            return null;
        }

        int length = crawlingResult.length();

        // GPT에게 전달할 문자열 길이 조절
        if(length > 1000){
            crawlingResult = crawlingResult.substring(length-1000,length);
        }else if(length >= 600){
            crawlingResult = crawlingResult.substring(100);
        }
        return crawlingResult;
    }

    private List<WebElement> findElementSafely(WebDriver driver, By by){
        for (int attempts = 0; attempts < 1; attempts++) {  // 3번의 기회 -> 1번의 기회(성능 때문)
            try {
                return driver.findElements(by);
            } catch (StaleElementReferenceException e) {    // 해당 웹이 동적인 경우 찾는 중간에 요소가 바뀌면 exception 발생
                break;
            }
        }
        return null;    // null : 요소 찾기 실패
    }

    private String getTableContent(WebDriver webDriver){
        List<WebElement> tables = findElementSafely(webDriver, By.cssSelector("table"));
        WebElement depth1Table=null;
        int maxLengthTable = 0;

        if (tables.isEmpty()){
            return null;
        }

        for (WebElement table : tables) {
            int length = table.getText().length();
            if (length > maxLengthTable) {
                depth1Table = table;
                maxLengthTable = length;
            }
        }

        String result;
        if (depth1Table != null){
            result = depth1Table.getText();
            return result;
        }else{
            return null;
        }
    }

    public String crawling(String url) {
        try{
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

                if (divs.isEmpty()){    // div 태그를 찾지 못함 - table을 쓴 경우를 고려하여 td 탐색
                    String tableresult = getTableContent(webDriver);
                    if(tableresult==null) {
                        return null;
                    }else{
                        return tableresult;
                    }
                }

                int maxLength1 = 0;

                for (WebElement div : divs) {
                    int length = div.getText().length();
                    if (length > maxLength1) {
                        depth1Div = div;
                        maxLength1 = length;
                    }
                }

                if(maxLength1<=50){
                    String tableresult = getTableContent(webDriver);
                    if(tableresult==null) {
                        return null;
                    }else if(tableresult.length()>50){
                        return tableresult;
                    }else{
                        return null;
                    }
                }

                // 스크롤 내리기 - depth1div 스크롤 내려서 더 많은 웹페이지내용 가져오게하기
                int SCROLL_PAUSE_TIME = 1000;
                ((JavascriptExecutor)webDriver).executeScript("window.scrollTo(0, document.body.scrollHeight)", depth1Div);
                Thread.sleep(SCROLL_PAUSE_TIME);    // 로딩시간 1초

                List<WebElement> divs2; // 주요 내용을 찾기 위해 깊은 탐색

                log.info("crawling : explore div2");
                divs2 = depth1Div.findElements(By.xpath("./div"));

                log.info("crawling : divs2 = {}", divs2);
                if (!divs2.isEmpty()){
                    int maxLength2 = 0;

                    for (WebElement div : divs2) {
                        log.info("divs2 : div = {}", div);
                        int length=0;
                        length = div.getText().length();
                        if (length > maxLength2) {
                            depth2Div = div;
                            maxLength2 = length;
                        }
                    }

                    int depth = 2;  // 너무 깊이 탐색하면 동적인 웹사이트의 요소가 변해버려서 오류 발생할 수 있음 - 성능 이슈로 5 -> 2로 낮춤

                    for (int i=1;i<depth;i++){
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
            } else if (depth1Div != null){
                result = depth1Div.getText();
            }else{
                return null;
            }

            return result;
        }catch (Exception e){
            log.info("getURLContent() 에러 발생", e);
            return null;
        }finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }

    }
}
